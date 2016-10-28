package local;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import controls.LocalControls;
import controls.ServerControls;
import java.awt.RenderingHints;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Keys;
import parameter.Parameters;
import utils.Utils;

public class MainWithMySQL {

    final int max_comments = 1;
    HashMap<String, Integer> mapLimitComments = new HashMap<>();

    WebDriver driver;
    WebDriverWait wait;

    int num_video_target = 0;
    int num_video_other = 0;

    Parameters parameters;
    LocalControls myLogs;

    static public DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    int iter = 0;

    public MainWithMySQL() {
        parameters = new Parameters();
        myLogs = new LocalControls();
    }

    public MainWithMySQL(String myIp) {
        parameters = new Parameters(myIp);
        myLogs = new LocalControls(myIp);
    }

    // login _youtube
    void login() {
        driver.get("https://accounts.google.com/ServiceLogin?passive=true&continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3Faction_handle_signin%3Dtrue%26app%3Ddesktop%26feature%3Dsign_in_button%26next%3D%252F%26hl%3Den&service=youtube&uilel=3&hl=en#identifier");
        driver.findElement(By.id("Email")).sendKeys(parameters.username);
        driver.findElement(By.id("next")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Passwd")));

        driver.findElement(By.id("Passwd")).sendKeys(parameters.password);
        driver.findElement(By.id("signIn")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-positioner")));

        System.out.println("Login success! " + parameters.username);
    }

    // change language --> English (in case it was not in English)
    void changeLanguage() throws InterruptedException {
        driver.get("https://www.youtube.com/my_videos?o=U");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("yt-picker-language-button")));
        Thread.sleep(1000);
        System.out.println(1);
        driver.findElement(By.id("yt-picker-language-button")).click();
        System.out.println(2);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//strong[@class=\"yt-picker-item\"]")));
        System.out.println(3);
        System.out.println(driver.findElement(By.xpath("//strong[@class=\"yt-picker-item\"]")).getText());
        Thread.sleep(1000);
        if (driver.findElement(By.xpath("//strong[@class=\"yt-picker-item\"]")).getText().compareTo("English (US)") != 0) {
            driver.findElement(By.xpath("//button[@value=\"en\"]")).click();
        }
        // wait until change done
        while (true) {
            System.out.println("Wait until change to English done....");
            if (driver.findElement(By.xpath("//link[@rel=\"search\"]")).getAttribute("href").toString().contains("locale=en_US")) {
                break;
            }
            Thread.sleep(500);
        }

        System.out.println("Change language done! English!");
    }

    // change location --> US (in case it was not in US)
    void changeLocation() throws InterruptedException {
        driver.get("https://www.youtube.com/?persist_gl=1&gl=US");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-positioner")));
        System.out.println("Change location done! United States!");
    }

