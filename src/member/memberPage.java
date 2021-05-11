package member;
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

public class memberPage {
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

		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).click();
		
		Thread.sleep(3000); 
	}
	
	@Test
	//Check if the sign up page displays correctly
	public void signUpDisplay() {
		assertAll("Sign up Page",
				//URL and navigation bar
	            () -> assertEquals("https://depositweb.herokuapp.com/members", (String) driver.getCurrentUrl()),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            //Side bar
	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            //Side bar active element
	            () -> assertEquals("active", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]")).getAttribute("class")),
	            
	            //Focus element
	            () -> assertEquals(driver.findElement(By.xpath("//*[@id=\"name\"]")), driver.switchTo().activeElement()),

	            //Sign up form
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("/html/body/div/div[2]/div[1]/ol/li[2]")).getText()),
	            () -> assertEquals("/ Đăng kí thành viên mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[1]/ol/li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Đăng kí thành viên mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div/h1")).getText()),
	            () -> assertEquals("Họ và tên", driver.findElement(By.xpath("/html/body/div/div[2]/div[3]/div/div/form/fieldset/div[1]/label")).getText())
	            
	        );
		driver.close();
	}
	
	
}
