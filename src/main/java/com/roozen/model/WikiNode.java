package com.roozen.model;

public class WikiNode {

    private String url;
    private String title;

    public WikiNode(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return this.getUrl() + " - " + this.getTitle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiNode)) return false;

        WikiNode wikiNode = (WikiNode) o;

        return !(url != null ? !url.equals(wikiNode.url) : wikiNode.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
