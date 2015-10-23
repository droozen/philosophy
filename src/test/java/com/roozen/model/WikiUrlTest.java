package com.roozen.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WikiUrlTest {

    @Test
    public void testUrlType() {
        assertEquals("Wrong UrlType. Expected ARTICLE", WikiUrl.UrlType.ARTICLE, new WikiUrl("/wiki/Computer_Science").getType());
        assertEquals("Wrong UrlType. Expected ARTICLE", WikiUrl.UrlType.ARTICLE, new WikiUrl("https://en.wikipedia.org/wiki/Computer_Science").getType());
        assertEquals("Wrong UrlType. Expected HELP", WikiUrl.UrlType.HELP, new WikiUrl("/wiki/Help:Contents").getType());
        assertEquals("Wrong UrlType. Expected CITATION", WikiUrl.UrlType.CITATION, new WikiUrl("/wiki/Tsunami#cite_note-2").getType());
        assertEquals("Wrong UrlType. Expected FILE", WikiUrl.UrlType.FILE, new WikiUrl("/wiki/File:UnknownGoose.avi").getType());
        assertEquals("Wrong UrlType. Expected OTHER", WikiUrl.UrlType.OTHER, new WikiUrl("http://google.com").getType());
    }

    @Test
    public void testMatches_happyPath() {
        {
            WikiUrl sourceUrl = new WikiUrl("/wiki/Computer_Science");
            WikiUrl testUrl = new WikiUrl("https://en.wikipedia.org/wiki/Computer_Science");

            assertTrue("Expected Urls to match", sourceUrl.matches(testUrl));
            assertTrue("Expected Urls to match", testUrl.matches(sourceUrl));
        }

        {
            WikiUrl sourceUrl = new WikiUrl("/wiki/Computer_Science");
            WikiUrl testUrl = new WikiUrl("/wiki/Computer_Science");

            assertTrue("Expected Urls to match", sourceUrl.matches(testUrl));
            assertTrue("Expected Urls to match", sourceUrl.matches(sourceUrl));
        }

        {
            WikiUrl sourceUrl = new WikiUrl("https://en.wikipedia.org/wiki/Computer_Science");
            WikiUrl testUrl = new WikiUrl("https://en.wikipedia.org/wiki/Computer_Science");

            assertTrue("Expected Urls to match", sourceUrl.matches(testUrl));
        }
    }

    @Test
    public void testMatches_failPath() {
        {
            WikiUrl sourceUrl = new WikiUrl(null);
            WikiUrl testUrl = new WikiUrl("/wiki/Computer_Science");

            assertFalse("Expected Urls not to match", sourceUrl.matches(testUrl));
            assertFalse("Expected Urls not to match", testUrl.matches(sourceUrl));
            assertFalse("Expected Urls not to match", sourceUrl.matches(null));
        }

        {
            WikiUrl sourceUrl = new WikiUrl("/wiki/Computer_Science");
            WikiUrl testUrl = new WikiUrl("/wiki/Tsunami");

            assertFalse("Expected Urls not to match", sourceUrl.matches(testUrl));
            assertFalse("Expected Urls not to match", testUrl.matches(sourceUrl));
        }
    }

}
