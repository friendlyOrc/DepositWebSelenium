package mainPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.*;

public class mainPage {
	WebDriver driver;
	JavascriptExecutor js;
	
	@Before
	public void init() throws InterruptedException {
		ChromeOptions options = new ChromeOptions();

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;

		driver.get("https://depositweb.herokuapp.com/");
		
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("kienpt");

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("123");
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(3500);
	}
	
	@Test
	//Test display of main page
	public void mainDisplay() throws InterruptedException {
		assertAll("Main Page",
	            () -> assertEquals("https://depositweb.herokuapp.com/", (String) driver.getCurrentUrl()),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            () -> assertEquals("QUẢN LÝ SỔ TIẾT KIỆM", driver.findElement(By.xpath("/html/body/div[1]/div[2]/h1")).getText()),
	            () -> assertEquals("TRANG CHỦ", driver.findElement(By.xpath("/html/body/div[1]/div[2]/h2")).getText()),
	            

	            () -> assertEquals("https://depositweb.herokuapp.com/img/logo.png", driver.findElement(By.xpath("/html/body/div[1]/div[2]/img")).getAttribute("src"))
	            
	        );
		driver.close();
	}
	
	//Test navigation
	@Test
	//Sign up navigation
	public void signupNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/members"));
		driver.close();
	}

	@Test
	//Create navigation
	public void createNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/create"));
		driver.close();
	}
	
	@Test
	//Pull out navigation
	public void pulloutNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/pullout"));
		driver.close();
	}
	
	@Test
	//Calculation navigation
	public void calcNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/calc"));
		driver.close();
	}
	
	@Test
	//Home navigation
	public void homeNavigation() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/nav/div/div/a")).click();
		
		Thread.sleep(3000); 
        assertEquals("https://depositweb.herokuapp.com/", (String) driver.getCurrentUrl());
        
		driver.close();
	}
	
	//Test logout
	@Test
	//Sign up navigation
	public void logout() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a")).click();
		driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/ul/li")).click();
		
		Thread.sleep(3000); 
		assertTrue("URL Verify", ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/login"));

		driver.navigate().back();
		Thread.sleep(3000);

		driver.findElement(By.xpath("/html/body/nav/div/div/a")).click();
		Thread.sleep(3000);
		
		assertTrue("URL Verify After Back", ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/login"));
		driver.close();
	}
}
