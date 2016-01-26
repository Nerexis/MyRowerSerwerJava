package myrowerserwer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nerexis
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLAccess {

    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public boolean Connect() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver");
            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://localhost/myrower?"
                            + "user=root&password=");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ResultSet GetAccountDataForLogin(String login) {

        try {
            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect
                    .prepareStatement("SELECT * FROM accounts WHERE login = ?;");
            // "myuser, webpage, datum, summery, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement.setString(1, login);
//      preparedStatement.setString(2, "TestEmail");
//      preparedStatement.setString(3, "TestWebpage");
//      preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
//      preparedStatement.setString(5, "TestSummary");
//      preparedStatement.setString(6, "TestComment");
            //preparedStatement.executeUpdate();
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        } finally {
//            close();
//        }
        return resultSet;
    }

    public boolean AddReserve(ReserveRequest request) {
        try {
            preparedStatement = connect
                    .prepareStatement("INSERT INTO reservation (userId, stationFrom, stationTo, time, date) "
                            + " VALUES (?, ?, ?, ?, ?);");
            preparedStatement.setInt(1, request.userId);
            preparedStatement.setString(2, request.from);
            preparedStatement.setString(3, request.to);
            preparedStatement.setString(4, request.time);
            preparedStatement.setString(5, request.date);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private void writeMetaData(ResultSet resultSet) throws SQLException {
        //   Now get some metadata from the database
        // Result set get the result of the SQL query

        System.out.println("The columns in the table are: ");

        System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
            System.out.println("Column " + i + " " + resultSet.getMetaData().getColumnName(i));
        }
    }

    private void writeResultSet(ResultSet resultSet) throws SQLException {
        // ResultSet is initially before the first data set
        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            String user = resultSet.getString("myuser");
            String website = resultSet.getString("webpage");
            String summery = resultSet.getString("summery");
            Date date = resultSet.getDate("datum");
            String comment = resultSet.getString("comments");
            System.out.println("User: " + user);
            System.out.println("Website: " + website);
            System.out.println("Summery: " + summery);
            System.out.println("Date: " + date);
            System.out.println("Comment: " + comment);
        }
    }

    // You need to close the resultSet
    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }
//
//      if (connect != null) {
//        connect.close();
//      }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CloseConnection() {
        if (connect != null) {
            try {
                connect.close();
            } catch (SQLException ex) {
                Logger.getLogger(MySQLAccess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
