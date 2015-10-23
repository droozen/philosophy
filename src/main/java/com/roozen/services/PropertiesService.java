package com.roozen.services;

import java.util.ResourceBundle;

/**
 * PropertiesService owns the properties, knowing what the properties are named and what bundle to get them from.
 *
 * Future expansion: If we have too many properties or different properties namespaces, we can delegate some of
 * that knowledge to future classes, but for now, the PropertiesService can own it.
 */
public class PropertiesService {

    private static ResourceBundle properties = ResourceBundle.getBundle("default");

    public static String getInitialPage() {
        return properties.getString("initialPage");
    }

    public static String getExecutionPage() {
        return properties.getString("executionPage");
    }

    public static String getPathPage() {
        return properties.getString("pathPage");
    }

    public static String getSubmitPage() {
        return properties.getString("submitPage");
    }

    public static String getWikiBase() {
        return properties.getString("wikiBase");
    }

    public static String getWikiDomain() {
        return properties.getString("wikiDomain");
    }

    public static String getWikiDestination() {
        return properties.getString("wikiDestination");
    }

    public static String getWikiFullDestination() {
        return properties.getString("wikiFullDestination");
    }

    public static String getJdbcClass() {
        return properties.getString("jdbcClass");
    }

    public static String getJdbcUrl() {
        return properties.getString("jdbcUrl");
    }

    public static String getExecutionTable() {
        return properties.getString("tableExecution");
    }

    public static String getPathTable() {
        return properties.getString("tablePath");
    }

    public static String getExecutionCreateSql() {
        return properties.getString("createExecutionSql");
    }

    public static String getPathCreateSql() {
        return properties.getString("createPathSql");
    }

    public static String getExecutionInsertSql() {
        return properties.getString("insertExecutionSql");
    }

    public static String getStepInsertSql() {
        return properties.getString("insertStepSql");
    }

    public static String getExecutionFetchSql() {
        return properties.getString("fetchExecutionSql");
    }

    public static String getPathFetchSql() {
        return properties.getString("fetchPathSql");
    }

    public static int getNumDeadEnds() {
        return new Integer(properties.getString("numDeadEnds"));
    }

    public static int getMaxHops() {
        return new Integer(properties.getString("maxHops"));
    }
}
