package controls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import database.mysql.ConnectionPool;
import utils.Utils;

public class LocalControls {

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String myIp;

    public LocalControls() {
        try {
            myIp = Utils.getIp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LocalControls(String myIp) {
        this.myIp = myIp;
    }

    public void saveLog(String log) {
        Date now = new Date();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "UPDATE account_dexuat SET mylog = '"
                    + log
                    + "', update_time = '"
                    + simpleDateFormat.format(now)
                    + "' WHERE ip = '"
                    + myIp
                    + "';";
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            e.printStackTrace();
        }
    }

    public void setStatus(int running) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "UPDATE account_dexuat SET running = "
                    + running
                    + " WHERE ip = '"
                    + myIp
                    + "';";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            e.printStackTrace();
        }
    }

    public boolean checkStop() {
        boolean check = false;
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM account_dexuat WHERE ip = \'" + myIp + "\';";
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                String running = resultset.getString("running");
                if (running == null || running.compareTo("0") == 0) {
                    check = true;
                }
            }
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            e.printStackTrace();
        }

        return check;
    }

    public static void insertAccount(String ip, String username, String password) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "INSERT INTO account_dexuat (ip, username, password, running, mylog) VALUES ('" + ip + "', '"
                    + username + "', '" + password + "', 0, 'starting...') ON DUPLICATE KEY UPDATE running=0, mylog='starting...';";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        LocalControls control = new LocalControls("0.0.0.0");
        control.saveLog("no log 4");
        System.out.println("All done!");
    }
}