    void init_main() throws InterruptedException, AWTException, IOException {
        System.setProperty("webdriver.chrome.driver", Parameters.file_driver);
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File("geckodriver/extension.crx"));
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 100);

        // login _youtube
        login();

        // close extension windows
        String base = driver.getWindowHandle();
        Set<String> set = driver.getWindowHandles();
        set.remove(base);
        assert set.size() == 1;
        driver.switchTo().window((String) set.toArray()[0]);
        driver.close();
        driver.switchTo().window(base);

        // change location
        // changeLocation();
        System.out.println("Done init!");
    }

    void watchVideo(String url) throws InterruptedException {
        System.out.println(url);
        driver.get(url);

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");

        int watch_time = Utils.getRandomNumber(parameters.min_time_second, parameters.max_time_second);
        System.out.println("watch_time = " + watch_time + "(s)");

        String log = simpleDateFormat.format(new Date()) + "     loop " + (iter + 1) + "     watching " + url + "     time " + watch_time + "(s)";
        try {
            myLogs.saveLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        watch_time = watch_time * 1000;
        Thread.sleep(watch_time);

        if (driver.findElements(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).size() > 0) {
            if (!driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).getAttribute("class").contains("hid yt-uix-tooltip")) {
                System.out.println("Action like");
                driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).click();
            }
        }

        // check_comment
        boolean checkComment = false;
        if (!mapLimitComments.containsKey(url)) {
            int temp = Utils.getRandomNumber(0, 1);
            if (temp == 1) {
                checkComment = true;
                mapLimitComments.put(url, 1);
            }
        } else if (mapLimitComments.get(url) < max_comments) {
            int temp = Utils.getRandomNumber(0, 1);
            if (temp == 1) {
                checkComment = true;
                mapLimitComments.put(url, mapLimitComments.get(url) + 1);
            }
        }
        if (checkComment && driver.findElements(By.className("comment-simplebox-renderer-collapsed-content")).size() > 0) {
            driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).click();

            while (!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
                System.out.println("waiting comment ...");
                Thread.sleep(1000);
            }
            int index = Utils.getRandomNumber(0, parameters.listComments.size() - 1);
            String comment = parameters.listComments.get(index);
            System.out.println("comment: " + comment);
            driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).sendKeys(comment);
            while (!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).isDisplayed()) {
                System.out.println("waiting submit comment ...");
                Thread.sleep(1000);
            }
            driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
            while (driver.findElements(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).size() > 0) {
                System.out.println("waiting submit done ...");
                Thread.sleep(1000);
            }
        }

        Thread.sleep(2000);
    }

    void startSeoSuggest() throws Exception {
        myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
        init_main();

        // String targetVideo = parameters.listTargetVideos.get(0);
        int sizeTargetVideo = parameters.listTargetVideos.size();
        int sizeOtherVideo = parameters.listOtherVideos.size();

        int times = 0;
        iter = 0;
        while (true) {
            System.out.println("Repeat: " + (++times));
            ArrayList<Integer> listIndexs = Utils.getListRandomNumbers(sizeOtherVideo, sizeOtherVideo);
            for (int i = 0; i < listIndexs.size(); i++) {
                int index = listIndexs.get(i);
                String otherVideo = parameters.listOtherVideos.get(index);

                // other video
                watchVideo(otherVideo);
                if (myLogs.checkStop() == true) {
                    break;
                }

                // target video
                int indexTarget = Utils.getRandomNumber(0, sizeTargetVideo - 1);
                String targetVideo = parameters.listTargetVideos.get(indexTarget);
                watchVideo(targetVideo);
                if (myLogs.checkStop() == true) {
                    break;
                }
            }

            iter++;
            System.out.println();

            // check status
            if (myLogs.checkStop() == true) {
                driver.close();
                System.out.println("Success!");
                myLogs.saveLog("Success!");
                myLogs.setStatus(0);
                break;
            }
        }
    }

    void searchVideo(String id_hashtag, int indexVideo) throws InterruptedException {
        System.out.println(id_hashtag);
        driver.get("https://www.youtube.com/");
        while (!driver.findElement(By.id("masthead-appbar-container")).isDisplayed()) {
            System.out.println("waiting youtube.com ...");
            Thread.sleep(1000);
        }
        driver.findElement(By.id("masthead-search-term")).sendKeys(id_hashtag.split("     ")[1] + " \"" + id_hashtag.split("     ")[0] + "\"");
        Thread.sleep(1000);
        driver.findElement(By.id("masthead-search-term")).sendKeys(Keys.ENTER);

        Thread.sleep(1000);
        while (driver.findElement(By.id("masthead-appbar-container")).isDisplayed()) {
            System.out.println("waiting search done ...");
            Thread.sleep(1000);
        }
        driver.findElement(By.xpath("//*/li/div/div/div[2]/h3/a")).click();

        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");

        int watch_time = Utils.getRandomNumber(parameters.min_time_second1, parameters.max_time_second1);
        System.out.println("watch_time = " + watch_time + "(s)");

        String log = simpleDateFormat.format(new Date()) + "     watching video " + indexVideo + "     " + id_hashtag + "     time " + watch_time + "(s)";
        try {
            myLogs.saveLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        watch_time = watch_time * 1000;
        Thread.sleep(watch_time);

        // random like
        int checkLike = Utils.getRandomNumber(0, 1);
        if (checkLike == 1) {
            if (driver.findElements(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).size() > 0) {
                if (!driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).getAttribute("class").contains("hid yt-uix-tooltip")) {
                    System.out.println("Like video!");
                    driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).click();
                }
            }
        }

        // check_comment
        int checkComment = Utils.getRandomNumber(0, 1);
        if (checkComment == 1 && driver.findElements(By.className("comment-simplebox-renderer-collapsed-content")).size() > 0) {
            driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).click();

            while (!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
                System.out.println("waiting comment ...");
                Thread.sleep(1000);
            }
            int index = Utils.getRandomNumber(0, parameters.listComments1.size() - 1);
            String comment = parameters.listComments1.get(index);
            System.out.println("Comment video: " + comment);
            driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).sendKeys(comment);
            while (!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).isDisplayed()) {
                System.out.println("waiting submit comment ...");
                Thread.sleep(1000);
            }
            driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
            while (driver.findElements(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).size() > 0) {
                System.out.println("waiting submit done ...");
                Thread.sleep(1000);
            }
        }

        Thread.sleep(2000);
    }

    void startSeoHomepage() throws Exception {
        myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
        init_main();
        ServerControls serverController = new ServerControls();

        // get list hashtags from mysql
        ArrayList<String> listHashtags = serverController.getAllHashtagFromMySQL();
        System.out.println(listHashtags);

        ArrayList<Integer> listIndexs = Utils.getListRandomNumbers(listHashtags.size(), listHashtags.size());
        System.out.println(listIndexs);
        for (int i = 0; i < listIndexs.size(); i++) {
            int index = listIndexs.get(i);
            String id_hashtag = listHashtags.get(index);

            // other video
            searchVideo(id_hashtag, i + 1);
            if (myLogs.checkStop() == true) {
                break;
            }
        }

        driver.close();
        System.out.println("Success!");
        myLogs.saveLog("Success!");
        myLogs.setStatus(0);
    }

    public static void main(String[] args) throws Exception {
        String myIp = Utils.getIp();
        if (args.length > 0) {
            myIp = args[0];
        }

        // myIp = "1.2.3.4";
        LocalControls.insertAccount(myIp, "", "");

        int count = 0;
        while (true) {
            System.out.println(++count);
            MainWithMySQL mainObject = new MainWithMySQL(myIp);
            if (mainObject.parameters.username.length() > 0) {
                if (mainObject.parameters.status.compareTo("1") == 0) {
                    mainObject.startSeoSuggest();
                } else if (mainObject.parameters.status.compareTo("2") == 0) {
                    mainObject.startSeoHomepage();
                }
            }

            System.out.println(myIp + " waiting 1s util status = 1 or 2");
            Thread.sleep(1000);
            mainObject.myLogs.saveLog(simpleDateFormat.format(new Date()) + " waiting... ");
        }
    }
}
