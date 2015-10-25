package com.roozen.engine;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;

import java.io.IOException;
import java.util.Collection;

public class WikiEngine {

    private String url;
    private int level = 0;

    private WikiParserComponent parserComponent;
    private WikiPathComponent pathComponent;

    public WikiEngine(String url) {
        this.url = url;

        this.parserComponent = new WikiParserComponent();
        this.pathComponent = new WikiPathComponent();
    }

    public void run() throws IOException {
        if (this.pathComponent.getPath().isEmpty() == false) {
            throw new RuntimeException("Cannot execute the same engine twice. Please instantiate new Wiki search engine.");
        } else if (!isWikipediaUrl(url)) {
            throw new RuntimeException("Cannot run engine on a non-Wikipedia link.");
        }

        this.parserComponent.setStartingUrl(url);
        WikiNode currentNode = this.parserComponent.parseNode();
        this.pathComponent.visit(currentNode);

        while (!this.pathComponent.isPathComplete(level)) {

            this.parserComponent.followLink(level);
            currentNode = this.parserComponent.parseNode();
            this.pathComponent.visit(currentNode);

            int numLoopsDetected = this.pathComponent.loopDetection();
            if (numLoopsDetected > level) level = numLoopsDetected;

            if (this.pathComponent.deadEndDetection()) level++;
        }
    }

    private static boolean isWikipediaUrl(String url) {
        return url.startsWith(PropertiesService.getWikiBase()) || url.startsWith(PropertiesService.getWikiDomain());
    }

    /**
     * Retrieve the final path this engine took.
     *
     * @return
     */
    public Collection<WikiNode> getResults() {
        return this.pathComponent.getPath();

    }
}
