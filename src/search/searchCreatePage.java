package search;

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

import model.Account;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.*;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.ss.usermodel.Sheet;

import org.apache.poi.ss.usermodel.Workbook;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@TestMethodOrder(OrderAnnotation.class)
public class searchCreatePage {

//	String siteUrl = "https://depositweb.herokuapp.com";
	String siteUrl = "http://localhost:8082";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();
	static ArrayList<Account> userList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		String userSheet = "Search";
		
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

		// Wait for the main page appears
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
	}

	@AfterEach
	public void afterTest() {
		driver.close();
	}
	
	@Test
//	Check if the search page displays correctly
    @Order(5)
	public void searchDisplay() {
		assertAll("Search Page",
				//URL and navigation bar
	            () -> assertEquals(siteUrl + "/create", (String) driver.getCurrentUrl()),
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
	            () -> assertEquals(driver.findElement(By.xpath("//input[@id='search']")), driver.switchTo().activeElement()),

	            //Sign up form
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("//ol[@class='breadcrumb']//li[@class='active']")).getText()),
	            () -> assertEquals("/ Tìm tài khoản", driver.findElement(By.xpath("//div[@class='col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main']//li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Tìm tài khoản", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),
	            () -> assertEquals("Tên chủ tài khoản:", driver.findElement(By.xpath("//label[@for='name']")).getText()),
	            () -> assertEquals("Tên chủ tài khoản", driver.findElement(By.xpath("//input[@id='search']")).getAttribute("placeholder")),
	            () -> assertEquals("Tìm", driver.findElement(By.xpath("//button[@class='btn btn-success']")).getText()),
	            () -> assertEquals("ID", driver.findElement(By.xpath("//th[1]")).getText()),
	            () -> assertEquals("Tên chủ tài khoản", driver.findElement(By.xpath("//th[2]")).getText()),
	            () -> assertEquals("Chứng minh thư/CCCD", driver.findElement(By.xpath("//th[3]")).getText()),
	            () -> assertEquals("Thao tác", driver.findElement(By.xpath("//th[4]")).getText())
	            
	        );
	}
	
	// Search with valid query
	@Test
	@Order(1)
	public void searchForResult() {
		Account acc = userList.get(0);
		
		driver.findElement(By.xpath("//input[@id='search']")).sendKeys(acc.getName());
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the search rs appears
		WebElement btn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//button[normalize-space()='Xem thông tin']"));
			}
		});
		
		btn.click();
		
		// Wait for the search rs appears
		WebElement infoBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tbody/tr/td[1]/button[1]"));
			}
		});

		if(acc.getSex() == 0) {
			assertEquals("Nam", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText());
		}else if(acc.getSex() == 1) {
			assertEquals("Nữ", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText());
		}else {
			assertEquals("Khác", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText());
		}
		assertAll("Search Successfully",
				() -> assertEquals("Tạo sổ tiết kiệm", infoBtn.getText()),
				() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
				() -> assertEquals(acc.getDob().toString(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
				() -> assertEquals(acc.getAddress(), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
				() -> assertEquals(acc.getIdcard(), driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText()),
				() -> assertEquals(acc.getEmail(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()));
		
	}
	
	// Search with nonsense query
	@Test
	@Order(2)
	public void searchNoResult() {
		Account acc = userList.get(0);
		
		driver.findElement(By.xpath("//input[@id='search']")).sendKeys(acc.getName() + "adasdasd");
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the search rs appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='empty']"));
			}
		});
		
		assertAll("No result search",
				() -> assertEquals("Không có tài khoản có thông tin trùng khớp!", msg.getText()));
		
	}
	
	// Search with no query
	@Test
	@Order(3)
	public void searchNoQuery() {
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the search rs appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='warning']"));
			}
		});
		
		assertAll("No result search",
				() -> assertEquals("Không được bỏ trống thông tin!", msg.getText()));
		
	}
	
	// Search with special character
	@Test
	@Order(4)
	public void searchSpe() {
		driver.findElement(By.xpath("//input[@id='search']")).sendKeys("@");
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the search rs appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@id='error']"));
			}
		});
		
		assertAll("No result search",
				() -> assertEquals("Tên không được chứa ký tự đặc biệt!", msg.getText()));
		
	}
	
}
