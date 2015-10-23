package com.roozen.view;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;

import java.util.Collection;

/**
 * ViewFactory decouples the specific views from the service that renders them.
 */
public class ViewFactory {

    /**
     * TODO: Discuss in peer review. The way we pass the arguments, we lose the compile time type checking on the arguments.
     *
     * @param path
     * @param arguments
     * @return
     */
    public static View getView(String path, Object... arguments) {
        if (path == null) return null;

        if (path.startsWith(PropertiesService.getInitialPage())) {
            return new SimpleFormView();
        } else if (path.startsWith(PropertiesService.getExecutionPage())) {
            return new ExecutionView();
        } else if (path.startsWith(PropertiesService.getPathPage())) {

            if (arguments.length <= 0) return new ErrorView("Invalid Arguments for Path page");
            else if (!(arguments[0] instanceof Integer)) return new ErrorView("Invalid Arguments for Path page: " + arguments[0]);
            else return new PathHistoricalView((Integer) arguments[0]);

        } else if (path.startsWith(PropertiesService.getSubmitPage())) {

            if (arguments.length <= 0) return new ErrorView("Invalid Arguments for Review page");
            else if (!(arguments[0] instanceof Collection)) return new ErrorView("Invalid Arguments for Review page: " + arguments[0]);
            else return new ResultView((Collection<WikiNode>) arguments[0]);
        }

        return null;
    }

    public static View getErrorView(String message) {
        return new ErrorView(message);
    }

}
