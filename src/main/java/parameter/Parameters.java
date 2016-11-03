package parameter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.mysql.ConnectionPool;
import utils.Utils;

public class Parameters {

    public static final String file_link = "links.txt";
    public static final String file_info = "info.txt";
    public static final String file_hours = "hours.txt";
    public static final String file_driver = "geckodriver/chromedriver.exe";

    public String username = "";
    public String password = "";
    public String status = "";
    public ArrayList<String> listTargetVideos = new ArrayList<String>();
    public ArrayList<String> listOtherVideos = new ArrayList<String>();
    public ArrayList<String> listComments = new ArrayList<String>();
    public int min_time_second_my_video = 0;
    public int max_time_second_my_video = 0;
    public int min_time_second_other_video = 0;
    public int max_time_second_other_video = 0;
    public int num_iteration = 0;
    public int num_times_comment = 0;
    
    public ArrayList<String> listComments1 = new ArrayList<String>();
    public int min_time_second1 = 0;
    public int max_time_second1 = 0;
    
    public static int max_second_wait = 10;
    public static int warning_seconds = 600;

    public Parameters() {
        try {
            getAccountFromMySQL();
            if (username.length() > 0) {
                getParameterFromMySQL();
                printParameters();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Parameters(String myIp) {
        try {
            getAccountFromMySQL(myIp);
            if (username.length() > 0) {
                getParameterFromMySQL();
                printParameters();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAccountFromMySQL(String myIp) throws Exception {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        connect = ConnectionPool.getConnection();
        statement = connect.createStatement();

        try {
            String query = "SELECT * FROM account_dexuat WHERE ip = \'" + myIp + "\';";
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                String running = resultset.getString("running");
                if (running!=null && running.compareTo("0") != 0) {
                    username = resultset.getString("username").trim();
                    password = resultset.getString("password").trim();
                    status = running;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ConnectionPool.closeConnection(resultset, statement, connect);
    }

    public void getAccountFromMySQL() throws Exception {
        String myIp = Utils.getIp();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        connect = ConnectionPool.getConnection();
        statement = connect.createStatement();

        try {
            String query = "SELECT * FROM account_dexuat WHERE ip = \'" + myIp + "\';";
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                String running = resultset.getString("running");
                if (running.compareTo("1") == 0) {
                    username = resultset.getString("username").trim();
                    password = resultset.getString("password").trim();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ConnectionPool.closeConnection(resultset, statement, connect);
    }

    public void getParameterFromMySQL() throws Exception {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        connect = ConnectionPool.getConnection();
        statement = connect.createStatement();

        try {
            String query = "SELECT * FROM parameters_dexuat;";
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String id = resultset.getString("id");
                String value = resultset.getString("value");
                if (id.compareTo("max_time_second_my_video") == 0) {
                    max_time_second_my_video = Integer.parseInt(value.trim());
                } else if (id.compareTo("min_time_second_my_video") == 0) {
                    min_time_second_my_video = Integer.parseInt(value.trim());
                } else if (id.compareTo("max_time_second_other_video") == 0) {
                    max_time_second_other_video = Integer.parseInt(value.trim());
                } else if (id.compareTo("min_time_second_other_video") == 0) {
                    min_time_second_other_video = Integer.parseInt(value.trim());
                } else if (id.compareTo("num_iteration") == 0) {
                    num_iteration = Integer.parseInt(value.trim());
                } else if (id.compareTo("num_times_comment") == 0) {
                    num_times_comment = Integer.parseInt(value.trim());
                } else if (id.compareTo("comments") == 0) {
                    String[] arrayComments = value.split(",");
                    for (int i = 0; i < arrayComments.length; i++) {
                        listComments.add(arrayComments[i].trim());
                    }
                } else if (id.compareTo("other_videos") == 0) {
                    String[] arrayVideos = value.split(",");
                    for (int i = 0; i < arrayVideos.length; i++) {
                        listOtherVideos.add(arrayVideos[i].trim());
                    }
                } else if (id.compareTo("target_videos") == 0) {
                    String[] arrayVideos = value.split(",");
                    for (int i = 0; i < arrayVideos.length; i++) {
                        listTargetVideos.add(arrayVideos[i].trim());
                    }
                } else if (id.compareTo("comments1") == 0) {
                    String[] arrayComments = value.split(",");
                    for (int i = 0; i < arrayComments.length; i++) {
                        listComments1.add(arrayComments[i].trim());
                    }
                } else if (id.compareTo("min_time_second1") == 0) {
                    min_time_second1 = Integer.parseInt(value.trim());
                } else if (id.compareTo("max_time_second1") == 0) {
                    max_time_second1 = Integer.parseInt(value.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ConnectionPool.closeConnection(resultset, statement, connect);
    }

    public void printParameters() {
        System.out.println("username: " + username);
        System.out.println("max_time_second_my_video = " + max_time_second_my_video + " (s)");
        System.out.println("min_time_second_my_video = " + min_time_second_my_video + " (s)");
        System.out.println("max_time_second_other_video = " + max_time_second_other_video + " (s)");
        System.out.println("min_time_second_other_video = " + min_time_second_other_video + " (s)");
        System.out.println("num_iteration = " + num_iteration);
        System.out.println("num_times_comment = " + num_times_comment);

        System.out.println("listTargetVideos");
        for (int i = 0; i < listTargetVideos.size(); i++) {
            System.out.println("   " + listTargetVideos.get(i));
        }

        System.out.println("listOtherVideos");
        for (int i = 0; i < listOtherVideos.size(); i++) {
            System.out.println("   " + listOtherVideos.get(i));
        }

        System.out.println("listComments");
        for (int i = 0; i < listComments.size(); i++) {
            System.out.println("   " + listComments.get(i));
        }
    }

    public static void main(String[] args) throws Exception {
        Parameters parameters = new Parameters();
        parameters.getAccountFromMySQL("0.0.0.0");
        if (parameters.username.length() > 0) {
            parameters.getParameterFromMySQL();
            parameters.printParameters();
        }
        System.out.println("Done!");
    }
}
