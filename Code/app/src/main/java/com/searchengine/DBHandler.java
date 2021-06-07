package com.searchengine;

import java.sql.*;
import java.util.Objects;

public class DBHandler {
    private Connection conn = null;

    private final String dbUrl = "jdbc:mysql://localhost:3306/engine";
    private final String username = "root", password = "";
    private final String charset = "?useUnicode=true&amp;characterEncoding=UTF-8";

    public DBHandler() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl + charset, username, password);
            Common.print("Connected to the DATABASE");
//            DeleteAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // for select statements
    public ResultSet execQuery(String sql, String[] values) {
        PreparedStatement pst;
        try {
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i) {
                pst.setString(i + 1, values[i]);
            }
            return pst.executeQuery();
        } catch (SQLException throwables) {
//            throwables.printStackTrace();
        }
        return null;
    }

    // for Insert, Update and Delete Statements
    public int exec(String sql, String[] values) {
        PreparedStatement pst = null;
        try {
            pst = conn.prepareStatement(sql);
            for (int i = 0; i < values.length; ++i) {
                pst.setString(i + 1, values[i]);
            }
            return pst.executeUpdate();
        } catch (SQLException throwables) {
            if (!Objects.requireNonNull(throwables.getMessage()).contains("Duplicate entry"))
            {
                System.out.println(Objects.requireNonNull(pst).toString());
//                throwables.printStackTrace();
            }
        }
        return 0;
    }

    public int insertIntoToBeCrawled(String url) {
        if (url.isEmpty() || url.length() > 766) return 0;
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        String query = "Insert INTO ToBeCrawled Values(?);";
        return this.exec(query, new String[]{url});
    }

    public int deleteIntoToBeCrawled(String url) {
        String query = "Delete From ToBeCrawled Where URL = ?;";
        return this.exec(query, new String[]{url});
    }

    public int insertIntoCrawled(String url, String title) {
        String query = "Insert INTO Crawled Values(?, ?);";
        return this.exec(query, new String[]{url, title});
    }

    public int insertIntoIndexed(String word, String url, float tf, String content) {
        String query = "Insert INTO Indexed Values(?, ?, " + tf + ", ?);";
        return this.exec(query, new String[]{word, url, content});
    }

    public int insertIntoBlockedURLS(String url) {
        String query = "Insert INTO BlockedURLS Values(?);";
        return this.exec(query, new String[]{url});
    }

    public int insertIntoDoneRobot(String url) {
        String query = "Insert INTO DoneRobot Values(?);";
        return this.exec(query, new String[]{url});
    }

    public int getNumberOfCrawledPages() {
        String query1 = "Select Count(*) From Crawled";
        try {
            ResultSet rs = execQuery(query1, new String[]{});
            int sum = 0;
            if (rs.next())
                sum += rs.getInt(1);
            return sum;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }


    public ResultSet getPendingURLs() {
        String query = "Select Distinct URL from tobecrawled where URL Not In (SELECT DISTINCT URL FROM crawled UNION SELECT DISTINCT URL FROM blockedurls);";
        ResultSet rs = this.execQuery(query, new String[]{});
        return rs;
    }

    public synchronized ResultSet getCrawledURL() {
        String query = "Select URL From Crawled Where URL NOT IN (Select Distinct URL From Indexed);";
        return this.execQuery(query, new String[]{});
    }

    private void DeleteAll() {
        String query1 = "Delete From ToBeCrawled;";
        String query2 = "Delete From Crawled;";
        String query3 = "Delete From Indexed;";
        String query4 = "Delete From BlockedURLS;";
        String query5 = "Delete From DoneRobot;";
        this.exec(query3, new String[]{});
        this.exec(query2, new String[]{});
        this.exec(query1, new String[]{});
        this.exec(query4, new String[]{});
        this.exec(query5, new String[]{});
        System.out.println("All Data is Deleted");
    }

    protected void finish() {
        try {
            if (conn != null || !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
