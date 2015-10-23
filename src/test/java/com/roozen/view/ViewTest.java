package com.roozen.view;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;
import com.roozen.services.SQLService;
import com.roozen.services.Server;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the ViewFactory and the Views it returns.
 * Just simple test on the View.render(), as we don't want to maintain an exact HTML match in our tests,
 * so we'll look for the important pieces we need.
 * <p>
 * Further UI tests may be created with Selenium
 */
public class ViewTest {

    private static SQLService sqlService;

    @BeforeClass
    public static void setUp() throws Exception {
        sqlService = new SQLService();
        sqlService.initializeDatabase();

        // We'll need the SQLService on the Server accessible for some tests,
        // but we don't want to change the interface of the class just for tests.
        // So we'll cheat and use a little reflection to set it up.
        Field field = Server.class.getDeclaredField("sqlService");
        field.setAccessible(true);
        field.set(null, sqlService);
        field.setAccessible(false);
    }

    @Test
    public void testErrorView() {
        final String inputMessage = "Test Error Message";
        View errorView = ViewFactory.getErrorView(inputMessage);

        assertTrue("Wrong class returned for error view", errorView instanceof ErrorView);

        // EXECUTE
        String result = errorView.render();

        // VERIFY
        assertTrue("Resulting HTML does not contain error message", result.contains(inputMessage));
        assertTrue("Resulting HTML does not contain link back to home page", result.contains("href=\"" + PropertiesService.getInitialPage() + "\""));
    }

    @Test
    public void testInitialPageView() {
        View pageView = ViewFactory.getView(PropertiesService.getInitialPage());

        assertTrue("Wrong class returned for initial page view", pageView instanceof SimpleFormView);

        // EXECUTE
        String result = pageView.render();

        // VERIFY
        assertTrue("Resulting HTML does not contain form", result.contains("form"));
        assertTrue("Resulting HTML does not contain input box", result.contains("input"));
        assertTrue("Resulting HTML does not contain link to historical executions", result.contains("href=\"" + PropertiesService.getExecutionPage() + "\""));
    }

    @Test
    public void testExecutionPageView() {
        View pageView = ViewFactory.getView(PropertiesService.getExecutionPage());

        assertTrue("Wrong class returned for execution page view", pageView instanceof ExecutionView);

        // EXECUTE
        String result = pageView.render();

        // VERIFY
        assertTrue("Resulting HTML does not contain table", result.contains("table"));
        assertTrue("Resulting HTML does not contain link back to home page", result.contains("href=\"" + PropertiesService.getInitialPage() + "\""));
    }

    @Test
    public void testPathPageView() {
        View pageView = ViewFactory.getView(PropertiesService.getPathPage(), new Integer(0));

        assertTrue("Wrong class returned for path page view", pageView instanceof PathHistoricalView);

        // EXECUTE
        String result = pageView.render();

        // VERIFY
        assertTrue("Resulting HTML does not contain table", result.contains("table"));
        assertTrue("Resulting HTML does not contain link back to home page", result.contains("href=\"" + PropertiesService.getInitialPage() + "\""));
    }

    @Test
    public void testResultView() {
        final String stepOne = "Step One";

        final ArrayDeque<WikiNode> path = new ArrayDeque<>();
        path.push(new WikiNode("/wiki/stepone", stepOne));
        path.push(new WikiNode("/wiki/steptwo", "Step Two"));
        path.push(new WikiNode("/wiki/stepthree", "Step Three"));

        View pageView = ViewFactory.getView(PropertiesService.getSubmitPage(), path.clone());

        assertTrue("Wrong class returned for result page view", pageView instanceof ResultView);

        // EXECUTE
        String result = pageView.render();

        // VERIFY
        assertEquals("Render changed the source path length?", 3, path.size());
        assertEquals("Render changed the node in the source path?", stepOne, path.peekLast().getTitle());

        Iterator<WikiNode> iterator = path.iterator();
        while (iterator.hasNext()) {
            WikiNode node = iterator.next();
            assertTrue("Resulting HTML does not contain step " + node.toString(), result.contains(node.toString()));
        }

        assertTrue("Resulting HTML does not contain number of links followed", result.contains("Number of Links Followed"));
        assertTrue("Resulting HTML does not contain link back to home page", result.contains("href=\"" + PropertiesService.getInitialPage() + "\""));
    }

    @Test
    public void testPathPageView_invalidArguments() {
        {
            View pageView = ViewFactory.getView(PropertiesService.getPathPage());
            assertTrue("Wrong class returned. Expected error.", pageView instanceof ErrorView);
            assertTrue("Missing error message", pageView.render().contains("Invalid Arguments for Path page"));
        }

        {
            final String inputInvalidArgument = "not an integer";
            View pageView = ViewFactory.getView(PropertiesService.getPathPage(), inputInvalidArgument);
            assertTrue("Wrong class returned. Exepcted error.", pageView instanceof ErrorView);
            assertTrue("Missing error message", pageView.render().contains("Invalid Arguments for Path page: " + inputInvalidArgument));
        }
    }

    @Test
    public void testResultPageView_invalidArguments() {
        {
            View pageView = ViewFactory.getView(PropertiesService.getSubmitPage());
            assertTrue("Wrong class returned. Expected error.", pageView instanceof ErrorView);
            assertTrue("Missing error message", pageView.render().contains("Invalid Arguments for Review page"));
        }

        {
            final String inputInvalidArgument = "not a collection";
            View pageView = ViewFactory.getView(PropertiesService.getSubmitPage(), inputInvalidArgument);
            assertTrue("Wrong class returned. Expected error.", pageView instanceof ErrorView);
            assertTrue("Missing error message", pageView.render().contains("Invalid Arguments for Review page: " + inputInvalidArgument));
        }
    }

}
