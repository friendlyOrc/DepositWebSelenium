package calc;

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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import model.Account;
import model.Saving;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.*;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@TestMethodOrder(OrderAnnotation.class)
public class calcPage {

//	String siteUrl = "https://depositweb.herokuapp.com";
	String siteUrl = "http://localhost:8082";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();
	static ArrayList<Saving> savList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		String savSheet = "Calc";
		
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
		}
		
		sheet = workbook.getSheet(savSheet);
		
		rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		

		// Create a loop over all the rows of excel file to read it

		for (int i = 1; i < rowCount + 1; i++) {

			Row row = sheet.getRow(i);

			// Create a loop to print cell values in a row
			Saving sav = new Saving();
			if(row.getCell(0) != null) {
				sav.setType((int) row.getCell(0).getNumericCellValue());
				sav.setBalance((int) row.getCell(1).getNumericCellValue());
				sav.setInterest((float) row.getCell(2).getNumericCellValue());
				sav.setTime((int) row.getCell(3).getNumericCellValue());
				sav.setMoneyAfter((long) row.getCell(4).getNumericCellValue());
				sav.setMoneyInt((long) row.getCell(5).getNumericCellValue());
				sav.setMonthly((long) row.getCell(6).getNumericCellValue());
			}
			
			savList.add(sav);
		}
		
		workbook.close();
	}
	
	@BeforeEach
	public void init() throws InterruptedException {
		ChromeOptions options = new ChromeOptions();

		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");

		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;

		driver.get(siteUrl + "/");
		
		WebElement loginInput = driver.findElement(By.xpath("//*[@id=\"username\"]"));
		loginInput.sendKeys(accList.get(0).getUsername());

		WebElement pwInput = driver.findElement(By.xpath("//*[@id=\"password\"]"));
		pwInput.sendKeys(accList.get(0).getPassword());
		
		driver.findElement(By.xpath("/html/body/div/div/div/div[2]/form/fieldset/button")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the create btn page appears
		WebElement btn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//a[normalize-space()='Tính lãi']"));
			}
		});
		
		btn.click();
		
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//a[normalize-space()='Tính lãi']"));
			}
		});
		
	}

	@AfterEach
	public void afterTest() {
		driver.close();
	}
	
	@Test
