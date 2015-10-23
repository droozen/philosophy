package com.roozen.model;

import com.roozen.services.PropertiesService;

public class WikiUrl {

    public enum UrlType {
        ARTICLE, CITATION, HELP, FILE, OTHER
    }

    private String url;
    private UrlType type;

    public WikiUrl(String url) {
        this.url = url;

        if (this.url == null) {
            this.type = UrlType.OTHER;
        } else if (!this.url.startsWith(PropertiesService.getWikiBase()) && !this.url.startsWith(PropertiesService.getWikiDomain())) {
            this.type = UrlType.OTHER;
        } else if (this.url.contains("/wiki/Help:")) {
            this.type = UrlType.HELP;
        } else if (this.url.contains("/wiki/File:")) {
            this.type = UrlType.FILE;
        } else if (this.url.contains("cite")) {
            this.type = UrlType.CITATION;
        } else {
            this.type = UrlType.ARTICLE;
        }
    }

    public String getUrl() {
        return this.url;
    }

    public UrlType getType() {
        return type;
    }

    public boolean matches(WikiUrl anotherUrl) {
        if (anotherUrl == null || anotherUrl.getUrl() == null || this.getUrl() == null) return false;
        if (this.equals(anotherUrl)) return true;
        if (this.getType() != anotherUrl.getType()) return false;

        if (anotherUrl.getUrl().startsWith(PropertiesService.getWikiBase())
                && url.startsWith(PropertiesService.getWikiDomain())
                && url.equals(PropertiesService.getWikiDomain() + anotherUrl.getUrl())) {
            return true;
        }

        if (url.startsWith(PropertiesService.getWikiBase())
                && anotherUrl.getUrl().startsWith(PropertiesService.getWikiDomain())
                && anotherUrl.getUrl().equals(PropertiesService.getWikiDomain() + url)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiUrl)) return false;

        WikiUrl wikiUrl = (WikiUrl) o;

        return !(url != null ? !url.equals(wikiUrl.url) : wikiUrl.url != null);

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }
}
