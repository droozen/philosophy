package com.roozen.view;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;
import com.roozen.services.SQLService;
import com.roozen.services.Server;
import j2html.tags.ContainerTag;

import java.util.List;

import static j2html.TagCreator.*;

public class PathHistoricalView implements View {

    private int executionId;

    public PathHistoricalView(int executionId) {
        this.executionId = executionId;
    }

    @Override
    public String render() {
        ContainerTag body = body().with(h1("Path For Execution"));

        ContainerTag table = table();
        table.setAttribute("border", "1");
        writeHeader(table);

        SQLService sqlService = Server.getSqlService();
        List<WikiNode> path = null;
        try {
            path = sqlService.fetchPathForExecution(executionId);
        } catch (Exception e) {
            return new ErrorView(e.getMessage()).render();
        }

        for (int index = 0; index < path.size(); index++) {
            WikiNode node = path.get(index);
            table.with(
                    tr().with(
                            // Link to view the whole path taken.
                            td(Integer.toString(index)),
                            td(node.getTitle()),
                            td(node.getUrl())
                    )
            );
        }

        body.with(table);
        body.with(a().withHref(PropertiesService.getInitialPage()).withText("Back to Start"));

        return body.render();
    }

    private void writeHeader(ContainerTag table) {
        table.with(
                tr().with(
                        th("Step"),
                        th("Page"),
                        th("URL")
                )
        );
    }
}
