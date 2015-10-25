package com.roozen.engine;

import com.roozen.model.WikiNode;
import com.roozen.model.WikiUrl;
import com.roozen.services.PropertiesService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;

public class WikiParserComponent {

    private String currentUrl;

    private Document currentPage;
    private WikiNode currentNode;

    public void setStartingUrl(String url) {
        if (currentUrl != null) throw new RuntimeException("We can only set starting url once");
        this.currentUrl = url;
    }

    /**
     * Must be called to parse next node in the path.
     *
     * @return WikiNode representing the requested url
     * @throws IOException
     */
    public WikiNode parseNode() throws IOException {
        if (currentUrl == null) return null;

        getCurrentPage(currentUrl);

        String navigableUrl = getNavigableUrl(currentUrl);
        String title = findTitle(currentPage);

        currentNode = new WikiNode(navigableUrl, title);
        return currentNode;
    }

    public void followLink(int level) throws IOException {
        if (currentPage == null) getCurrentPage(currentUrl); // For first link
        currentUrl = followLinkAtLevel(currentPage, currentUrl, level);
    }

    private String followLinkAtLevel(Document page, String currentUrl, int level) throws IOException {
        Elements paragraphs = page.getElementById("mw-content-text").select("p");

        Iterator<Element> paragraphIter = paragraphs.iterator();
        while (paragraphIter.hasNext()) {
            Element paragraph = paragraphIter.next();
            String linkToFollow = searchForLinks(paragraph, currentUrl, level);
            if (linkToFollow != null) return linkToFollow;
        }

        return null;
    }

    private void getCurrentPage(String currentUrl) throws IOException {
        String navigableUrl = getNavigableUrl(currentUrl);
        currentPage = Jsoup.connect(navigableUrl).get();
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


    private String getNavigableUrl(String url) {
        if (url.startsWith(PropertiesService.getWikiBase())) {
            url = PropertiesService.getWikiDomain() + url;
        }

        return url;
    }

    private String findTitle(Document page) throws IOException {
        Element firstHeading = page.getElementById("firstHeading");
        return firstHeading.html();
    }

}
