package com.company;
import java.sql.*;
import com.tmax.tibero.jdbc.ext.TbDataSource;

public class Main {

    static final String JDBC_DRIVER = "com.tmax.tibero.jdbc.TbDriver";
    static final String DATABASE_URL = "jdbc:tibero:thin:@127.0.0.1:8629:tibero";

    //USERNAME is the username to connect database
    static final String USERNAME = "tibero2";
    //PASSWORD is the password to connect database
    static final String PASSWORD = "tibero";

    static Connection connection;

    public static void main(String[] args){
        Welcome welcome = new Welcome();

        TbDataSource dataSource = new TbDataSource();
        try{
            dataSource.setUser(USERNAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setURL(DATABASE_URL);
            connection = dataSource.getConnection();

            System.out.println("Welcome\n");
            welcome.readInput();

        }catch(SQLException e){
            e.printStackTrace();
        }

    }
}
