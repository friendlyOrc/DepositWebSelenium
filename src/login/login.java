package login;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.*;

public class login {
	WebDriver driver;
	JavascriptExecutor js;
	
	
	//Verify if text is present in the page
	private boolean isTextPresent(String text){
        try{
            boolean b = driver.getPageSource().contains(text);
            return b;
        }
        catch(Exception e){
            return false;
        }
  }
	
	@Before
	public void init() {
		ChromeOptions options = new ChromeOptions();

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;

		driver.get("https://depositweb.herokuapp.com/");
	}
	
	@Test
	//Test Login Display 
	public void loginDisplay() throws InterruptedException {
		assertAll("Login display",
	            () -> assertEquals(true, ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/login")),
	            
	            () -> assertEquals("Tên đăng nhập:", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/label")).getText()),
	            () -> assertEquals("Tên đăng nhập", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("placeholder")),
	            () -> assertEquals("Mật khẩu:", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/label")).getText()),
	            () -> assertEquals("Mật khẩu", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/input")).getAttribute("placeholder")),

	            () -> assertEquals(driver.findElement(By.xpath("//*[@id=\"username\"]")), driver.switchTo().activeElement()),
	            
	            () -> assertEquals("Đăng nhập", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).getText()),

	            () -> assertEquals("https://depositweb.herokuapp.com/img/logo.png", driver.findElement(By.xpath("/html/body/div/div/div/div[1]/img")).getAttribute("src"))
	            
	        );
		driver.close();
		
	}
	
	@Test
	//Test Login with valid credentials
	public void loginValid() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("kienpt");

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("123");
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Valid",
	            () -> assertEquals("https://depositweb.herokuapp.com/", (String) driver.getCurrentUrl()),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText())
	        );
		driver.close();
	}
	

	
	@Test
	//Test Login with wrong username
	public void loginInvalidUsername() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("ThisIsAnInvalidUsername");

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("123");
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/login")),
	            () -> assertEquals("Tài khoản không tồn tại!", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div")).getText()),

	            () -> assertEquals("ThisIsAnInvalidUsername", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("value"))
	        );
		driver.close();
	}
	
	@Test
	//Test Login with wrong password
	public void loginInvalidPassword() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("kienpt");

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("WrongPassword");
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/login")),
	            () -> assertEquals("Mật khẩu sai!", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div")).getText()),

	            () -> assertEquals("kienpt", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("value"))
	        );
		driver.close();
	}
	
	@Test
	//Test Login with empty username
	public void loginInvalidEmptyUN() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("123");
		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("validationMessage");
		
		assertNotNull(message, "Msg is absent");

		Thread.sleep(1000);
		driver.close();
	}
	

	@Test
	//Test Login with empty password
	public void loginInvalidEmptyPW() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("kienpt");
		
		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("validationMessage");
		
		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
		driver.close();
	}
	
	@Test
	//Log in navigation
	public void loginNavigation() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys("kienpt");
	
		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys("123");
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);

		driver.navigate().back();
		Thread.sleep(3000);
		
		assertEquals(true, ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/"));
		driver.close();
	}
}
