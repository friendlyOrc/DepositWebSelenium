package create;

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
import org.springframework.test.annotation.Rollback;

import dao.DAO;
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
@Rollback
public class createPage {

//	String siteUrl = "https://depositweb.herokuapp.com";
	String siteUrl = "http://localhost:8082";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();
	static ArrayList<Account> userList = new ArrayList<>();
	static ArrayList<Saving> savList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		DAO dao = new DAO();
		dao.Backupdbtosql();
		
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		String userSheet = "Search";
		String savSheet = "Saving";
		
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
		
		sheet = workbook.getSheet(userSheet);
		
		rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		

		// Create a loop over all the rows of excel file to read it

		for (int i = 1; i < rowCount + 1; i++) {

			Row row = sheet.getRow(i);

			// Create a loop to print cell values in a row
			Account acc = new Account();
			if(row.getCell(0).getStringCellValue() != "") {
				acc.setName(row.getCell(0).getStringCellValue());
				acc.setDob(Date.valueOf(row.getCell(1).getStringCellValue()));
				acc.setSex((int) row.getCell(2).getNumericCellValue());
				acc.setAddress(row.getCell(3).getStringCellValue());
				acc.setIdcard(row.getCell(4).getStringCellValue());
				acc.setEmail(row.getCell(5).getStringCellValue());
			}
			
			userList.add(acc);
		}
		
		sheet = workbook.getSheet(savSheet);
		
		rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		

		// Create a loop over all the rows of excel file to read it

		for (int i = 1; i < rowCount + 1; i++) {

			Row row = sheet.getRow(i);

			// Create a loop to print cell values in a row
			Saving sav = new Saving();
			if(row.getCell(0).getNumericCellValue() != 0) {
				sav.setType((int) row.getCell(0).getNumericCellValue());
				sav.setBalance((int) row.getCell(1).getNumericCellValue());
				sav.setInterest((float) row.getCell(2).getNumericCellValue());
				sav.setTime((int) row.getCell(3).getNumericCellValue());
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
				return driver.findElement(By.xpath("//a[contains(text(),'Tạo sổ tiết kiệm')]"));
			}
		});
		
		btn.click();
		
		WebElement mainBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//h1[contains(text(),'Tìm tài khoản')]"));
			}
		});
		
		Account acc = userList.get(4);
		
		driver.findElement(By.xpath("//input[@id='search']")).sendKeys(acc.getName());
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();

		// Wait for the search rs appears
		WebElement infoBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//button[normalize-space()='Xem thông tin']"));
			}
		});
		
		infoBtn.click();
		
		// Wait for the search rs appears
		WebElement createBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tbody/tr/td[1]/button[1]"));
			}
		});
		
		createBtn.click();
	}

	@AfterEach
	public void afterTest() {
		driver.close();
	}


	@AfterAll
	static void afterAll() {
		DAO dao = new DAO();
		dao.Restoredbfromsql("backup.sql");
	}
	
	@Test
