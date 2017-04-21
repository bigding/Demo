package com.test.feiyun.demo;

/**
 * Created by 飞云 on 2016/11/1.
 */

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class DatabaseUtils {


    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://119.29.214.61/mengxiangshou";
    static final String USER = "root";
    static final String PASS = "feiyun45683995++";
    static final String TAG = "TAG";
    public static Connection getConnection(){
        Connection conn = null;
        try{
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.i(TAG,"Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Log.i(TAG,"Connecting success!");

        }catch (SQLException e) {

            e.printStackTrace();
            // TODO: handle exception
        }
        return conn;
    }
    public static ResultSet getResultSet(String sql) {

        ResultSet rs = null;
        Connection conn =null;
        conn = getConnection();
        try {
            Statement stmt = conn.createStatement();
            rs =stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    public static void executeSql(String sql) {
        // TODO Auto-generated method stub
        Connection conn = null;
        conn = getConnection();
        PreparedStatement pstmt;
        try{
            pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        }catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }
    //数据备份到本地
    public static void writeToLocalDB(ResultSet rs){
        SQLiteDatabase db = openOrCreateDatabase("local.db",null);
        String sql = "create table login(username varchar(20),name varchar(20),gender varchar(20),mail varchar(20),password varchar(20))";
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL(sql);
        try {
            while(rs.next()){
                ContentValues cnt = new ContentValues();
                cnt.put("id",rs.getInt("id"));
                cnt.put("username",rs.getString("username"));
                cnt.put("name",rs.getString("name"));
                cnt.put("gender",rs.getString("gender"));
                cnt.put("mail",rs.getString("mail"));
                cnt.put("password",rs.getString("password"));
                db.insert("login",null,cnt);
                Log.i(TAG,"Write Success!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

