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

import model.Account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class login {
	String siteUrl = "https://depositweb.herokuapp.com";
	WebDriver driver;
	JavascriptExecutor js;
	
	static ArrayList<Account> accList = new ArrayList<>();
	
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
	
	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "Account.xlsx";
		String sheetName = "Sheet1";
		System.out.print("=====================================");
		File file = new File(filePath + "\\" + fileName);

		FileInputStream inputStream = new FileInputStream(file);

		Workbook workbook = new XSSFWorkbook(inputStream);

		// Read sheet inside the workbook by its name   

		Sheet sheet = workbook.getSheet(sheetName);

		// Find number of rows in excel file

		int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		

		// Create a loop over all the rows of excel file to read it

		for (int i = 1; i < rowCount + 1; i++) {

			Row row = sheet.getRow(i);

			// Create a loop to print cell values in a row

			Account acc = new Account();
			acc.setUsername(row.getCell(0).getStringCellValue());
			acc.setPassword(row.getCell(1).getStringCellValue());
			acc.setName(row.getCell(2).getStringCellValue());
			accList.add(acc);
			System.out.print(acc.getUsername() + "||" + acc.getPassword());
		}
		workbook.close();
	}
	
	@BeforeEach
	public void init() {
		ChromeOptions options = new ChromeOptions();

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;

		driver.get(siteUrl);
	}
	
	@AfterEach
	public void afterTest() {
		driver.close();
	}
	
	
	@Test
	//Test Login Display 
	public void loginDisplay() throws InterruptedException {
		assertAll("Login display",
	            () -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),
	            
	            () -> assertEquals("Tên đăng nhập:", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/label")).getText()),
	            () -> assertEquals("Tên đăng nhập", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("placeholder")),
	            () -> assertEquals("Mật khẩu:", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/label")).getText()),
	            () -> assertEquals("Mật khẩu", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/input")).getAttribute("placeholder")),

	            () -> assertEquals(driver.findElement(By.xpath("//*[@id=\"username\"]")), driver.switchTo().activeElement()),
	            
	            () -> assertEquals("Đăng nhập", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).getText()),

	            () -> assertEquals("https://depositweb.herokuapp.com/img/logo.png", driver.findElement(By.xpath("/html/body/div/div/div/div[1]/img")).getAttribute("src"))
	            
	        );
	}
	
	@Test
	//Test Login with valid credentials
	public void loginValid() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Valid",
	            () -> assertEquals(siteUrl + "/", (String) driver.getCurrentUrl()),
	            () -> assertEquals(accList.get(0).getName(), driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText())
	        );
	}
	

	
	@Test
	//Test Login with wrong username
	public void loginInvalidUsername() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(1).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(1).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),
	            () -> assertEquals("Tài khoản không tồn tại!", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div")).getText()),

	            () -> assertEquals(accList.get(1).getUsername(), driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("value"))
	        );
	}
	
	@Test
	//Test Login with wrong password
	public void loginInvalidPassword() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(2).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(2).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);
		
		
		
		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),
	            () -> assertEquals("Mật khẩu sai!", driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div")).getText()),

	            () -> assertEquals(accList.get(2).getUsername(), driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("value"))
	        );
	}
	
	@Test
	//Test Login with empty username
	public void loginInvalidEmptyUN() throws InterruptedException {

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("validationMessage");
		
		assertNotNull(message, "Msg is absent");

		Thread.sleep(1000);
	}
	

	@Test
	//Test Login with empty password
	public void loginInvalidEmptyPW() throws InterruptedException {

		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input")).getAttribute("validationMessage");
		
		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	@Test
	//Log in navigation
	public void loginNavigation() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Thread.sleep(5000);

		driver.navigate().back();
		Thread.sleep(3000);
		
		assertEquals(true, ((String) driver.getCurrentUrl()).contains("https://depositweb.herokuapp.com/"));
	}
}
