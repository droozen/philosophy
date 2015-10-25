package com.roozen.engine;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WikiPathComponent {

    private Set<String> visitedUrls = new HashSet<>();
    private List<WikiNode> path = new ArrayList<>();

    public void visit(WikiNode currentNode) {
        visitedUrls.add(currentNode.getUrl());
        path.add(currentNode);

        System.out.println(this.getClass().getName() + " visit: " + currentNode.toString());
    }

    public List<WikiNode> getPath() {
        return new ArrayList<>(path);
    }

    public boolean isPathComplete(int level) {
        return haveReachedDestination() || haveReachedMaxHops() || haveReachedMaxLoopsOrDeadEnds(level);
    }

    // TODO: Optimize if needed. For each entry we have to evaluate all entries.
    public int loopDetection() {
        WikiNode last = getLast(path);

        int count = 0;
        for (int i = 0; i < path.size() - 1; i++) { // evaluate all but the last entry in the path
            if (path.get(i).equals(last)) count++;
        }

        if (count > 0) System.out.println("Detected loop at: " + last.toString());
        return count;
    }

    public boolean deadEndDetection() {
        WikiNode last = getLast(path);
        if (last != null && last.getUrl() != null) {
            return false;
        }

        System.out.println("Detected dead end.");

        // If we hit a dead end, let's pop that node off and continue following the next link down.
        path.remove(path.size() - 1);
        return true;
    }

    private WikiNode getLast(List<WikiNode> path) {
        if (path.size() <= 0) return null;
        return path.get(path.size() - 1);
    }

    private boolean haveReachedMaxHops() {
        return path.size() >= PropertiesService.getMaxHops();
    }

    private boolean haveReachedMaxLoopsOrDeadEnds(int level) {
        return level >= PropertiesService.getNumDeadEnds();
    }

    private boolean haveReachedDestination() {
        WikiNode last = getLast(path);
        return last != null &&
                (last.getUrl().equals(PropertiesService.getWikiDestination()) ||
                        last.getUrl().equals(PropertiesService.getWikiFullDestination()));
    }

}
