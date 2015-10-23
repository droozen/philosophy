package com.roozen.view;

import com.roozen.model.WikiExecution;
import com.roozen.services.PropertiesService;
import com.roozen.services.SQLService;
import com.roozen.services.Server;
import j2html.tags.ContainerTag;

import java.util.List;

import static j2html.TagCreator.*;

public class ExecutionView implements View {

    @Override
    public String render() {
        ContainerTag body = body().with(h1("Executions"), h2("Times in UTC"));

        ContainerTag table = table();
        table.setAttribute("border", "1");
        writeHeader(table);

        SQLService sqlService = Server.getSqlService();
        List<WikiExecution> executions = null;
        try {
            executions = sqlService.fetchExecutions();
        } catch (Exception e) {
            return new ErrorView(e.getMessage()).render();
        }

        for (WikiExecution execution : executions) {
            table.with(
                    tr().with(
                            // Link to view the whole path taken.
                            td().with(a(Integer.toString(execution.getId())).withHref(PropertiesService.getPathPage() + "/" + execution.getId())),
                            td(execution.getRunTime().toString()),
                            td(execution.getPage()),
                            td(execution.getUrl())
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
                        th("ID"),
                        th("Run Time"),
                        th("Page"),
                        th("URL")
                )
        );
    }
}
