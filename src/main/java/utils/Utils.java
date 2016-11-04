package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import parameter.Parameters;

public class Utils {

    static public ArrayList<Integer> getListRandomNumbers(int size, int max_number) {
        ArrayList<Integer> listRandomNumbers = new ArrayList<Integer>();
        Random random = new Random();
        HashSet<Integer> setCheckDuplicate = new HashSet<Integer>();
        while (listRandomNumbers.size() < size) {
            int number = random.nextInt(max_number);
            if (!setCheckDuplicate.contains(number)) {
                setCheckDuplicate.add(number);
                listRandomNumbers.add(number);
            }
        }

        return listRandomNumbers;
    }

    static public int getRandomNumber(int min_value, int max_value) {
        if (min_value > max_value) {
            return -1;
        }

        Random random = new Random();
        int result = random.nextInt(max_value - min_value + 1) + min_value;
        return result;
    }

    public static String getIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            in.close();
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static ArrayList<String> sortArrayString(ArrayList<String> originList) {
        if (originList == null) {
            return new ArrayList<>();
        }
        String[] arrayString = new String[originList.size()];
        for (int i = 0; i < originList.size(); i++) {
            arrayString[i] = originList.get(i);
        }

        for (int i = 0; i < arrayString.length - 1; i++) {
            for (int j = i + 1; j < arrayString.length; j++) {
                if (arrayString[i].compareTo(arrayString[j]) > 0) {
                    String tempString = arrayString[i];
                    arrayString[i] = arrayString[j];
                    arrayString[j] = tempString;
                }
            }
        }

        ArrayList<String> sortedList = new ArrayList<>();
        for (int i = 0; i < arrayString.length; i++) {
            sortedList.add(arrayString[i]);
        }

        return sortedList;
    }

    public static Date getOutOfTimeFromNow() {
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.HOUR, -7);
        // c.add(Calendar.MINUTE, -15); 
        c.add(Calendar.SECOND, -1 * Parameters.warning_seconds);
        Date minDate = c.getTime();
        return minDate;
    }

    public static String normalizeText(String text) {
        text = text.replace("\"", "\\\"");
        text = text.replace("\'", "\\\'");
        return text;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getRandomNumber(0, 2));
    }
}
