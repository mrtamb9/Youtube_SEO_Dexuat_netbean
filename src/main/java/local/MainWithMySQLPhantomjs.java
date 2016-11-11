package local;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sun.jna.platform.FileUtils;

import controls.LocalControls;
import controls.ServerControls;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.*;

import parameter.Parameters;
import utils.Utils;

public class MainWithMySQLPhantomjs {

	final int max_comments = 1;
	HashMap<String, Integer> mapLimitCommentSeoSuggest = new HashMap<>();
	HashMap<String, Integer> mapLimitCommentClickSuggest = new HashMap<>();

	WebDriver driver;
	WebDriverWait wait;

	int num_video_target = 0;
	int num_video_other = 0;

	Parameters parameters;
	LocalControls myLogs;

	static DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

	int iter = 0;

	public MainWithMySQLPhantomjs() {
		parameters = new Parameters();
		myLogs = new LocalControls();
	}

	public MainWithMySQLPhantomjs(String myIp) {
		parameters = new Parameters(myIp);
		myLogs = new LocalControls(myIp);
	}

	// change language --> English (in case it was not in English)
	void changeLanguage() {
		driver.get("https://www.youtube.com/my_videos?o=U");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("yt-picker-language-button")));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}
		driver.findElement(By.id("yt-picker-language-button")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//strong[@class=\"yt-picker-item\"]")));
		System.out.println(driver.findElement(By.xpath("//strong[@class=\"yt-picker-item\"]")).getText());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}
		if (driver.findElement(By.xpath("//strong[@class=\"yt-picker-item\"]")).getText()
				.compareTo("English (US)") != 0) {
			driver.findElement(By.xpath("//button[@value=\"en\"]")).click();
		}
		// wait until change done
		while (true) {
			System.out.println("Wait until change to English done....");
			if (driver.findElement(By.xpath("//link[@rel=\"search\"]")).getAttribute("href").toString()
					.contains("locale=en_US")) {
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				// do nothing
			}
		}
		System.out.println("Change language done! English!");
	}

	// change location --> US (in case it was not in US)
	void changeLocation() {
		driver.get("https://www.youtube.com/?persist_gl=1&gl=US");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
			Logger.getLogger(MainWithMySQLPhantomjs.class.getName()).log(Level.SEVERE, null, ex);
		}
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-search-term")));
		} catch (Exception e) {
			quitDriver();
		}
		System.out.println("Change location done! United States!");
	}

	void quitDriver() {
		driver.quit();
	}

	void loadDriver() {
		// http://phantomjs.org/download.html
		Capabilities caps = new DesiredCapabilities();
		((DesiredCapabilities) caps).setJavascriptEnabled(true);
		((DesiredCapabilities) caps).setCapability("takesScreenshot", true);
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				"phantomjs/phantomjs.exe");

		String[] phantomArgs = new String[] { "--webdriver-loglevel=NONE" };
		((DesiredCapabilities) caps).setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);

		driver = new PhantomJSDriver(caps);

		wait = new WebDriverWait(driver, 100);
	}

	// login _youtube
	void login() {
		try {
			System.out.println("Login...");
			driver.get(
					"https://accounts.google.com/ServiceLogin?passive=true&continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3Faction_handle_signin%3Dtrue%26app%3Ddesktop%26feature%3Dsign_in_button%26next%3D%252F%26hl%3Den&service=youtube&uilel=3&hl=en#identifier");
			driver.findElement(By.id("Email")).sendKeys(parameters.username);
			Thread.sleep(500);
			driver.findElement(By.id("next")).click();

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Passwd")));

			System.out.println("a");
			driver.findElement(By.id("Passwd")).sendKeys(parameters.password);
			Thread.sleep(500);
			driver.findElement(By.id("signIn")).click();
			System.out.println("b");
			Thread.sleep(5000);

			// dang nhap loi thi van vao view binh thuong: comment dong duoi lai
			// wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-search-term")));

			System.out.println("Login Done! " + parameters.username);

			// Now you can do whatever you need to do with it, for example copy
			// somewhere
			Thread.sleep(1000);
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			org.apache.commons.io.FileUtils.copyFile(scrFile, new File("screenshot.jpg"));

		} catch (Exception e) {
			e.printStackTrace();
			quitDriver();
		}

		System.out.println(driver.getCurrentUrl());
	}

	void init_main() {
		mapLimitCommentSeoSuggest = new HashMap<>();
		mapLimitCommentClickSuggest = new HashMap<>();

		// load driver and load cookie
		loadDriver();

		// login _youtube
		login();
		if (driver == null) {
			System.out.println("login fail! quit driver");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				// do nothing
			}
			System.exit(1);
		}

		// change location
		// changeLocation();
		System.out.println("Done init!");
	}

	void watchVideo(String url, int min_time, int max_time) {
		url = url + "&t=1s";
		System.out.println(url);
		driver.get(url);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException ex) {
			// do nothing
		}

		// scroll down
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,250)", "");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}

		int watch_time = Utils.getRandomNumber(min_time, max_time);
		System.out.println("watch_time = " + watch_time + "(s)");

		String log = simpleDateFormat.format(new Date()) + "     loop " + (iter + 1) + "     watching " + url
				+ "     time " + watch_time + "(s)";
		try {
			myLogs.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int max_step = watch_time;
		int step = 0;
		while (step < max_step) {
			step++;
			if (step % 60 == 0) {
				if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(url) != 0) {
					return;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				// do nothing
			}
		}

		if (driver.getCurrentUrl().compareTo(url) == 0) {
			// check like a comment
			if (Utils.getRandomNumber(0, 1000) % 2 == 1
					&& mapLimitCommentSeoSuggest.getOrDefault(url, 0) < max_comments) {

				try {
					int indexSection = Utils.getRandomNumber(1, 1000) % 10 + 1;
					System.out.println("index comment to like and reply: " + indexSection);
					if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
							+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).isDisplayed()) {
						if (driver
								.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]"))
								.getAttribute("data-action-on") == null) {
							System.out.println("Action like a comment");
							driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
									+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).click();

							// reply comment
							Thread.sleep(1000);
							if (driver
									.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
											+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]"))
									.isDisplayed()) {
								System.out.println("Action reply a comment");
								driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]")).click();
								Thread.sleep(1000);
								int indexComment = Utils.getRandomNumber(0, parameters.listComments.size() - 1);
								String reply = parameters.listComments.get(indexComment);
								System.out.println("reply: " + reply);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]"))
										.sendKeys(reply);
								Thread.sleep(1000);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]"))
										.click();
								Thread.sleep(2000);
							}
						}
					}

					if (mapLimitCommentSeoSuggest.containsKey(url)) {
						mapLimitCommentSeoSuggest.put(url, mapLimitCommentSeoSuggest.get(url) + 1);
					} else {
						mapLimitCommentSeoSuggest.put(url, 1);
					}

					Thread.sleep(1000);
				} catch (Exception e) {
					System.err.println("Like a comment fail!");
				}
			}

			// subscribe
			if (Utils.getRandomNumber(0, 1000) % 2 == 1) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					// do nothing
				}
				try {
					if (driver
							.findElement(
									By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span/span[1]"))
							.isDisplayed()) {
						System.out.println("Action subscribe");
						driver.findElement(By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span"))
								.click();
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					System.out.println("Subsribe fail!");
				}
			}
		}
	}

	void startSeoSuggest() {
		myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
		init_main();

		parameters.warning_seconds = parameters.max_time_second_my_video;

		// String targetVideo = parameters.listTargetVideos.get(0);
		int sizeTargetVideo = parameters.listTargetVideos.size();
		int sizeOtherVideo = parameters.listOtherVideos.size();
		// System.out.println(parameters.listTargetVideos);
		// System.out.println(parameters.listOtherVideos);

		int times = 0;
		iter = 0;

		while (true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				Logger.getLogger(MainWithMySQLPhantomjs.class.getName()).log(Level.SEVERE, null, ex);
			}
			System.out.println("Repeat: " + (++times));
			ArrayList<Integer> listIndexs = Utils.getListRandomNumbers(sizeOtherVideo, sizeOtherVideo);
			System.out.println(listIndexs);
			for (int i = 0; i < listIndexs.size(); i++) {
				int index = listIndexs.get(i);
				String otherVideo = parameters.listOtherVideos.get(index);

				// other video
				watchVideo(otherVideo, parameters.min_time_second_other_video, parameters.max_time_second_other_video);
				if (myLogs.checkStop() == true) {
					break;
				}

				// target video
				if (sizeTargetVideo >= 1) {
					int indexTarget = Utils.getRandomNumber(0, 1000) % (sizeTargetVideo);
					String targetVideo = parameters.listTargetVideos.get(indexTarget);
					watchVideo(targetVideo, parameters.min_time_second_my_video, parameters.max_time_second_my_video);
					if (myLogs.checkStop() == true) {
						break;
					}
				}
			}

			iter++;
			System.out.println();

			// check status
			if (myLogs.checkStop() == true) {
				quitDriver();
				System.out.println("Stopping...!");
				myLogs.saveLog("Stopping...!");
				myLogs.setStatus(0);
				break;
			}
		}
	}

	void searchVideo(String id_hashtag, int indexVideo) {
		System.out.println(id_hashtag);
		driver.get("https://www.youtube.com/");

		int secondWait = 0;
		try {
			while (secondWait < parameters.max_second_wait
					&& !driver.findElement(By.id("masthead-search-term")).isDisplayed()) {
				System.out.println("waiting youtube.com load done ...");
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(driver.getCurrentUrl());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}
		try {
			if (driver.findElement(By.id("masthead-search-term")).isDisplayed()) {
				driver.findElement(By.id("masthead-search-term"))
						.sendKeys(id_hashtag.split("     ")[1] + " \"" + id_hashtag.split("     ")[0] + "\"");
				Thread.sleep(1000);
				driver.findElement(By.id("masthead-search-term")).sendKeys(Keys.ENTER);
			}
		} catch (Exception e) {
			System.out.println("Search hashtag fail!");
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}
		secondWait = 0;
		try {
			while (secondWait < parameters.max_second_wait
					&& driver.findElement(By.id("masthead-appbar-container")).isDisplayed()) {
				System.out.println("waiting search done ...");
				Thread.sleep(1000);
				secondWait++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(driver.getCurrentUrl());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			// do nothing
		}
		try {
			if (driver.findElement(By.xpath("//*/li/div/div/div[2]/h3/a")).isDisplayed()) {
				driver.findElement(By.xpath("//*/li/div/div/div[2]/h3/a")).click();
				Thread.sleep(2000);
				JavascriptExecutor jse = (JavascriptExecutor) driver;
				jse.executeScript("window.scrollBy(0,250)", "");
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			System.out.println("Search no result!");
			return;
		}

		System.out.println(driver.getCurrentUrl());

		String url = driver.getCurrentUrl();
		int watch_time = Utils.getRandomNumber(parameters.min_time_second1, parameters.max_time_second1);
		System.out.println("watch_time = " + watch_time + "(s)");

		String log = simpleDateFormat.format(new Date()) + "     watching video " + indexVideo + "     " + id_hashtag
				+ "     time " + watch_time + "(s)";
		try {
			myLogs.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int max_step = watch_time;
		int step = 0;
		while (step < max_step) {
			step++;
			if (step % 60 == 0) {
				if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(url) != 0) {
					return;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				// do nothing
			}
		}

		System.out.println(driver.getCurrentUrl());

		if (driver.getCurrentUrl().compareTo(url) == 0) {
			// random like
			int checkLike = Utils.getRandomNumber(0, 100);
			if (checkLike % 2 == 1) {
				try {
					if (driver.findElements(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button"))
							.size() > 0) {
						if (!driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button"))
								.getAttribute("class").contains("hid yt-uix-tooltip")) {
							System.out.println("Like video!");
							driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button"))
									.click();
						}
					}
				} catch (Exception e) {
					System.out.println("Like fail!");
				}
			}

			// check_comment
			int checkComment = Utils.getRandomNumber(0, 100);
			try {
				if (checkComment % 2 == 1 && driver
						.findElement(By.className("comment-simplebox-renderer-collapsed-content")).isDisplayed()) {
					driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).click();

					secondWait = 0;
					while (secondWait < parameters.max_second_wait && !driver
							.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
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
					while (secondWait < parameters.max_second_wait
							&& !driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]"))
									.isDisplayed()) {
						System.out.println("waiting submit comment ...");
						Thread.sleep(1000);
						secondWait++;
					}

					if (driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]"))
							.isDisplayed()) {
						driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
					}

					secondWait = 0;
					while (secondWait < parameters.max_second_wait && driver
							.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed()) {
						System.out.println("waiting submit done ...");
						Thread.sleep(1000);
						secondWait++;
					}
				}
			} catch (Exception e) {
				System.out.println("Comment fail!");
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				// do nothing
			}
		}
	}

	void startSeoKeyword() {
		myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
		init_main();
		parameters.warning_seconds = parameters.max_time_second1;
		ServerControls serverController = new ServerControls();

		// get list _hashtags from _mysql
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

		quitDriver();
		System.out.println("Stopping...!");
		myLogs.saveLog("Stopping...!");
		myLogs.setStatus(0);
	}

	void startClickSuggest() {
		myLogs.saveLog(simpleDateFormat.format(new Date()) + " starting... " + (new Date()));
		init_main();
		parameters.warning_seconds = parameters.max_time_second_my_channel;

		int sizeSourceVideo = parameters.listSourceVideos.size();

		int times = 0;
		iter = 0;

		while (true) {
			System.out.println("Repeat: " + (++times));
			ArrayList<Integer> listIndexs = Utils.getListRandomNumbers(sizeSourceVideo, sizeSourceVideo);
			for (int i = 0; i < listIndexs.size(); i++) {
				int index = listIndexs.get(i);
				String source_video = parameters.listSourceVideos.get(index);
				clickSuggestVideo(source_video);
				if (myLogs.checkStop() == true) {
					break;
				}
			}

			iter++;
			System.out.println();

			// check status
			if (myLogs.checkStop() == true) {
				quitDriver();
				System.out.println("Stopping...!");
				myLogs.saveLog("Stopping...!");
				myLogs.setStatus(0);
				break;
			}
		}
	}

	void clickSuggestVideo(String source_video) {
		source_video = source_video + "&t=1s";
		System.out.println(source_video);
		driver.get(source_video);

		// scroll dơwn
		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}

		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,250)", "");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		int watch_time_source_video = Utils.getRandomNumber(parameters.min_time_second_source_video,
				parameters.max_time_second_source_video);
		System.out.println("watch_time = " + watch_time_source_video + "(s)");

		String log = simpleDateFormat.format(new Date()) + "     loop " + (iter + 1) + "     watching " + source_video
				+ "     time " + watch_time_source_video + "(s)";
		try {
			myLogs.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int max_step = watch_time_source_video;
		int step = 0;
		while (step < max_step) {
			step++;
			if (step % 60 == 0) {
				if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(source_video) != 0) {
					return;
				}

				try {
					driver.findElement(By.id("watch-more-related-button")).click();
				} catch (Exception e) {
					// do nothing
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}

		if (driver.getCurrentUrl().compareTo(source_video) == 0) {
			// check like a comment
			if (Utils.getRandomNumber(0, 1000) % 2 == 1
					&& mapLimitCommentClickSuggest.getOrDefault(source_video, 0) < max_comments) {
				if (mapLimitCommentClickSuggest.containsKey(source_video)) {
					mapLimitCommentClickSuggest.put(source_video, mapLimitCommentClickSuggest.get(source_video) + 1);
				} else {
					mapLimitCommentClickSuggest.put(source_video, 1);
				}

				try {
					int indexSection = Utils.getRandomNumber(1, 1000) % 10 + 1;
					System.out.println("index comment to like and reply: " + indexSection);
					if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
							+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).isDisplayed()) {
						if (driver
								.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]"))
								.getAttribute("data-action-on") == null) {
							System.out.println("Action like a comment");
							driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
									+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).click();

							// reply comment
							Thread.sleep(1000);
							if (driver
									.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
											+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]"))
									.isDisplayed()) {
								System.out.println("Action reply a comment");
								driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]")).click();
								Thread.sleep(1000);
								int indexComment = Utils.getRandomNumber(0, parameters.listCommentsSuggest.size() - 1);
								String reply = parameters.listCommentsSuggest.get(indexComment);
								System.out.println("reply: " + reply);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]"))
										.sendKeys(reply);
								Thread.sleep(1000);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]"))
										.click();
								Thread.sleep(2000);
							}
						}
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					System.err.println("Like a comment fail!");
				}
			}

			// subscribe
			if (Utils.getRandomNumber(0, 1000) % 2 == 1) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				try {
					if (driver
							.findElement(
									By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span/span[1]"))
							.isDisplayed()) {
						System.out.println("Action subscribe");
						driver.findElement(By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span"))
								.click();
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					System.out.println("Subsribe fail!");
				}
			}
		}

		// click suggest video
		// Check all channel of all video suggested --> list my video channel
		// Random one video and click
		// watch video
		// System.out.println(parameters.listChannels.toString());
		ArrayList<String> listXpathElements = new ArrayList<>();
		try {

			String tempChannel = driver
					.findElement(By
							.xpath("//*[@id=\"watch7-sidebar-modules\"]/div[1]/div/div[2]/ul/li/div[1]/a/span[3]/span"))
					.getAttribute("data-ytid");
			// System.out.println(tempChannel);
			if (parameters.listChannels.contains(tempChannel)) {
				listXpathElements.add("//*[@id=\"watch7-sidebar-modules\"]/div[1]/div/div[2]/ul/li/div[1]/a");
			}
		} catch (Exception e) {
			// do nothing
		}

		for (int i = 1; i <= 19; i++) {
			try {
				String tempChannel = driver
						.findElement(By.xpath("//*[@id=\"watch-related\"]/li[" + i + "]/div[1]/div[1]/a/span[3]/span"))
						.getAttribute("data-ytid");
				// System.out.println(tempChannel);
				if (parameters.listChannels.contains(tempChannel)) {
					listXpathElements.add("//*[@id=\"watch-related\"]/li[" + i + "]/div[1]/div[1]/a");
				}
			} catch (Exception e) {
				// do nothing
			}
		}

		for (int i = 1; i <= 20; i++) {
			try {
				String tempChannel = driver
						.findElement(
								By.xpath("//*[@id=\"watch-more-related\"]/li[" + i + "]/div[1]/div[1]/a/span[3]/span"))
						.getAttribute("data-ytid");
				// System.out.println(tempChannel);
				if (parameters.listChannels.contains(tempChannel)) {
					listXpathElements.add("//*[@id=\"watch-more-related\"]/li[" + i + "]/div[1]/div[1]/a");
				}
			} catch (Exception e) {
				// do nothing
			}
		}

		if (listXpathElements.size() > 0) {
			int indexVideo = Utils.getRandomNumber(0, 1000) % listXpathElements.size();
			String xpath = listXpathElements.get(indexVideo);
			System.out.println(xpath);
			driver.findElement(By.xpath(xpath)).sendKeys(Keys.ENTER);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
			watchSuggestedVideo();
		}
	}

	void watchSuggestedVideo() {
		String url = driver.getCurrentUrl();
		System.out.println("Click suggested video: " + url);
		// scroll dơwn
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("window.scrollBy(0,250)", "");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}

		int watch_time = Utils.getRandomNumber(parameters.min_time_second_my_channel,
				parameters.max_time_second_my_channel);
		System.out.println("watch_time = " + watch_time + "(s)");

		String log = simpleDateFormat.format(new Date()) + "     loop " + (iter + 1) + "     watching " + url
				+ "     time " + watch_time + "(s)";
		try {
			myLogs.saveLog(log);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int max_step = watch_time;
		int step = 0;
		while (step < max_step) {
			step++;
			if (step % 60 == 0) {
				if (myLogs.checkStop() == true || driver.getCurrentUrl().compareTo(url) != 0) {
					return;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}

		if (driver.getCurrentUrl().compareTo(url) == 0) {
			// check like a comment
			if (Utils.getRandomNumber(0, 1000) % 2 == 1
					&& mapLimitCommentClickSuggest.getOrDefault(url, 0) < max_comments) {

				try {
					int indexSection = Utils.getRandomNumber(1, 1000) % 10 + 1;
					System.out.println("index comment to like and reply: " + indexSection);
					if (driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
							+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).isDisplayed()) {
						if (driver
								.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]"))
								.getAttribute("data-action-on") == null) {
							System.out.println("Action like a comment");
							driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
									+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[2]")).click();

							// reply comment
							Thread.sleep(1000);
							if (driver
									.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
											+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]"))
									.isDisplayed()) {
								System.out.println("Action reply a comment");
								driver.findElement(By.xpath("//*[@id=\"comment-section-renderer-items\"]/section["
										+ indexSection + "]/div[1]/div[2]/div[3]/div[1]/button[1]")).click();
								Thread.sleep(1000);
								int indexComment = Utils.getRandomNumber(0, parameters.listComments.size() - 1);
								String reply = parameters.listComments.get(indexComment);
								System.out.println("reply: " + reply);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]"))
										.sendKeys(reply);
								Thread.sleep(1000);
								driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]"))
										.click();
								Thread.sleep(2000);
							}
						}
					}

					if (mapLimitCommentClickSuggest.containsKey(url)) {
						mapLimitCommentClickSuggest.put(url, mapLimitCommentClickSuggest.get(url) + 1);
					} else {
						mapLimitCommentClickSuggest.put(url, 1);
					}

					Thread.sleep(1000);
				} catch (Exception e) {
					System.err.println("Like a comment fail!");
				}
			}

			// subscribe
			if (Utils.getRandomNumber(0, 1000) % 2 == 1) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				try {
					if (driver
							.findElement(
									By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span/span[1]"))
							.isDisplayed()) {
						System.out.println("Action subscribe");
						driver.findElement(By.xpath("//*[@id=\"watch7-subscription-container\"]/span/button[1]/span"))
								.click();
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					System.out.println("Subsribe fail!");
				}
			}
		}
	}

	public static void main(String[] args) {

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
			MainWithMySQLPhantomjs mainObject = new MainWithMySQLPhantomjs(myIp);
			if (mainObject.parameters.username.length() > 0) {
				if (mainObject.parameters.status.compareTo("1") == 0) {
					mainObject.startSeoSuggest();
				} else if (mainObject.parameters.status.compareTo("2") == 0) {
					mainObject.startSeoKeyword();
				} else if (mainObject.parameters.status.compareTo("3") == 0) {
					mainObject.startClickSuggest();
				}
			}

			System.out.println(myIp + " waiting 10s util status change...");
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			mainObject.myLogs.saveLog(simpleDateFormat.format(new Date()) + " waiting... ");
		}
	}
}
