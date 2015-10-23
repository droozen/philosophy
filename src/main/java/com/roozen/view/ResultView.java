package com.roozen.view;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;
import j2html.tags.ContainerTag;

import java.util.Collection;
import java.util.Iterator;

import static j2html.TagCreator.*;

public class ResultView implements View {

    private Collection<WikiNode> results;

    public ResultView(Collection<WikiNode> results) {
        this.results = results;
    }

    @Override
    public String render() {

        ContainerTag body = body().with(h1("Results"));

        ContainerTag listOfPages = ul();

        WikiNode node = null;
        Iterator<WikiNode> iterator = results.iterator();
        while (iterator.hasNext()) {
            node = iterator.next();
            listOfPages.with(li(node.toString()));
        }

        body.with(listOfPages);
        ContainerTag div = div().with(text("Number of Links Followed: " + results.size()));
        if (results.size() >= PropertiesService.getMaxHops()) {
            div.with(text("That's the maximum amount of links allowed!" +
                    (ifReachedDestination(node.getUrl()) ? "But you made it!" : "Sorry you didn't get to the end.")));
        }
        body.with(div);
        body.with(a().withHref(PropertiesService.getInitialPage()).withText("Back to Start"));

        return body.render();
    }

    private boolean ifReachedDestination(String url) {
        return url.equals(PropertiesService.getWikiFullDestination()) || url.equals(PropertiesService.getWikiDestination());
    }

}
