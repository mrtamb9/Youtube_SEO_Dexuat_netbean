package test;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestChromedriver {
	static WebDriver driver;
    WebDriverWait wait;
    
    void init()
    {
    	System.out.println("Start: init");
    	System.setProperty("webdriver.chrome.driver", "geckodriver/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addExtensions(new File("geckodriver/extension.crx"));
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, 100);
        
        driver.get("https://www.youtube.com");
        try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        driver.quit();
        
        if(driver == null) {
        	System.out.println("Driver null");
        } else {
        	System.out.println("Driver NOT null");
        }
    }
    
    public static void main(String [] args) {
    	TestChromedriver testing = new TestChromedriver();
    	testing.init();
    }
}
