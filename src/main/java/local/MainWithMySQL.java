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
        mapLimitComments = new HashMap<>();

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

    void watchVideo(String url) throws InterruptedException, Exception {
        url = url + "&t=1s";
        System.out.println(url);
        driver.get(url);

        // scroll dơwn
        Thread.sleep(1000);
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0,250)", "");
        Thread.sleep(1000);

        int watch_time = Utils.getRandomNumber(parameters.min_time_second, parameters.max_time_second);
        System.out.println("watch_time = " + watch_time + "(s)");

        String log = simpleDateFormat.format(new Date()) + "     loop " + (iter + 1) + "     watching " + url + "     time " + watch_time + "(s)";
        try {
            myLogs.saveLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int max_step = watch_time;
        int step = 0;
        while (step < max_step) {
            step++;
            if (step % 10 == 0) {
                
                String tempChannel = driver.findElement(By.xpath("//*[@id=\"watch7-sidebar-modules\"]/div[1]/div/div[2]/ul/li/div[1]/a/span[3]/span")).getAttribute("data-ytid");
                System.out.println(tempChannel);
                driver.findElement(By.xpath("//*[@id=\"watch7-sidebar-modules\"]/div[1]/div/div[2]/ul/li/div[1]/a")).sendKeys(Keys.ENTER);
                
                if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(url) != 0) {
                    return;
                }
            }
            Thread.sleep(1000);
        }

        if (driver.getCurrentUrl().compareTo(url) == 0) {
            // check like a comment
            if (Utils.getRandomNumber(0, 1000) % 2 == 1 && mapLimitComments.getOrDefault(url, 0) < max_comments) {
                if (mapLimitComments.containsKey(url)) {
                    mapLimitComments.put(url, mapLimitComments.get(url) + 1);
                } else {
                    mapLimitComments.put(url, 1);
                }

                try {
                    int indexSection = Utils.getRandomNumber(1, 1000) % 10 + 1;
                    System.out.println("index comment to like and reply: " + indexSection);
                    if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section[" + indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).isDisplayed()) {
                        if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section[" + indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).getAttribute("data-action-on") == null) {
                            System.out.println("Action like a comment");
                            driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section[" + indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).click();

                            // reply comment
                            Thread.sleep(1000);
                            if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section[" + indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]")).isDisplayed()) {
                                System.out.println("Action reply a comment");
                                driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section[" + indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]")).click();
                                Thread.sleep(1000);
                                int indexComment = Utils.getRandomNumber(0, parameters.listComments.size() - 1);
                                String reply = parameters.listComments.get(indexComment);
                                System.out.println("reply: " + reply);
                                driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).sendKeys(reply);
                                Thread.sleep(1000);
                                driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
                                Thread.sleep(2000);
                            }
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.err.println("Error like a comment!");
                }
            }

            // subscribe
            if (Utils.getRandomNumber(0, 1000) % 2 == 1) {
                Thread.sleep(1000);
                try {
                    if (driver.findElement(By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span/span[1]")).isDisplayed()) {
                        System.out.println("Action subscribe");
                        driver.findElement(By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span")).click();
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void startSeoSuggest() throws Exception {
        myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
        init_main();
        Parameters.warning_seconds = parameters.max_time_second;

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
                if (sizeTargetVideo >= 1) {
                    int indexTarget = Utils.getRandomNumber(0, 1000) % (sizeTargetVideo);
                    String targetVideo = parameters.listTargetVideos.get(indexTarget);
                    watchVideo(targetVideo);
                    if (myLogs.checkStop() == true) {
                        break;
                    }
                }
            }

            iter++;
            System.out.println();

            // check status
            if (myLogs.checkStop() == true) {
                driver.quit();
                System.out.println("Stopping...!");
                myLogs.saveLog("Stopping...!");
                myLogs.setStatus(0);
                break;
            }
        }
    }

    void searchVideo(String id_hashtag, int indexVideo) throws InterruptedException, Exception {
        System.out.println(id_hashtag);
        driver.get("https://www.youtube.com/");

        int secondWait = 0;
        try {
            while (secondWait < Parameters.max_second_wait && !driver.findElement(By.id("masthead-search-term")).isDisplayed()) {
                System.out.println("waiting youtube.com load done ...");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        try {
            if (driver.findElement(By.id("masthead-search-term")).isDisplayed()) {
                driver.findElement(By.id("masthead-search-term")).sendKeys(id_hashtag.split("     ")[1] + " \"" + id_hashtag.split("     ")[0] + "\"");
                Thread.sleep(1000);
                driver.findElement(By.id("masthead-search-term")).sendKeys(Keys.ENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        secondWait = 0;
        try {
            while (secondWait < parameters.max_second_wait && driver.findElement(By.id("masthead-appbar-container")).isDisplayed()) {
                System.out.println("waiting search done ...");
                Thread.sleep(1000);
                secondWait++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        try {
            if (driver.findElement(By.xpath("//*/li/div/div/div[2]/h3/a")).isDisplayed()) {
                driver.findElement(By.xpath("//*/li/div/div/div[2]/h3/a")).click();
                JavascriptExecutor jse = (JavascriptExecutor) driver;
                jse.executeScript("window.scrollBy(0,250)", "");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String url = driver.getCurrentUrl();
        int watch_time = Utils.getRandomNumber(parameters.min_time_second1, parameters.max_time_second1);
        System.out.println("watch_time = " + watch_time + "(s)");

        String log = simpleDateFormat.format(new Date()) + "     watching video " + indexVideo + "     " + id_hashtag + "     time " + watch_time + "(s)";
        try {
            myLogs.saveLog(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int max_step = watch_time;
        int step = 0;
        while (step < max_step) {
            step++;
            if (step % 10 == 0) {
                if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(url) != 0) {
                    return;
                }
            }
            Thread.sleep(1000);
        }

        if (driver.getCurrentUrl().compareTo(url) == 0) {
            // random like
            int checkLike = Utils.getRandomNumber(0, 100);
            if (checkLike % 2 == 1) {
                try {
                    if (driver.findElements(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).size() > 0) {
                        if (!driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).getAttribute("class").contains("hid yt-uix-tooltip")) {
                            System.out.println("Like video!");
                            driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).click();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // check_comment                        
            int checkComment = Utils.getRandomNumber(0, 100);
            try {
                if (checkComment % 2 == 1 && driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).isDisplayed()) {
                    driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).click();

                    secondWait = 0;
                    while (secondWait < parameters.max_second_wait && !driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
                        System.out.println("waiting comment ...");
                        Thread.sleep(1000);
                        secondWait++;
                    }

                    if (driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
                        int index = Utils.getRandomNumber(0, parameters.listComments1.size() - 1);
                        String comment = parameters.listComments1.get(index);
                        System.out.println("Comment video: " + comment);
                        driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).sendKeys(comment);
                    }

                    secondWait = 0;
                    while (secondWait < parameters.max_second_wait && !driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).isDisplayed()) {
                        System.out.println("waiting submit comment ...");
                        Thread.sleep(1000);
                        secondWait++;
                    }

                    if (driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).isDisplayed()) {
                        driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
                    }

                    secondWait = 0;
                    while (secondWait < parameters.max_second_wait && driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
                        System.out.println("waiting submit done ...");
                        Thread.sleep(1000);
                        secondWait++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread.sleep(2000);
        }
    }

    void startSeoHomepage() throws Exception {
        myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
        init_main();
        Parameters.warning_seconds = parameters.max_time_second1;
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

        driver.quit();
        System.out.println("Stopping...!");
        myLogs.saveLog("Stopping...!");
        myLogs.setStatus(0);
    }

    public static void main(String[] args) throws Exception {

        System.out.println("=== author: mrtamb9 ===");
        String myIp = Utils.getIp();
        if (args.length > 0) {
            myIp = args[0];
        }

        myIp = "1.2.3.4";

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
