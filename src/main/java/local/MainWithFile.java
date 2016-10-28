package local;

import java.awt.AWTException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import parameter.Parameters;
import utils.Utils;

public class MainWithFile {
	
	static String file_info = "info.txt";
	static String username = "mrtamb9@gmail.com";
	static String password = "Tambk1209";
	
	static WebDriver driver;
	static WebDriverWait wait;
	
	static String file_target_videos = "target-videos.txt";
	static String file_other_videos = "other-videos.txt";
	static String file_comments = "comments.txt";
	
	static ArrayList<String> listTargetVideos = new ArrayList<String>();
	static ArrayList<String> listOtherVideos = new ArrayList<String>();
	static ArrayList<String> listComments = new ArrayList<String>();
	static int num_video_target = 0;
	static int num_video_other = 0;
	
	static int min_time_second = 0;
	static int max_time_second = 0;
	static int num_iteration = 0;
	static int num_times_comment = 0;
	
	static boolean checkComment = false;
	
	static void loadParameters() throws IOException
	{
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_info)));
			String line;
			while((line=br.readLine())!=null)
			{
				String [] tempArray = line.split("=");
				if(tempArray.length==2)
				{
					if (tempArray[0].trim().compareToIgnoreCase("username") == 0) {
						username = tempArray[1].trim();
						System.out.println("username = " + username);
					} else if (tempArray[0].trim().compareToIgnoreCase("password") == 0) {
						password = tempArray[1].trim();
					} else if (tempArray[0].trim().compareToIgnoreCase("num_video_target") == 0) {
						num_video_target = Integer.parseInt(tempArray[1].trim());
						System.out.println("num_video_target = " + num_video_target);
					} else if (tempArray[0].trim().compareToIgnoreCase("num_video_other") == 0) {
						num_video_other = Integer.parseInt(tempArray[1].trim());
						System.out.println("num_video_other = " + num_video_other);
					} else if (tempArray[0].trim().compareToIgnoreCase("min_time_second") == 0) {
						min_time_second = Integer.parseInt(tempArray[1].trim());
						System.out.println("min_time_second = " + min_time_second + "(s)");
					} else if (tempArray[0].trim().compareToIgnoreCase("max_time_second") == 0) {
						max_time_second = Integer.parseInt(tempArray[1].trim());
						System.out.println("max_time_second = " + max_time_second + "(s)");
					} else if (tempArray[0].trim().compareToIgnoreCase("num_iteration") == 0) {
						num_iteration = Integer.parseInt(tempArray[1].trim());
						System.out.println("num_iteration = " + num_iteration);
					} else if (tempArray[0].trim().compareToIgnoreCase("num_times_comment") == 0) {
						num_times_comment = Integer.parseInt(tempArray[1].trim());
						System.out.println("num_times_comment = " + num_times_comment);
					}
				}
			}
			br.close();
		}
		
		// load list target videos
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_target_videos)));
			String line;
			while((line=br.readLine())!=null)
			{
				line = line.trim();
				if(line.length()>0)
				{
					listTargetVideos.add(line);
				}
			}
			br.close();
		}
		
		// load list other videos
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_other_videos)));
			String line;
			while((line=br.readLine())!=null)
			{
				line = line.trim();
				if(line.length()>0)
				{
					listOtherVideos.add(line);
				}
			}
			br.close();
		}
		
		
		// load comments
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_comments)));
			String line;
			while((line=br.readLine())!=null)
			{
				line = line.trim();
				if(line.length()>0)
				{
					listComments.add(line);
				}
			}
			br.close();
		}
	}
	
	// login _youtube
	static void login()
	{
		driver.get("https://accounts.google.com/ServiceLogin?passive=true&continue=https%3A%2F%2Fwww.youtube.com%2Fsignin%3Faction_handle_signin%3Dtrue%26app%3Ddesktop%26feature%3Dsign_in_button%26next%3D%252F%26hl%3Den&service=youtube&uilel=3&hl=en#identifier");
		driver.findElement(By.id("Email")).sendKeys(username);
		driver.findElement(By.id("next")).click();
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Passwd")));

		driver.findElement(By.id("Passwd")).sendKeys(password);
		driver.findElement(By.id("signIn")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-positioner")));
		
		System.out.println("Login success! " + username);
	}
	
	// change language --> English (in case it was not in English)
	static void changeLanguage() throws InterruptedException
	{
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
		if(driver.findElement(By.xpath("//strong[@class=\"yt-picker-item\"]")).getText().compareTo("English (US)")!=0)
		{
			driver.findElement(By.xpath("//button[@value=\"en\"]")).click();
		}
		// wait until change done
		while(true)
		{
			System.out.println("Wait until change to English done....");
			if(driver.findElement(By.xpath("//link[@rel=\"search\"]")).getAttribute("href").toString().contains("locale=en_US"))
			{
				break;
			}
			Thread.sleep(500);
		}
		
		System.out.println("Change language done! English!");
	}
	
	// change location --> US (in case it was not in US)
	static void changeLocation() throws InterruptedException
	{
		driver.get("https://www.youtube.com/?persist_gl=1&gl=US");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("masthead-positioner")));
		System.out.println("Change location done! United States!");
	}
	
	static void init_main() throws InterruptedException, AWTException, IOException
	{
		// load parameter to login and something else
		loadParameters();
		
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
	    assert set.size()==1;
	    driver.switchTo().window((String) set.toArray()[0]);
	    driver.close();
	    driver.switchTo().window(base);
	    
	    // change location
	    changeLocation();
	    
	    System.out.println("Done init!");
	}
	
	static void watchVideo(String url) throws InterruptedException
	{
		System.out.println(url);
		driver.get(url);
		
		JavascriptExecutor jse = (JavascriptExecutor) driver;
	    jse.executeScript("window.scrollBy(0,250)", "");
		
		int watch_time = Utils.getRandomNumber(min_time_second, max_time_second);
		System.out.println("watch_time = " + watch_time + "(s)");
		
		watch_time = watch_time * 1000;
		Thread.sleep(watch_time);

		if(driver.findElements(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).size()>0)
		{
			if(!driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).getAttribute("class").contains("hid yt-uix-tooltip"))
			{
				System.out.println("Action like");
				driver.findElement(By.xpath("//*[@id=\"watch8-sentiment-actions\"]/span/span[1]/button")).click();
			}
		}
		
		if(checkComment && driver.findElements(By.className("comment-simplebox-renderer-collapsed-content")).size()>0)
		{
			driver.findElement(By.className("comment-simplebox-renderer-collapsed-content")).click();
			
			while(!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).isDisplayed())
			{
				System.out.println("waiting comment ...");
				Thread.sleep(1000);
			}
			int index = Utils.getRandomNumber(0, listComments.size()-1);
			String comment = listComments.get(index);
			System.out.println("comment: " + comment);
			driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).sendKeys(comment);
			while(!driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).isDisplayed())
			{
				System.out.println("waiting submit comment ...");
				Thread.sleep(1000);
			}
			driver.findElement(By.xpath("//*[@id=\"comment-simplebox\"]/div[3]/div[2]/button[2]")).click();
			while(driver.findElements(By.xpath("//*[@id=\"comment-simplebox\"]/div[2]/div[2]")).size()>0)
			{
				System.out.println("waiting submit done ...");
				Thread.sleep(1000);
			}
		}
		
		Thread.sleep(2000);
	}
	
	public static void main(String [] args) throws InterruptedException, AWTException, IOException
	{
		init_main();
		
		String targetVideo = listTargetVideos.get(0);
		int sizeOtherVideo = listOtherVideos.size();
		ArrayList<Integer> listWhenComments = Utils.getListRandomNumbers(num_times_comment, num_iteration);
	    
		int times = 0;
		for(int iter=0; iter<num_iteration; iter++)
	    {
			System.out.println("Repeat: " + (++times));
			
			// check comment or not
			if(listWhenComments.contains(iter))
			{
				checkComment = true;
				System.out.println("checkComment = true");
			} else {
				checkComment = false;
				System.out.println("checkComment = false");
			}
			
	    	ArrayList<Integer> listIndexs = Utils.getListRandomNumbers(sizeOtherVideo, sizeOtherVideo);
	    	for(int i=0; i<listIndexs.size(); i++)
	    	{
	    		int index = listIndexs.get(i);
	    		String otherVideo = listOtherVideos.get(index);
	    		watchVideo(otherVideo);
	    		watchVideo(targetVideo);
	    	}
	    	
	    	System.out.println();
	    }
		
		FileWriter fw = new FileWriter("logs.txt");
		fw.write((new Date()).toString() + "\n");
		fw.write("Done! " + num_iteration + " (iterations)");
		fw.write("\n");
		fw.close();
		driver.close();
	}
}
