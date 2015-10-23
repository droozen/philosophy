package com.roozen.services;

import com.roozen.model.WikiExecution;
import com.roozen.model.WikiNode;

import java.sql.*;
import java.util.*;

/**
 * Light database for this small project
 */
public class SQLService {

    //<editor-fold desc="Initialize">
    public void initializeDatabase() throws SQLException {
        Connection connection = null;
        try {
            connection = getConnection();
            System.out.println("Opened database successfully");

            initializeExecutionTable(connection);
            System.out.println(PropertiesService.getExecutionTable() + " created or already exists");

            initializePathTable(connection);
            System.out.println(PropertiesService.getPathTable() + " created or already exists");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        } finally {
            if (connection != null) connection.close();
        }
    }

    private void initializeExecutionTable(Connection connection) throws Exception {
        String table = PropertiesService.getExecutionTable();
        boolean exists = checkTable(connection, table);
        if (exists) return;

        String sql = PropertiesService.getExecutionCreateSql();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Created table " + PropertiesService.getExecutionTable());
        } finally {
            if (statement != null) statement.close();
        }
    }

    private void initializePathTable(Connection connection) throws Exception {
        String table = PropertiesService.getPathTable();
        boolean exists = checkTable(connection, table);
        if (exists) return;

        String sql = PropertiesService.getPathCreateSql();

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(sql);
            System.out.println("Created table " + table);
        } finally {
            if (statement != null) statement.close();
        }
    }

    /**
     * If the DB Table already exists we don't want to blow away previous work.
     *
     * @param connection
     * @param table
     * @return
     * @throws SQLException
     */
    private boolean checkTable(Connection connection, String table) throws SQLException {
        String checkExistenceSql = "select 1 from " + table;
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeQuery(checkExistenceSql);
        } catch (SQLException e) {
            return false; // Assume the error is because the table doesn't exist.
        } finally {
            if (statement != null) statement.close();
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Record Results">
    public synchronized boolean recordResults(Collection<WikiNode> results) {
        Connection connection = null;
        try {
            connection = getConnection();
            System.out.println("Opened database successfully");

            connection.setAutoCommit(false);

            int executionId = recordExecution(connection, results);
            recordPath(connection, results, executionId);
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) rollback(connection);
            return false;
        } finally {
            if (connection != null) close(connection);
        }

        return true;
    }

    /**
     * Insert the record representing this execution run.
     *
     * @param connection
     * @param results
     * @return
     * @throws SQLException
     */
    private int recordExecution(Connection connection, Collection<WikiNode> results) throws SQLException {
        String executionTable = PropertiesService.getExecutionTable();
        WikiNode firstNode = results.iterator().next();

        String nextIdSql = "select max(id) + 1 from " + executionTable;

        int nextId = 0;
        Statement nextIdStatement = null;
        try {
            nextIdStatement = connection.createStatement();
            nextId = nextIdStatement.executeQuery(nextIdSql).getInt(1);
        } finally {
            if (nextIdStatement != null) {
                nextIdStatement.close();
            }
        }

        String insertSql = PropertiesService.getExecutionInsertSql();
        insertSql = insertSql.replace(":id", Integer.toString(nextId));
        insertSql = insertSql.replace(":page", firstNode.getTitle());
        insertSql = insertSql.replace(":url", firstNode.getUrl());

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(insertSql);
        } finally {
            if (statement != null) statement.close();
        }

        return nextId;
    }

    /**
     * Insert the records for each step on the path we took.
     *
     * @param connection
     * @param results
     * @param executionId
     * @throws SQLException
     */
    private void recordPath(Connection connection, Collection<WikiNode> results, int executionId) throws SQLException {
        int step = 0;

        Iterator<WikiNode> iterator = results.iterator();
        while (iterator.hasNext()) {
            WikiNode node = iterator.next();
            recordStep(connection, node, executionId, step);
            step++;
        }
    }

    private void recordStep(Connection connection, WikiNode node, int executionId, int step) throws SQLException {
        String stepSql = PropertiesService.getStepInsertSql();
        stepSql = stepSql.replace(":id", Integer.toString(executionId));
        stepSql = stepSql.replace(":step", Integer.toString(step));
        stepSql = stepSql.replace(":page", node.getTitle());
        stepSql = stepSql.replace(":url", node.getUrl());

        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(stepSql);
        } finally {
            if (statement != null) statement.close();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Fetch">
    public List<WikiExecution> fetchExecutions() throws Exception {
        List<WikiExecution> executions = new ArrayList<>();

        String fetchExecutionSql = PropertiesService.getExecutionFetchSql();


        Statement statement = null;
        Connection connection = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchExecutionSql);

            while (resultSet.next()) {
                WikiExecution execution =
                        new WikiExecution(resultSet.getInt("id"), resultSet.getString("run_time"),
                                resultSet.getString("page"), resultSet.getString("url"));
                executions.add(execution);
            }

        } finally {
            if (statement != null) close(statement);
            if (connection != null) close(connection);
        }

        return executions;
    }

    public List<WikiNode> fetchPathForExecution(int executionId) throws Exception {
        List<WikiNode> path = new ArrayList<>();

        String fetchPathSql = PropertiesService.getPathFetchSql();
        fetchPathSql = fetchPathSql.replace(":id", Integer.toString(executionId));

        Statement statement = null;
        Connection connection = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(fetchPathSql);

            while (resultSet.next()) {
                WikiNode node = new WikiNode(resultSet.getString("url"), resultSet.getString("page"));
                path.add(node);
            }

        } finally {
            if (statement != null) close(statement);
            if (connection != null) close(connection);
        }

        return path;
    }
    //</editor-fold>

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(PropertiesService.getJdbcClass());
        return DriverManager.getConnection(PropertiesService.getJdbcUrl());
    }

    private void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(AutoCloseable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
