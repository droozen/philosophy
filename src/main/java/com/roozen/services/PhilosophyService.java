package com.roozen.services;

import com.roozen.engine.WikiEngine;
import com.roozen.view.ErrorView;
import com.roozen.view.ViewFactory;
import org.apache.commons.lang3.StringUtils;
import spark.QueryParamsMap;

import static spark.Spark.get;
import static spark.Spark.post;


public class PhilosophyService {

    /**
     * Simple getter links, such as the initial form and viewing results.
     */
    public static void initLinks() {

        // Initial Form
        get(PropertiesService.getInitialPage(), (request, response) -> {
            return ViewFactory.getView(PropertiesService.getInitialPage()).render();
        });

        // View a list of executions that have been run
        get(PropertiesService.getExecutionPage(), (request, response) -> {
           return ViewFactory.getView(PropertiesService.getExecutionPage()).render();
        });

        // View the path of a given execution
        get(PropertiesService.getPathPage() + "/:id", (request, response) -> {
            String id = request.params(":id");
            if (id == null || id.isEmpty() || !StringUtils.isNumeric(id)) {
                return new ErrorView("Invalid Execution ID").render();
            }

            return ViewFactory.getView(PropertiesService.getPathPage(), new Integer(id)).render();
        });

    }

    /**
     * The engine that drives the logic to get to philosophy.
     */
    public static void initEngine() {

        // Submit Input
        post(PropertiesService.getSubmitPage(), (request, response) -> {
            QueryParamsMap queryParamsMap = request.queryMap("wiki-url");
            if (!validateParameters(queryParamsMap)) {
                return ViewFactory.getErrorView("Could not find URL").render();
            }

            try {
                WikiEngine engine = new WikiEngine(queryParamsMap.value());
                engine.run();

                Server.getSqlService().recordResults(engine.getResults());
                return ViewFactory.getView(PropertiesService.getSubmitPage(), engine.getResults()).render();
            } catch (Exception e) {
                return ViewFactory.getErrorView(e.getMessage()).render();
            }
        });

    }

    private static boolean validateParameters(QueryParamsMap queryParamsMap) {
        boolean pass = queryParamsMap != null;
        pass = pass && queryParamsMap.hasValue();
        pass = pass && queryParamsMap.value() != null;
        pass = pass && queryParamsMap.value().isEmpty() == false;
        pass = pass && isWikipediaUrl(queryParamsMap.value());

        return pass;
    }

    private static boolean isWikipediaUrl(String url) {
        return url.startsWith(PropertiesService.getWikiBase()) || url.startsWith(PropertiesService.getWikiDomain());
    }

}
