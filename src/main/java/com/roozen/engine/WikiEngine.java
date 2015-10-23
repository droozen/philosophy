package com.roozen.engine;

import com.roozen.model.WikiNode;
import com.roozen.model.WikiUrl;
import com.roozen.services.PropertiesService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class WikiEngine {

    private String url;
    private ArrayDeque<WikiNode> path;
    private HashSet<String> visitedUrls;
    private int numDeadEnds = 0;

    public WikiEngine(String url) {
        this.url = url;
        this.path = new ArrayDeque<>();
        this.visitedUrls = new HashSet<>();
    }

    public void run() throws IOException {
        if (path.isEmpty() == false) {
            throw new RuntimeException("Cannot execute the same engine twice. Please instantiate new Wiki search engine.");
        } else if (!isWikipediaUrl(url)) {
            throw new RuntimeException("Cannot run engine on a non-Wikipedia link.");
        }

        String nextUrl = url;
        while (nextUrl != null) {
            visitedUrls.add(nextUrl);
            String currentUrl = getNavigableUrl(nextUrl);
            Document doc = Jsoup.connect(currentUrl).get(); // Find Page
            path.add(new WikiNode(currentUrl, findTitle(doc))); // Add to Stack

            System.out.println(path.peekLast().toString());
            if (haveReachedDestination(currentUrl)) {
                break;
            }

            nextUrl = followLink(doc, currentUrl); // Next Page
            nextUrl = loopAndDeadEndDetection(nextUrl, currentUrl);

            if (path.size() >= PropertiesService.getMaxHops()) {
                break;
            }
        }
    }

    private static boolean isWikipediaUrl(String url) {
        return url.startsWith(PropertiesService.getWikiBase()) || url.startsWith(PropertiesService.getWikiDomain());
    }

    private String loopAndDeadEndDetection(String nextUrl, String currentUrl) throws IOException {
        if (haveDetectedDeadEnd(nextUrl) || haveDetectedLoop(nextUrl)) {
            System.out.println("Detected dead end or loop at: " + currentUrl + " -> " + nextUrl);
            if (numDeadEnds >= PropertiesService.getNumDeadEnds()) {
                processFinalLink(nextUrl, currentUrl);
                return null;
            }

            numDeadEnds++;

            // Look for second link
            currentUrl = getNavigableUrl(path.peekLast().getUrl());
            Document doc = Jsoup.connect(currentUrl).get();
            nextUrl = followLinkAtLevel(doc, currentUrl, numDeadEnds);
        }
        return nextUrl;
    }

    private boolean haveDetectedDeadEnd(String nextUrl) {
        return nextUrl == null;
    }

    private void processFinalLink(String nextUrl, String currentUrl) throws IOException {
        String loopUrl = getNavigableUrl(nextUrl);
        // Add the duplicated url first so we know which page we hit again.
        path.add(new WikiNode(currentUrl, findTitle(Jsoup.connect(loopUrl).get())));
        path.add(new WikiNode("/loop", "Loop Detected"));
    }

    private boolean haveDetectedLoop(String nextUrl) {
        if (visitedUrls.contains(nextUrl) || visitedUrls.contains(PropertiesService.getWikiBase() + nextUrl)) {
            return true;
        }

        return false;
    }

    private String getNavigableUrl(String nextUrl) {
        if (nextUrl.startsWith(PropertiesService.getWikiBase())) {
            nextUrl = PropertiesService.getWikiDomain() + nextUrl;
        }

        return nextUrl;
    }

    private boolean haveReachedDestination(String nextUrl) {
        return nextUrl.equals(PropertiesService.getWikiDestination()) || nextUrl.equals(PropertiesService.getWikiFullDestination());
    }

    private String findTitle(Document page) throws IOException {
        Element firstHeading = page.getElementById("firstHeading");
        return firstHeading.html();
    }

    private String followLink(Document page, String currentUrl) {
        return followLinkAtLevel(page, currentUrl, 0);
    }

    private String followLinkAtLevel(Document page, String currentUrl, int level) {
        Elements paragraphs = page.getElementById("mw-content-text").select("p");

        Iterator<Element> paragraphIter = paragraphs.iterator();
        while (paragraphIter.hasNext()) {
            Element paragraph = paragraphIter.next();
            String linkToFollow = searchForLinks(paragraph, currentUrl, level);
            if (linkToFollow != null) return linkToFollow;
        }

        return null;
    }

    /**
     * Find the Xth link, where X is the value of level passed in.
     * The level argument allows us to back off from a dead end and try a different path.
     *
     * @param paragraph
     * @param currentUrl
     * @param level
     * @return
     */
    private String searchForLinks(Element paragraph, String currentUrl, int level) {
        WikiUrl url = new WikiUrl(currentUrl);
        Elements links = paragraph.select("a");

        int currentLevel = 0;
        Iterator<Element> iterator = links.iterator();
        while (iterator.hasNext()) {
            Element link = iterator.next();
            WikiUrl linkToFollow = new WikiUrl(link.attr("href"));

            if (isSamePageAsCurrent(url, linkToFollow)) {
                continue;
            } else if (!isWikipediaArticle(linkToFollow)) {
                continue;
            } else if (currentLevel < level) {
                currentLevel++;
                continue;
            } else {
                return linkToFollow.getUrl();
            }
        }
        return null; // Dead End
    }

    private boolean isWikipediaArticle(WikiUrl linkToFollow) {
        return linkToFollow.getType() == WikiUrl.UrlType.ARTICLE;
    }

    private boolean isSamePageAsCurrent(WikiUrl url, WikiUrl linkToFollow) {
        return url.matches(linkToFollow);
    }

    /**
     * Retrieve the final path this engine took.
     *
     * @return
     */
    public Collection<WikiNode> getResults() {
        return path.clone();

    }
}
