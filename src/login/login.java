// DEPOSIT WEBSITE
// JUNIT GROUP 11 - PTIT
// LOGIN AUTOMATION TEST 
// Writen by KienPT

package login;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByXPath;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import model.Account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class login {
//	String siteUrl = "https://depositweb.herokuapp.com";
	String siteUrl = "http://localhost:8082";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();

	// Verify if text is present in the page
	private boolean isTextPresent(String text) {
		try {
			boolean b = driver.getPageSource().contains(text);
			return b;
		} catch (Exception e) {
			return false;
		}
	}

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		
		File file = new File(filePath + "\\" + fileName);

		FileInputStream inputStream = new FileInputStream(file);

		Workbook workbook = new XSSFWorkbook(inputStream);

		// Read sheet inside the workbook by its name

		Sheet sheet = workbook.getSheet(accSheet);

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
//			System.out.print(acc.getUsername() + "||" + acc.getPassword());
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
	// Test Login Display
	public void loginDisplay() throws InterruptedException {
		assertAll("Login display",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),

				() -> assertEquals("Tên đăng nhập:",
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/label"))
								.getText()),
				() -> assertEquals("Tên đăng nhập",
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input"))
								.getAttribute("placeholder")),
				() -> assertEquals("Mật khẩu:",
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/label"))
								.getText()),
				() -> assertEquals("Mật khẩu",
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[2]/input"))
								.getAttribute("placeholder")),

				() -> assertEquals(driver.findElement(By.xpath("//*[@id=\"username\"]")),
						driver.switchTo().activeElement()),

				() -> assertEquals("Đăng nhập",
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).getText()),

				() -> assertEquals(siteUrl + "/img/logo.png",
						driver.findElement(By.xpath("/html/body/div/div/div/div[1]/img")).getAttribute("src"))

		);
	}

	@Test
	// Test Login with valid credentials
	public void loginValid() throws InterruptedException {
		
		//Locate Element with Javascript
		WebElement loginInput = (WebElement) js.executeScript("return document.getElementById('username')");
		loginInput.sendKeys(accList.get(0).getUsername());

		WebElement pwInput = (WebElement) js.executeScript("return document.getElementById('password')");
		pwInput.sendKeys(accList.get(0).getPassword());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		// Thread.sleep pauses the running code in an amount of time.
		Thread.sleep(5000);

		assertAll("Login Valid", () -> assertEquals(siteUrl + "/", (String) driver.getCurrentUrl()),
				() -> assertEquals(accList.get(0).getName(),
						driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()));
	}

	@Test
	// Test Login with wrong username
	public void loginInvalidUsername() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(1).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(1).getPassword());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		// Explicit waits freeze the thread, until the condition is resolves.
		WebElement msg = new WebDriverWait(driver, 10)
				.until(driver -> driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div")));

		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),
				() -> assertEquals("Tài khoản không tồn tại!", msg.getText()),

				() -> assertEquals(accList.get(1).getUsername(),
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input"))
								.getAttribute("value")));
	}

	@Test
	// Test Login with wrong password
	public void loginInvalidPassword() throws InterruptedException {

		// An implicit wait is to tell WebDriver to poll the DOM for a certain amount of
		// time when trying to find an element or elements if they are not immediately
		// available.
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(2).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(2).getPassword());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		WebElement msg = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/div/div/div"));

		assertAll("Login Invalid",
				() -> assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login")),
				() -> assertEquals("Mật khẩu sai!", msg.getText()),

				() -> assertEquals(accList.get(2).getUsername(),
						driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input"))
								.getAttribute("value")));
	}

	@Test
	// Test Login with empty username
	public void loginInvalidEmptyUN() throws InterruptedException {

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");

		Thread.sleep(1000);
	}

	@Test
	// Test Login with empty password
	public void loginInvalidEmptyPW() throws InterruptedException {

		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();

		String message = driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/div[1]/input"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}

	@Test
	// Log in navigation
	public void loginNavigation() throws InterruptedException {
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());

		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		//FluentWait instance defines the maximum amount of time to wait for a condition, as well as the frequency with which to check the condition.
		//Checking the element once every 2 seconds for 15s
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement nameOfUser = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]"));
			}
		});

		driver.navigate().back();

		Thread.sleep(3000);

		assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl));
	}
}
