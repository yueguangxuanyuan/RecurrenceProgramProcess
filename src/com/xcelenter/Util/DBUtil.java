package com.xcelenter.Util;

import java.sql.*;

public class DBUtil {

    public static Connection getDBConnection(String dbFilePath){
        Connection con = null;

        try{
            Class.forName("org.sqlite.JDBC");

            con = DriverManager.getConnection("jdbc:sqlite:"+dbFilePath);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return con;
    }

    public static void elegantlyClose(Connection connection, Statement statement, ResultSet resultSet){
        try {
            if(resultSet != null && !resultSet.isClosed()){
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(statement != null && !statement.isClosed()){
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
