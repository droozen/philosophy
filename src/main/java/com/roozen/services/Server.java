package com.roozen.services;

import java.sql.SQLException;

/**
 * Quick and lightweight API for this small project.
 */
public class Server {

    private static SQLService sqlService;

    public static void main(String[] args) throws SQLException {
        sqlService = new SQLService();

        // Create Database
        sqlService.initializeDatabase();

        // Philosophy Engine
        PhilosophyService.initLinks();
        PhilosophyService.initEngine();
    }

    public static SQLService getSqlService() {
        return sqlService;
    }

}
