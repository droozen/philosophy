package com.roozen.engine;

import com.roozen.model.WikiNode;
import com.roozen.services.PropertiesService;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import static org.junit.Assert.*;

public class WikiEngineTest {

    @Test
    public void testHappyPath() throws IOException {
        final String firstPage = "https://en.wikipedia.org/wiki/Computer_science";
        final String expectedLastPage = "https://en.wikipedia.org/wiki/Philosophy";
        final WikiEngine engine = new WikiEngine(firstPage);

        // EXECUTE
        engine.run();

        // VERIFY
        Collection<WikiNode> results = engine.getResults();
        assertTrue("Expected some results.", results.size() > 1);
        assertEquals("First page should have been " + firstPage, firstPage, results.iterator().next().getUrl());

        WikiNode lastPage = null;
        Iterator<WikiNode> iterator = results.iterator();
        while (iterator.hasNext()) {
            lastPage = iterator.next();
        }

        assertEquals("Last page should have been " + expectedLastPage, expectedLastPage, lastPage.getUrl());

        // Cannot run twice
        try {
            engine.run();
        } catch (RuntimeException e) {
            // Pass
            return;
        }
        fail("We should have hit an exception when attempting to run the engine twice.");
    }

    @Test
    public void testPathWithLoop() throws IOException {
        // As of 10/23/2015, Tsunami had a loop on the page Language
        final String firstPage = "https://en.wikipedia.org/wiki/Tsunami";
        final String loopPage = "https://en.wikipedia.org/wiki/Language";
        final String expectedLastPage = "https://en.wikipedia.org/wiki/Philosophy";
        final WikiEngine engine = new WikiEngine(firstPage);

        // EXECUTE
        engine.run();

        // VERIFY
        Collection<WikiNode> results = engine.getResults();
        assertTrue("Expected some results.", results.size() > 1);
        assertEquals("First page should have been " + firstPage, firstPage, results.iterator().next().getUrl());

        WikiNode lastPage = null;
        Iterator<WikiNode> iterator = results.iterator();
        int countLoop = 0;
        while (iterator.hasNext()) {
            lastPage = iterator.next();
            if (lastPage.getUrl().equals(loopPage)) {
                countLoop++;
            }
        }

        assertEquals("Last page should have been " + expectedLastPage, expectedLastPage, lastPage.getUrl());
        assertEquals("We should have entered the loop page a couple times", 2, countLoop);
        // Writing actually leads to Communication, which is the first loop, then backs up and goes to language, which is the second loop.
    }

    @Test
    public void testNonWikipediaUrl() throws IOException {
        final String firstPage = "https://www.google.com";
        final WikiEngine engine = new WikiEngine(firstPage);

        try {
            engine.run();
        } catch (RuntimeException e) {
            // Pass
            return;
        }
        fail("We should not run the engine with a non-wikipedia url");
    }

    @Test
    public void testReachedMaxHops() throws Exception {
        final String firstPage = "https://en.wikipedia.org/wiki/Tsunami";
        final String expectedLastPage = "https://en.wikipedia.org/wiki/Philosophy";
        final WikiEngine engine = new WikiEngine(firstPage);

        ResourceBundle properties = ResourceBundle.getBundle("test");

        Field field = PropertiesService.class.getDeclaredField("properties");
        field.setAccessible(true);
        field.set(null, properties);
        field.setAccessible(false);

        // EXECUTE
        engine.run();

        // VERIFY
        Collection<WikiNode> results = engine.getResults();
        assertEquals("Expected only 3 links, as that's the max defined in our test properties", 3, results.size());

        WikiNode lastPage = null;
        Iterator<WikiNode> iterator = results.iterator();
        while (iterator.hasNext()) {
            lastPage = iterator.next();
        }

        assertFalse("Expected not to reach " + expectedLastPage, lastPage.getUrl().equals(expectedLastPage));
    }
}
