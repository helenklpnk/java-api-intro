package db;

import entities.Account;

import java.io.BufferedReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {
    public static boolean checkForAccount(Connection conn, String login, String password) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT login, password, first_name, last_name, " +
                "city, balance FROM account");
        while (rs.next()) {
            if (rs.getString(1).trim().equals(login) &&
                rs.getString(2).trim().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public static double showBalance(Connection conn, String login) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT login, balance FROM account");
        while (rs.next()) {
            if (rs.getString(1).trim().equals(login)) {
                return rs.getDouble(2);
            }
        }
        return -1;
    }

    public static void makeDeposit(Connection conn, String login, double amount) throws SQLException {
        PreparedStatement prst = conn.prepareStatement("INSERT INTO transaction (login, description, debit , credit, trans_date) "
                + "VALUES (?, ?, ?, ?, CURRENT_DATE)");
        prst.setString(1, login);
        prst.setString(2, "Deposit");
        prst.setDouble(3, amount);
        prst.setDouble(4, 0.00);
        prst.executeUpdate();
    }

    public static int withdrawMoney(Connection conn, String login, double amount) throws SQLException {
        if (amount > showBalance(conn, login)) {
            System.out.println("You don't have enough money.");
            return -1;
        }
        PreparedStatement prst = conn.prepareStatement("INSERT INTO transaction (login, description, debit , credit, trans_date) "
                + "VALUES (?, ?, ?, ?, CURRENT_DATE)");
        prst.setString(1, login);
        prst.setString(2, "Withdraw");
        prst.setDouble(3, 0.00);
        prst.setDouble(4, amount);
        return prst.executeUpdate();
    }

    public static boolean openAccount(Connection conn, String login, String passwd, String fname, String lname, String city, double initBalance) throws SQLException {
        if (checkForAccount(conn, login, passwd)) {
            System.out.println("Account with this ID already exists.");
            return false;
        }
        PreparedStatement prst = conn.prepareStatement("INSERT INTO ACCOUNT (login, password, first_name, last_name, " +
                "city, balance) "
                + "VALUES (?, ?, ?, ?, ?, ?)");
        prst.setString(1, login);
        prst.setString(2, passwd);
        prst.setString(3, fname);
        prst.setString(4, lname);
        prst.setString(5, city);
        prst.setDouble(6, initBalance);
        prst.executeUpdate();
        return true;
    }

    public static int closeAccount(Connection conn, String login) throws SQLException {
        double balance;
        if ((balance = showBalance(conn, login)) != 0.00) {
            System.out.println("You have " + balance + " dollars. "
                    + "Withdraw before closing your account.");
        }
        PreparedStatement prst = conn.prepareStatement("DELETE FROM account WHERE login = ?");
        prst.setString(1, login);
        return prst.executeUpdate();
    }
}
