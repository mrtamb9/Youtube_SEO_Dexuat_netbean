package controls;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.mysql.ConnectionPool;
import utils.Utils;

public class ServerControls {

    // ALTER TABLE parameters_dexuat MODIFY `value` LONGTEXT CHARACTER SET utf8;
    public ArrayList<String> listIps;
    public ArrayList<String> listUserNames;
    public ArrayList<String> listPassword;
    public ArrayList<String> listStatus;
    public ArrayList<String> listMyLogs;
    public ArrayList<String> listInfos;
    public ArrayList<Date> listUpdateTimes;
    public ArrayList<String> listWarning;

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean setStatus(String groupIPs, int running) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "UPDATE account_dexuat SET running = " + running + " WHERE ip IN " + groupIPs + ";";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean insertAccount(String ip, String username, String password) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "INSERT INTO account_dexuat (ip, username, password, running, mylog) VALUES ('" + ip + "', '"
                    + username + "', '" + password + "', 0, 'no log') ON DUPLICATE KEY UPDATE username='" + username
                    + "', password='" + password + "', running=0, mylog='no log';";
            System.out.println(query);
            statement.executeUpdate(query);

            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean insertMultiAccount(ArrayList<String> listAccounts) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "INSERT INTO account_dexuat (ip, username, password, running, mylog) VALUES ";
            for (int i = 0; i < listAccounts.size(); i++) {
                query += listAccounts.get(i) + ",";
            }
            if (query.endsWith(",")) {
                query = query.substring(0, query.length() - 1);
            }
            System.out.println("Insert " + listAccounts.size() + " accounts!");
            statement.executeUpdate(query);

            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }

    }

    public boolean insertHashtag(String video_id, String hashtag) throws Exception {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "INSERT INTO hashtag_table (video_id, hashtag) VALUES ('" + video_id + "', '" + hashtag + "') ON DUPLICATE KEY UPDATE hashtag='" + hashtag + "';";
            System.out.println(query);
            statement.executeUpdate(query);

            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }

    }

    public boolean deleteHashTagInMySQL(String video_id) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "DELETE FROM hashtag_table WHERE video_id = '" + video_id + "';";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public ArrayList<String> getAllHashtagFromMySQL() {

        ArrayList<String> listHashtag = new ArrayList<>();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM hashtag_table;";
            System.out.println(query);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String video_id = resultset.getString("video_id");
                String hashtag = resultset.getString("hashtag").trim();

                listHashtag.add(video_id + "     " + hashtag);
            }
            ConnectionPool.closeConnection(resultset, statement, connect);
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
        }

        return listHashtag;
    }

    public boolean getAllAccountFromMySQL() {
        Date outOfTime = Utils.getOutOfTimeFromNow();
        listIps = new ArrayList<>();
        listUserNames = new ArrayList<>();
        listPassword = new ArrayList<>();
        listStatus = new ArrayList<>();
        listMyLogs = new ArrayList<>();
        listInfos = new ArrayList<>();
        listUpdateTimes = new ArrayList<>();
        listWarning = new ArrayList<>();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "SELECT * FROM account_dexuat;";
            System.out.println(query);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String ip = resultset.getString("ip");
                String username = resultset.getString("username").trim();
                String password = resultset.getString("password").trim();
                String status = resultset.getString("running");
                String mylog = resultset.getString("mylog").trim();
                String updateTimeString = resultset.getString("update_time");
                Date updateTime = null;
                if (updateTimeString != null) {
                    updateTime = simpleDateFormat.parse(updateTimeString);
                }

                listIps.add(ip);
                listUserNames.add(username);
                listPassword.add(password);
                listStatus.add(status);
                listMyLogs.add(mylog);
                listUpdateTimes.add(updateTime);

                String tempString = ip + "     " + username + "     " + status + "     " + mylog;
                listInfos.add(tempString);
                if (updateTime == null || updateTime.before(outOfTime) || username.length() == 0) {
                    listWarning.add(tempString);
                }
            }

            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean deleteAccountInMySQL(String groupIPs) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();
            String query = "DELETE FROM account_dexuat WHERE ip IN " + groupIPs + ";";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (SQLException e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean updateParameters(String min_time_second_my_video, String max_time_second_my_video,
            String min_time_second_other_video, String max_time_second_other_video,
            String target_videos, String other_videos, String comments) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "UPDATE parameters_dexuat SET value = CASE id"
                    + " WHEN 'min_time_second_my_video' THEN '" + min_time_second_my_video
                    + "' WHEN 'max_time_second_my_video' THEN '" + max_time_second_my_video
                    + "' WHEN 'min_time_second_other_video' THEN '" + min_time_second_other_video
                    + "' WHEN 'max_time_second_other_video' THEN '" + max_time_second_other_video
                    + "' WHEN 'other_videos' THEN '" + other_videos.trim()
                    + "' WHEN 'target_videos' THEN '" + target_videos.trim()
                    + "' WHEN 'comments' THEN '" + comments.trim()
                    + "' END WHERE id IN("
                    + "'max_time_second_my_video'"
                    + ", "
                    + "'min_time_second_my_video'"
                    + ", "
                    + "'max_time_second_other_video'"
                    + ", "
                    + "'min_time_second_other_video'"
                    + ", "
                    + "'other_videos'"
                    + ", "
                    + "'target_videos'"
                    + ", "
                    + "'comments'"
                    + ");";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean updateParametersClickSuggest(String min_time_second_my_channel, String max_time_second_my_channel,
            String min_time_second_source_video, String max_time_second_source_video,
            String channels, String sourceVideos, String comments) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "UPDATE parameters_dexuat SET value = CASE id"
                    + " WHEN 'min_time_second_my_channel' THEN '" + min_time_second_my_channel
                    + "' WHEN 'max_time_second_my_channel' THEN '" + max_time_second_my_channel
                    + "' WHEN 'min_time_second_source_video' THEN '" + min_time_second_source_video
                    + "' WHEN 'max_time_second_source_video' THEN '" + max_time_second_source_video
                    + "' WHEN 'channels' THEN '" + channels.trim()
                    + "' WHEN 'source_videos' THEN '" + sourceVideos.trim()
                    + "' WHEN 'comments_click_suggest' THEN '" + comments.trim()
                    + "' END WHERE id IN("
                    + "'min_time_second_my_channel'"
                    + ", "
                    + "'max_time_second_my_channel'"
                    + ", "
                    + "'min_time_second_source_video'"
                    + ", "
                    + "'max_time_second_source_video'"
                    + ", "
                    + "'channels'"
                    + ", "
                    + "'source_videos'"
                    + ", "
                    + "'comments_click_suggest'"
                    + ");";
            System.out.println(query);
            statement.executeUpdate(query);
            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            return false;
        }
    }

    public boolean updateParametersHomepage(String min_time_second, String max_time_second, String comments) {
        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        System.out.println(comments);

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "UPDATE parameters_dexuat SET value = CASE id WHEN 'min_time_second1' THEN "
                    + Integer.parseInt(min_time_second.trim()) + " WHEN 'max_time_second1' THEN "
                    + Integer.parseInt(max_time_second.trim()) + " WHEN 'comments1' THEN '"
                    + comments.trim() + "' END WHERE id IN('max_time_second1', 'min_time_second1', 'comments1');";
            System.out.println(query);
            statement.executeUpdate(query);

            ConnectionPool.closeConnection(resultset, statement, connect);
            return true;
        } catch (Exception e) {
            ConnectionPool.closeConnection(resultset, statement, connect);
            e.printStackTrace();
            return false;
        }
    }

    public String[] getParameterFromMySQL() {
        ArrayList<String> listParameters = new ArrayList<>();

        int max_time_second_my_video = -1;
        int min_time_second_my_video = -1;
        int max_time_second_other_video = -1;
        int min_time_second_other_video = -1;
        ArrayList<String> listComments = new ArrayList<>();
        ArrayList<String> listTargetVideos = new ArrayList<>();
        ArrayList<String> listOtherVideos = new ArrayList<>();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "SELECT * FROM parameters_dexuat;";
            System.out.println(query);
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
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closeConnection(resultset, statement, connect);
        }

        listParameters.add("=== Seo Suggest Parameters ===");
        listParameters.add("");

        listParameters.add("Watch my video random from " + min_time_second_my_video + "(s) to " + max_time_second_my_video + "(s)");
        listParameters.add("");

        listParameters.add("Watch other video random from " + min_time_second_other_video + "(s) to " + max_time_second_other_video + "(s)");
        listParameters.add("");

        listParameters.add("My Videos:");
        listParameters.add("");
        for (int i = 0; i < listTargetVideos.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listTargetVideos.get(i));
        }

        listParameters.add("");
        listParameters.add("Other Videos:");
        for (int i = 0; i < listOtherVideos.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listOtherVideos.get(i));
        }

        listParameters.add("");
        listParameters.add("Comments:");
        for (int i = 0; i < listComments.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listComments.get(i));
        }

        // extract into array
        String[] arrayParameters = new String[listParameters.size()];
        for (int i = 0; i < listParameters.size(); i++) {
            arrayParameters[i] = listParameters.get(i);
        }

        return arrayParameters;
    }

    public String[] getParameterHomepageFromMySQL() {
        ArrayList<String> listParameters = new ArrayList<>();

        int max_time_second1 = -1;
        int min_time_second1 = -1;
        ArrayList<String> listComments1 = new ArrayList<>();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "SELECT * FROM parameters_dexuat;";
            System.out.println(query);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String id = resultset.getString("id");
                String value = resultset.getString("value");
                if (id.compareTo("max_time_second1") == 0) {
                    max_time_second1 = Integer.parseInt(value.trim());
                } else if (id.compareTo("min_time_second1") == 0) {
                    min_time_second1 = Integer.parseInt(value.trim());
                } else if (id.compareTo("comments1") == 0) {
                    String[] arrayComments = value.split(",");
                    for (int i = 0; i < arrayComments.length; i++) {
                        listComments1.add(arrayComments[i].trim());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closeConnection(resultset, statement, connect);
        }

        listParameters.add("=== Seo Homepage Parameters ===");
        listParameters.add("");

        listParameters.add("Time Watch Video (min) = " + min_time_second1 + " (s)");
        listParameters.add("");

        listParameters.add("Time Watch Video (max) = " + max_time_second1 + " (s)");
        listParameters.add("");

        listParameters.add("Comments:");
        for (int i = 0; i < listComments1.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listComments1.get(i));
        }

        // extract into array
        String[] arrayParameters = new String[listParameters.size()];
        for (int i = 0; i < listParameters.size(); i++) {
            arrayParameters[i] = listParameters.get(i);
        }

        return arrayParameters;
    }

    public String[] getParameterClickSuggest() {
        ArrayList<String> listParameters = new ArrayList<>();

        int max_time_second_my_channel = -1;
        int min_time_second_my_channel = -1;
        int max_time_second_source_video = -1;
        int min_time_second_source_video = -1;
        ArrayList<String> listComments = new ArrayList<>();
        ArrayList<String> listChannels = new ArrayList<>();
        ArrayList<String> listSourceVideos = new ArrayList<>();

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        try {
            connect = ConnectionPool.getConnection();
            statement = connect.createStatement();

            String query = "SELECT * FROM parameters_dexuat;";
            System.out.println(query);
            resultset = statement.executeQuery(query);
            while (resultset.next()) {
                String id = resultset.getString("id");
                String value = resultset.getString("value");
                if (value != null) {
                    if (id.compareTo("max_time_second_my_channel") == 0) {
                        max_time_second_my_channel = Integer.parseInt(value.trim());
                    } else if (id.compareTo("min_time_second_my_channel") == 0) {
                        min_time_second_my_channel = Integer.parseInt(value.trim());
                    } else if (id.compareTo("max_time_second_source_video") == 0) {
                        max_time_second_source_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("min_time_second_source_video") == 0) {
                        min_time_second_source_video = Integer.parseInt(value.trim());
                    } else if (id.compareTo("comments_click_suggest") == 0) {
                        String[] arrayComments = value.split(",");
                        for (int i = 0; i < arrayComments.length; i++) {
                            listComments.add(arrayComments[i].trim());
                        }
                    } else if (id.compareTo("channels") == 0) {
                        String[] arrayVideos = value.split(",");
                        for (int i = 0; i < arrayVideos.length; i++) {
                            listChannels.add(arrayVideos[i].trim());
                        }
                    } else if (id.compareTo("source_videos") == 0) {
                        String[] arrayVideos = value.split(",");
                        for (int i = 0; i < arrayVideos.length; i++) {
                            listSourceVideos.add(arrayVideos[i].trim());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closeConnection(resultset, statement, connect);
        }

        listParameters.add("=== Seo Click Suggest Videos ===");
        listParameters.add("");

        listParameters.add("Watch my channel random from " + min_time_second_my_channel + "(s) to " + max_time_second_my_channel + "(s)");
        listParameters.add("");

        listParameters.add("Watch other video random from " + min_time_second_source_video + "(s) to " + max_time_second_source_video + "(s)");
        listParameters.add("");

        listParameters.add("My Channels:");
        listParameters.add("");
        for (int i = 0; i < listChannels.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listChannels.get(i));
        }

        listParameters.add("");
        listParameters.add("Source Videos:");
        for (int i = 0; i < listSourceVideos.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listSourceVideos.get(i));
        }

        listParameters.add("");
        listParameters.add("Comments:");
        for (int i = 0; i < listComments.size(); i++) {
            listParameters.add("     " + (i + 1) + ". " + listComments.get(i));
        }

        // extract into array
        String[] arrayParameters = new String[listParameters.size()];
        for (int i = 0; i < listParameters.size(); i++) {
            arrayParameters[i] = listParameters.get(i);
        }

        return arrayParameters;
    }

    public static boolean checkLogin(String username, String password) throws Exception {

        username = username.trim();
        boolean check = false;

        Connection connect = null;
        Statement statement = null;
        ResultSet resultset = null;
        connect = ConnectionPool.getConnection();
        statement = connect.createStatement();

        try {
            String query = "SELECT COUNT(*) FROM login WHERE username = '"
                    + username
                    + "' AND password = '"
                    + password
                    + "';";
            System.out.println("Check login...");
            resultset = statement.executeQuery(query);
            if (resultset.next()) {
                int count = Integer.parseInt(resultset.getString(1));
                if (count > 0) {
                    check = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionPool.closeConnection(resultset, statement, connect);
        }

        return check;
    }

    public static void main(String[] args) throws Exception {
        ServerControls serverControls = new ServerControls();
        System.out.println(checkLogin("test", "123"));
    }
}