//	Check if the calc page displays correctly
    @Order(1)
	public void createPageDisplay() {
		Account acc = accList.get(0);
		
		assertAll("Create Calculate Page",
				//URL and navigation bar
	            () -> assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/calc")),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            //Side bar
	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            //Side bar active element
	            () -> assertEquals("active", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]")).getAttribute("class")),
	            
	            //Focus element
	            () -> assertNotNull(driver.findElement(By.xpath("//*[@id=\"moneyInput\"]")).getAttribute("autofocus")),

	            //Create form
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("//ol[@class='breadcrumb']//li[@class='active']")).getText()),
	            () -> assertEquals("/ Tính lãi", driver.findElement(By.xpath("//div[@class='col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main']//li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),

	            () -> assertEquals("Phương thức tính lãi tiết kiệm", driver.findElement(By.xpath("//label[@for='type']")).getText()),
	            () -> assertEquals("Lãnh lãi hàng tháng", new Select(driver.findElement(By.xpath("//select[@id='term']"))).getFirstSelectedOption().getText()),
	            
	            () -> assertEquals("Tiền gửi (VND)", driver.findElement(By.xpath("//label[@for='input']")).getText()),
	            () -> assertEquals("Tiền gửi ban đầu", driver.findElement(By.xpath("//input[@id='moneyInput']")).getAttribute("placeholder").toString()),
	            
	            () -> assertEquals("Lãi suất (%)", driver.findElement(By.xpath("//label[@for='interest']")).getText()),
	            () -> assertEquals("3.1", driver.findElement(By.xpath("//input[@id='interestInput']")).getAttribute("value").toString()),
	            
	            () -> assertEquals("Kỳ hạn (Tháng)", driver.findElement(By.xpath("//label[@for='time']")).getText()),
	            () -> assertEquals("1 tháng", new Select(driver.findElement(By.xpath("//select[@id='timeInput']"))).getFirstSelectedOption().getText()),
	            
	            () -> assertEquals("Tính", driver.findElement(By.xpath("//button[@class='btn btn-success']")).getText()),

	            () -> assertEquals("Tổng số tiền", driver.findElement(By.xpath("//th[1]")).getText()),
	            () -> assertEquals("Tiền lãi", driver.findElement(By.xpath("//th[2]")).getText()),
	            () -> assertEquals("Tiền lãi định kỳ", driver.findElement(By.xpath("//th[3]")).getText())
	            
	        );
	}
	
	// Check time select box
	@Order(2)
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
	public void selectTime(int i) {
		Saving sav = savList.get(i);
		
		new Select(driver.findElement(By.xpath("//select[@id='timeInput']"))).selectByIndex(i);
		assertEquals(sav.getInterest(), Float.parseFloat((String)js.executeScript("return document.querySelector(\"#interestInput\").value")));
	}
	
	//Check term select box
	@Test
	@Order(3)
	public void selectTerm1() {
		new Select(driver.findElement(By.xpath("//select[@id='term']"))).selectByIndex(0);
		assertEquals("Lãnh lãi hàng tháng", new Select(driver.findElement(By.xpath("//select[@id='term']"))).getFirstSelectedOption().getText());
	}
	
	//Check term select box
	@Test
	@Order(4)
	public void selectTerm2() {
		new Select(driver.findElement(By.xpath("//select[@id='term']"))).selectByIndex(1);
		assertEquals("Lãnh lãi cuối kỳ", new Select(driver.findElement(By.xpath("//select[@id='term']"))).getFirstSelectedOption().getText());
	}
	
    // Calculate with type 1
	@Order(5)
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
	void calcType1(int argument) {
		Saving sav = savList.get(argument);
		
		js.executeScript("document.querySelector(\"#term\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#moneyInput\").value = " + sav.getBalance());
		js.executeScript("document.querySelector(\"#interestInput\").value = " + sav.getInterest());
		js.executeScript("document.querySelector(\"#timeInput\").selectedIndex = " + argument);
		
		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the rs appears
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//td[@id='sum']"));
			}
		});
		
		
		assertAll("Create Successfully",
			
			() -> assertEquals(String.valueOf(sav.getMoneyAfter()), driver.findElement(By.xpath("//td[@id='sum']")).getText().replace(",", "")),
			() -> assertEquals(String.valueOf(sav.getMoneyInt()), driver.findElement(By.xpath("//td[@id='interest__sum']")).getText().replace(",", "")),
			() -> assertEquals(String.valueOf(sav.getMonthly()), driver.findElement(By.xpath("//td[@id='interest']")).getText().replace(",", ""))
			
			);
	}

	
	
	@Order(6)
	@ParameterizedTest
	@ValueSource(ints = {16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31})
	void calctype2(int argument) {
		Saving sav = savList.get(argument);
		
		js.executeScript("document.querySelector(\"#term\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#moneyInput\").value = " + sav.getBalance());
		js.executeScript("document.querySelector(\"#interestInput\").value = " + sav.getInterest());
		js.executeScript("document.querySelector(\"#timeInput\").selectedIndex = " + (argument - 16));
		
		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the rs appears
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//td[@id='sum']"));
			}
		});
		
		
		assertAll("Create Successfully",
			
			() -> assertEquals(String.valueOf(sav.getMoneyAfter()), driver.findElement(By.xpath("//td[@id='sum']")).getText().replace(",", "")),
			() -> assertEquals(String.valueOf(sav.getMoneyInt()), driver.findElement(By.xpath("//td[@id='interest__sum']")).getText().replace(",", "")),
			() -> assertEquals(String.valueOf(sav.getMonthly()), driver.findElement(By.xpath("//td[@id='interest']")).getText().replace(",", ""))
			
			);
	}
	
	// No balance warning message	
	@Test
	@Order(7)
	public void noInput() throws InterruptedException {
		
		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		

		String message = driver.findElement(By.xpath("//input[@id='moneyInput']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	// Balance has special character
	@Test
	@Order(8)
	public void speBalance() throws InterruptedException {
		Saving sav = savList.get(0);

		driver.findElement(By.xpath("//input[@id='moneyInput']")).sendKeys("@!@#$@#$");

		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='speInp']"));
			}
		});
		
		assertEquals("Tiền gửi không được chứa ký tự khác ngoài chữ số!", msg.getText());
	}
		
	// Balance smaller than 1m
	@Test
	@Order(9)
	public void smallInput() throws InterruptedException {
		Saving sav = savList.get(0);

		driver.findElement(By.xpath("//input[@id='moneyInput']")).sendKeys("1000");

		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='smlInp']"));
			}
		});
		
		assertEquals("Tiền gửi nhỏ nhất là 1.000.000đ!", msg.getText());
	}
	
	// Float Balance
	@Test
	@Order(10)
	public void floatInput() throws InterruptedException {
		Saving sav = savList.get(0);

		driver.findElement(By.xpath("//input[@id='moneyInput']")).sendKeys("10000000.123");

		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='speInp']"));
			}
		});
		
		assertEquals("Tiền gửi không được chứa ký tự khác ngoài chữ số!", msg.getText());
	}

		
}