//	Check if the search page displays correctly
    @Order(1)
	public void createPageDisplay() {
		Account acc = userList.get(4);
		
		assertAll("Create Saving Page",
				//URL and navigation bar
	            () -> assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/create")),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            //Side bar
	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            //Side bar active element
	            () -> assertEquals("active", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]")).getAttribute("class")),
	            
	            //Focus element
	            () -> assertEquals(driver.findElement(By.xpath("//input[@id='balance']")), driver.switchTo().activeElement()),

	            //Create form
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("//ol[@class='breadcrumb']//li[@class='active']")).getText()),
	            () -> assertEquals("/ Mở sổ", driver.findElement(By.xpath("//div[@class='col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main']//li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Mở sổ", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),
	            () -> assertEquals("Chủ tài khoản: " + acc.getName(), driver.findElement(By.xpath("//h5[1]")).getText()),
	            () -> assertEquals("Số CMT/CCCD: " + acc.getIdcard(), driver.findElement(By.xpath("//h5[2]")).getText()),

	            () -> assertEquals("Phương thức lãnh lãi", driver.findElement(By.xpath("//label[@for='type']")).getText()),
	            () -> assertEquals("Lãnh lãi hàng tháng", new Select(driver.findElement(By.xpath("//select[@id='type']"))).getFirstSelectedOption().getText()),
	            
	            () -> assertEquals("Tiền gửi (VND)", driver.findElement(By.xpath("//label[@for='input']")).getText()),
	            () -> assertEquals("number", driver.findElement(By.xpath("//input[@id='balance']")).getAttribute("type")),
	            () -> assertEquals("0.0", driver.findElement(By.xpath("//input[@id='balance']")).getAttribute("value").toString()),
	            
	            () -> assertEquals("Lãi suất (%)", driver.findElement(By.xpath("//label[@for='interest']")).getText()),
	            () -> assertEquals("0.1", driver.findElement(By.xpath("//input[@id='interest']")).getAttribute("value").toString()),
	            
	            () -> assertEquals("Kỳ hạn (Tháng)", driver.findElement(By.xpath("//label[@for='time']")).getText()),
	            () -> assertEquals("Không kỳ hạn", new Select(driver.findElement(By.xpath("//select[@id='time']"))).getFirstSelectedOption().getText()),
	            
	            () -> assertEquals("Tạo", driver.findElement(By.xpath("//button[@type='submit']")).getText())
	            
	        );
	}
	

	@Order(2)
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16})
	void createSavingType1(int argument) {
		Saving sav = savList.get(argument);
		Account acc = accList.get(0);
		Account client = userList.get(4);
		
		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + sav.getBalance());
		js.executeScript("document.querySelector(\"#time\").selectedIndex = " + argument);
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		Date sqlDate = new Date(Calendar.getInstance().getTime().getTime());
		System.out.println(sqlDate);
		
		if(sav.getType() == 1) {
			assertEquals("Lãnh lãi hàng tháng", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}else {
			assertEquals("Lãnh lãi cuối kỳ hạn", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}
		
		assertAll("Create Successfully",
			() -> assertEquals("Tạo sổ tiết kiệm thành công!", msg.getText()),
			() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//h5[2]//span[1]")).getText()),
			
			() -> assertEquals(client.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
			() -> assertEquals(client.getAddress(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
			() -> assertEquals(client.getIdcard(), driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
			
			() -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
			() -> assertEquals(sqlDate.toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
			
			() -> assertEquals(sqlDate.toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
			() -> assertEquals(sav.getBalance() + "0", driver.findElement(By.xpath("//td[3]")).getText().replaceAll(",", "")),
			() -> assertEquals(sav.getBalance() + "0", driver.findElement(By.xpath("//td[4]")).getText().replaceAll(",", "")),
			() -> assertEquals(String.valueOf(sav.getInterest()), driver.findElement(By.xpath("//td[5]")).getText()),
			() -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//td[6]")).getText())
			
			);
	}
	
	@Order(3)
	@ParameterizedTest
	@ValueSource(ints = {17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33})
	void createSavingType2(int argument) {
		Saving sav = savList.get(argument);
		Account acc = accList.get(0);
		Account client = userList.get(4);

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + sav.getBalance());
		js.executeScript("document.querySelector(\"#time\").selectedIndex = " + (argument - 17));
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the msg appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		Date sqlDate = new Date(Calendar.getInstance().getTime().getTime());
		System.out.println(sqlDate);
		
		if(sav.getType() == 1) {
			assertEquals("Lãnh lãi hàng tháng", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}else {
			assertEquals("Lãnh lãi cuối kỳ", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}
		
		assertAll("Create Successfully",
			() -> assertEquals("Tạo sổ tiết kiệm thành công!", msg.getText()),
			() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//h5[2]//span[1]")).getText()),
			
			() -> assertEquals(client.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
			() -> assertEquals(client.getAddress(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
			() -> assertEquals(client.getIdcard(), driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
			
			() -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
			() -> assertEquals(sqlDate.toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
			
			() -> assertEquals(sqlDate.toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
			() -> assertEquals(sav.getBalance() + "0", driver.findElement(By.xpath("//td[3]")).getText().replaceAll(",", "")),
			() -> assertEquals(sav.getBalance() + "0", driver.findElement(By.xpath("//td[4]")).getText().replaceAll(",", "")),
			() -> assertEquals(String.valueOf(sav.getInterest()), driver.findElement(By.xpath("//td[5]")).getText()),
			() -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//td[6]")).getText())
			
			);
	}
		
	// No balance warning message	
	@Test
	@Order(4)
	public void noInput() throws InterruptedException {
		Saving sav = savList.get(0);
		Account acc = accList.get(0);
		Account client = userList.get(4);

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#time\").selectedIndex = 1");
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		

		String message = driver.findElement(By.xpath("//input[@id='balance']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	// Balance has special character
	@Test
	@Order(5)
	public void speBalance() throws InterruptedException {
		Saving sav = savList.get(0);
		Account acc = accList.get(0);
		Account client = userList.get(4);

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + "-10000");
		js.executeScript("document.querySelector(\"#time\").selectedIndex = 1");
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Tiền gửi không được chứa ký tự khác ngoài chữ số!", msg.getText());
	}
		
	// Balance smaller than 1m
	@Test
	@Order(6)
	public void smallInput() throws InterruptedException {
		Saving sav = savList.get(0);
		Account acc = accList.get(0);
		Account client = userList.get(4);

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + "10000");
		js.executeScript("document.querySelector(\"#time\").selectedIndex = 1");
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the main page appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Tiền gửi tối thiểu là 1.000.000 (một triệu đồng)!", msg.getText());
	}
	
	// Float Balance
	@Test
	@Order(7)
	public void floatInput() throws InterruptedException {
		Saving sav = savList.get(0);
		Account acc = accList.get(0);
		Account client = userList.get(4);

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + "10000.123");
		js.executeScript("document.querySelector(\"#time\").selectedIndex = 1");
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();

		String message = driver.findElement(By.xpath("//input[@id='balance']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
		
}

