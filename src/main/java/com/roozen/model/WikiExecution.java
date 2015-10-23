package com.roozen.model;

public class WikiExecution {

    private int id;
    private String runTime;
    private String page;
    private String url;

    public WikiExecution(int id, String runTime, String page, String url) {
        this.id = id;
        this.runTime = runTime;
        this.page = page;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getRunTime() {
        return runTime;
    }

    public String getPage() {
        return page;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiExecution)) return false;

        WikiExecution that = (WikiExecution) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
