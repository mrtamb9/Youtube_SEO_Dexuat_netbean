package test;

import java.io.File;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestChromedriver {
	static WebDriver driver;
    WebDriverWait wait;
	String file_driver = "geckodriver/chromedriver.exe";
    
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
    
    void quitDriver() {
//		Set<Cookie> setCookies = driver.manage().getCookies();
//		try {
//			FileWriter fw = new FileWriter(file_cookie);
//			for (Cookie cookie : setCookies) {
//				String name = cookie.getName();
//				String value = cookie.getValue();
//				String path = cookie.getPath();				
//				String expiry = simpleDateFormat.format(new Date());
//				System.out.println(cookie.getDomain());
//				String temp = "\t" + name + "###" + value + "###" + expiry + "###" + path;
//				System.out.println(temp);
//				if (cookie.getExpiry() != null && name != null && value != null && path != null) {
//					expiry = simpleDateFormat.format(cookie.getExpiry());
//				    System.out.println(cookie.getDomain());
//					String str = name + "###" + value + "###" + expiry + "###" + path;
//					System.out.println(str);
//					fw.write(str + "\n");
//				}
//				System.out.println();
//			}
//			
//			fw.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		driver.quit();
	}

	void loadDriver() {
		System.setProperty("webdriver.chrome.driver", file_driver);
		ChromeOptions options = new ChromeOptions();
		options.addExtensions(new File("geckodriver/extension.crx"));
		driver = new ChromeDriver(options);
//		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file_cookie)));
//			String str;
//			while ((str = br.readLine()) != null) {
//				System.out.println(str);
//				String[] arrayCookie = str.split("###");
//				if (arrayCookie.length == 4) {
//					String name = arrayCookie[0];
//					String value = arrayCookie[1];
//					Date expiry = simpleDateFormat.parse(arrayCookie[2]);
//					String path = arrayCookie[3];
//					Cookie cookie = new Cookie(name, value, path, expiry);
//
//					System.out.println(name + "###" + value + "###" + expiry + "###" + path);
//					System.out.println(cookie);
//					System.out.println();
//					driver.manage().addCookie(cookie);
//				}
//			}
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		wait = new WebDriverWait(driver, 100);
	}
    
    public static void main(String [] args) {
    	TestChromedriver testing = new TestChromedriver();
    	testing.init();
    }
}
