package parameter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import database.mysql.ConnectionPool;
import utils.Utils;

public class Parameters {

    public String file_driver = "geckodriver/chromedriver.exe";
    public String username = "";
    public String password = "";
    public String status = "";
    public ArrayList<String> listTargetVideos = new ArrayList<String>();
    public ArrayList<String> listOtherVideos = new ArrayList<String>();
    public ArrayList<String> listComments = new ArrayList<String>();
    public ArrayList<String> listComments1 = new ArrayList<String>();
    public ArrayList<String> listChannels = new ArrayList<String>();
    public ArrayList<String> listSourceVideos = new ArrayList<String>();
    public ArrayList<String> listCommentsSuggest = new ArrayList<String>();
    public int min_time_second_my_video = 0;
    public int max_time_second_my_video = 0;
    public int min_time_second_other_video = 0;
    public int max_time_second_other_video = 0;
    public int num_iteration = 0;
    public int num_times_comment = 0;
    
    public int min_time_second1 = 0;
    public int max_time_second1 = 0;

    public int max_time_second_my_channel = 0;
    public int min_time_second_my_channel = 0;
    public int max_time_second_source_video = 0;
    public int min_time_second_source_video = 0;

    public int max_second_wait = 10;
    static public int warning_seconds = 600;

    public Parameters() {
        try {
            getAccountFromMySQL();
            if (username.length() > 0) {
                getParameterFromMySQL();
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAccountFromMySQL(String myIp) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM account_dexuat WHERE ip = \'" + myIp + "\';";
            // System.out.println(query);
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                String running = resultset.getString("running");
                if (running != null && running.compareTo("0") != 0) {
                    username = resultset.getString("username").trim();
                    password = resultset.getString("password").trim();
                    status = running;
                }
            }
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            System.out.println("Get acount fail!");            
            e.printStackTrace();
        }
    }

    public void getAccountFromMySQL() {

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            String myIp = Utils.getIp();
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM account_dexuat WHERE ip = \'" + myIp + "\';";
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                String running = resultset.getString("running");
                if (running.compareTo("1") == 0) {
                    username = resultset.getString("username").trim();
                    password = resultset.getString("password").trim();
                }
            }
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            System.out.println("Get acount fail!");
        }

        ConnectionPool.closeConnection(resultset, statement, connect);
    }

    public void getParameterFromMySQL() {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM parameters_dexuat;";
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String id = resultset.getString("id");
                String value = resultset.getString("value");
                if (value != null) {
                    if (id.compareTo("max_time_second_my_video") == 0) {
                        max_time_second_my_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("min_time_second_my_video") == 0) {
                        min_time_second_my_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("max_time_second_other_video") == 0) {
                        max_time_second_other_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("min_time_second_other_video") == 0) {
                        min_time_second_other_video = Integer.parseInt(value.trim());
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
                    } else if (id.compareTo("min_time_second_my_channel") == 0) {
                        min_time_second_my_channel = Integer.parseInt(value.trim());
                    } else if (id.compareTo("max_time_second_my_channel") == 0) {
                        max_time_second_my_channel = Integer.parseInt(value.trim());
                    } else if (id.compareTo("min_time_second_source_video") == 0) {
                        min_time_second_source_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("max_time_second_source_video") == 0) {
                        max_time_second_source_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("channels") == 0) {
                        String[] arrayChannels = value.split(",");
                        for (int i = 0; i < arrayChannels.length; i++) {
                            listChannels.add(arrayChannels[i].trim());
                        }
                    } else if (id.compareTo("source_videos") == 0) {
                        String[] arraySourceVideos = value.split(",");
                        for (int i = 0; i < arraySourceVideos.length; i++) {
                            listSourceVideos.add(arraySourceVideos[i].trim());
                        }
                    } else if (id.compareTo("comments_click_suggest") == 0) {
                        String[] arrayComments = value.split(",");
                        for (int i = 0; i < arrayComments.length; i++) {
                            listCommentsSuggest.add(arrayComments[i].trim());
                        }
                    }
                }
            }
            System.out.println("Load parameter success!");
            System.out.println("username: " + username);
            System.out.println();
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            System.out.println("Load parameter fail!");
        }
    }

    public static void main(String[] args) throws Exception {
        Parameters parameters = new Parameters();
        parameters.getAccountFromMySQL("1.2.3.4");
        if (parameters.username.length() > 0) {
            parameters.getParameterFromMySQL();
        }
        System.out.println("Done!");
    }
}
