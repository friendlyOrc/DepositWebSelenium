package pullout;

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
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
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
public class pulloutPage {

//	String siteUrl = "https://depositweb.herokuapp.com";
	String siteUrl = "http://localhost:8082";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();
	static ArrayList<Account> userList = new ArrayList<>();
	static ArrayList<Saving> savList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		String userSheet = "Search";
		String savSheet = "CreatedSaving";
		
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
				sav.setCreateTime(Date.valueOf(row.getCell(4).getStringCellValue()));	
				sav.setId((int) row.getCell(5).getNumericCellValue());
				}
			
			savList.add(sav);
		}
		
		workbook.close();
		
		DAO dao = new DAO();
		dao.Backupdbtosql();
		
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

		// Wait for the btn appears
		WebElement btn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//a[contains(text(),'Rút sổ')]"));
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
		WebElement pulloutBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tbody/tr/td[1]/button[1]"));
			}
		});
		
		pulloutBtn.click();
		
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//h1[contains(text(),'Danh sách sổ tiết kiệm')]"));
			}
		});
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
//	Check if the pull out page displays correctly
    @Order(1)
	public void pulloutDisplay() {
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		
		assertAll("Pull out Page",
				//URL and navigation bar
	            () -> assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/pullout")),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            //Side bar
	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            //Side bar active element
	            () -> assertEquals("active", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]")).getAttribute("class")),
	            

	            //Pull out page
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("//ol[@class='breadcrumb']//li[@class='active']")).getText()),
	            () -> assertEquals("/ Danh sách sổ tiết kiệm", driver.findElement(By.xpath("//div[@class='col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main']//li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Danh sách sổ tiết kiệm", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),
	            () -> assertEquals("Chủ tài khoản: " + acc.getName(), driver.findElement(By.xpath("//h5[1]")).getText()),
	            () -> assertEquals("Số CMT/CCCD: " + acc.getIdcard(), driver.findElement(By.xpath("//h5[2]")).getText()),

	            () -> assertEquals("ID", driver.findElement(By.xpath("//th[1]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getId()), driver.findElement(By.xpath("//tbody//tr//td[1]")).getText()),

	            () -> assertEquals("Số dư", driver.findElement(By.xpath("//th[2]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getBalance()) + "0", driver.findElement(By.xpath("//tbody//tr//td[2]")).getText().replace(",", "")),

	            () -> assertEquals("Ngày tạo", driver.findElement(By.xpath("//th[3]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getCreateTime()), driver.findElement(By.xpath("//tbody//tr//td[3]")).getText()),

	            () -> assertEquals("Trạng thái", driver.findElement(By.xpath("//th[4]")).getText()),
	            () -> assertEquals("Chưa rút", driver.findElement(By.xpath("//button[@class='btn btn-success']")).getText())
	            
	        );
	}
	
	@Test
//	Check if the saving info displays correctly
    @Order(2)
	public void savingInfoDisplay() throws InterruptedException {
		Thread.sleep(3000);
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		
		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the title appears
		WebElement title = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tr[@class='text-center']//td//h2"));
			}
		});
		
		if(sav.getType() == 1) {
			assertEquals("Lãnh lãi hàng tháng", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}else {
			assertEquals("Lãnh lãi cuối kỳ hạn", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}
		
		assertAll("Saving info ",
	            () -> assertEquals("Sổ tiết kiệm", title.getText()),
	            () -> assertEquals(acc.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
	            () -> assertEquals(acc.getAddress(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
	            () -> assertEquals(acc.getIdcard(), driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
	            () -> assertEquals(sav.getCreateTime().toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
	            () -> assertEquals(sav.getCreateTime().toString(), driver.findElement(By.xpath("//*[@id=\"info\"]/table/tbody/tr[8]/td/table/tbody/tr/td[1]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getId()), driver.findElement(By.xpath("//tr[@class='text-center']//td[2]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getBalance()).substring(0, String.valueOf(sav.getBalance()).length() - 2), driver.findElement(By.xpath("//tr[@class='text-center']//td[3]")).getText().replace(",", "")),
	            () -> assertEquals(String.valueOf(sav.getBalance()).substring(0, String.valueOf(sav.getBalance()).length() - 2), driver.findElement(By.xpath("//tr[@class='text-center']//td[4]")).getText().replace(",", "")),
	            () -> assertEquals(sav.getInterest(), Float.parseFloat(driver.findElement(By.xpath("//td[5]")).getText())),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//td[6]")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//button[@class='btn btn-success text-center']")).getText())
	            
	        );
	}
	@Test
	//	Check pull out
    @Order(3)
	public void pullout() {
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		Account staff = accList.get(0);

		driver.findElement(By.xpath("//button[@class='btn btn-success']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);
	
		// Wait for the title appears
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tr[@class='text-center']//td//h2"));
			}
		});
		driver.findElement(By.xpath("//button[@class='btn btn-success text-center']")).click();
		
	
		WebElement pageTitle = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='col-sm-12']//h1"));
			}
		});
	
		savList.get(0).setBalance(Float.parseFloat(driver.findElement(By.xpath("//tr[@class='text-center']//td[3]")).getText().replace(",", "")));
		if(sav.getType() == 1) {
			assertEquals("Lãnh lãi hàng tháng", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}else {
			assertEquals("Lãnh lãi cuối kỳ hạn", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}
		
		assertAll("Saving info ",
	            () -> assertEquals("Rút sổ tiết kiệm", pageTitle.getText()),
	            () -> assertEquals(staff.getName(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(2) > tbody > tr:nth-child(4) > td:nth-child(2)\").innerText")),
	            
	            () -> assertEquals("ĐÃ RÚT", driver.findElement(By.xpath("//span[@id='stamp']")).getText()),
	
	            () -> assertEquals(acc.getName(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(2) > td:nth-child(2)\").innerText")),
	            () -> assertEquals(acc.getAddress(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(3) > td:nth-child(2)\").innerText")),
	            () -> assertEquals(acc.getIdcard(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(4) > td:nth-child(2)\").innerText")),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
	            () -> assertEquals(sav.getCreateTime().toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
	            
	            () -> assertEquals(sav.getCreateTime().toString(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(8) > td > table > tbody > tr > td:nth-child(1)\").innerText")),
	            () -> assertEquals(String.valueOf(sav.getId()), driver.findElement(By.xpath("//tr[@class='text-center']//td[2]")).getText()),
	            () -> assertNotEquals(String.valueOf(sav.getBalance()).substring(0, String.valueOf(sav.getBalance()).length() - 2), driver.findElement(By.xpath("//tr[@class='text-center']//td[3]")).getText().replace(",", "")),
	            () -> assertNotEquals(String.valueOf(sav.getBalance()).substring(0, String.valueOf(sav.getBalance()).length() - 2), driver.findElement(By.xpath("//tr[@class='text-center']//td[4]")).getText().replace(",", "")),
	            () -> assertEquals(sav.getInterest(), Float.parseFloat(driver.findElement(By.xpath("//td[5]")).getText())),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//td[6]")).getText())
	            
				);
		
		
	}
	
	@Test
	//Check pull out button change
	@Order(4)
	public void btnChanged() {
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		
		assertAll("Pull out Page after pull out",
				//URL and navigation bar
	            () -> assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/pullout")),
	            () -> assertEquals("Phạm Trung Kiên", driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            //Side bar
	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            //Side bar active element
	            () -> assertEquals("active", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]")).getAttribute("class")),
	            

	            //Pull out page
	            //Breadcrumb
	            () -> assertEquals("Trang chủ", driver.findElement(By.xpath("//ol[@class='breadcrumb']//li[@class='active']")).getText()),
	            () -> assertEquals("/ Danh sách sổ tiết kiệm", driver.findElement(By.xpath("//div[@class='col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2 main']//li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Danh sách sổ tiết kiệm", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),
	            () -> assertEquals("Chủ tài khoản: " + acc.getName(), driver.findElement(By.xpath("//h5[1]")).getText()),
	            () -> assertEquals("Số CMT/CCCD: " + acc.getIdcard(), driver.findElement(By.xpath("//h5[2]")).getText()),

	            () -> assertEquals("ID", driver.findElement(By.xpath("//th[1]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getId()), driver.findElement(By.xpath("//tbody//tr//td[1]")).getText()),

	            () -> assertEquals("Số dư", driver.findElement(By.xpath("//th[2]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getBalance()) + "0", driver.findElement(By.xpath("//tbody//tr//td[2]")).getText().replace(",", "")),

	            () -> assertEquals("Ngày tạo", driver.findElement(By.xpath("//th[3]")).getText()),
	            () -> assertEquals(String.valueOf(sav.getCreateTime()), driver.findElement(By.xpath("//tbody//tr//td[3]")).getText()),

	            () -> assertEquals("Trạng thái", driver.findElement(By.xpath("//th[4]")).getText()),
	            () -> assertEquals("Đã rút", driver.findElement(By.xpath("//button[@class='btn btn-success']")).getText()),
	            () -> assertNotNull(driver.findElement(By.xpath("//button[@class='btn btn-success']")).getAttribute("disabled"))
	            
	        );
	}
	
	@Test
	//Check savng appears in saving list
	@Order(5)
	public void newSaving() {
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		
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

		// Wait for the create page appears
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//h1[contains(text(),'Mở sổ')]"));
			}
		});

		js.executeScript("document.querySelector(\"#type\").selectedIndex = " + (sav.getType() - 1));
		js.executeScript("document.querySelector(\"#balance\").value = " + sav.getBalance());
		js.executeScript("document.querySelector(\"#time\").selectedIndex = " + (sav.getTime() ));
		
		driver.findElement(By.xpath("//button[@type='submit']")).click();
		
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		// Wait for the btn appears
		WebElement pullBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//a[contains(text(),'Rút sổ')]"));
			}
		});
		
		pullBtn.click();
		
		WebElement findBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//h1[contains(text(),'Tìm tài khoản')]"));
			}
		});
		
		driver.findElement(By.xpath("//input[@id='search']")).sendKeys(acc.getName());
		
		driver.findElement(By.xpath("//button[normalize-space()='Tìm']")).click();

		// Wait for the search rs appears
		WebElement infoBtn2 = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//button[normalize-space()='Xem thông tin']"));
			}
		});
		
		infoBtn2.click();
		
		// Wait for the search rs appears
		WebElement pulloutBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tbody/tr/td[1]/button[1]"));
			}
		});
		
		pulloutBtn.click();
		
		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		assertAll("Pull out Page",
	            () -> assertEquals("Danh sách sổ tiết kiệm", driver.findElement(By.xpath("//div[@class='col-sm-12']//h1")).getText()),
	            () -> assertEquals("Chủ tài khoản: " + acc.getName(), driver.findElement(By.xpath("//h5[1]")).getText()),
	            () -> assertEquals("Số CMT/CCCD: " + acc.getIdcard(), driver.findElement(By.xpath("//h5[2]")).getText()),

	            () -> assertEquals(String.valueOf(sav.getBalance()) + "0", driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText().replace(",", "")),

	            () -> assertEquals(date.toString(), driver.findElement(By.xpath("//tbody/tr[2]/td[3]")).getText()),

	            () -> assertEquals("Chưa rút", driver.findElement(By.xpath("//tbody/tr[2]/td[4]/div[1]/button[1]")).getText())
	            
	        );
	}
	
	@Test
	//	Check pull out
    @Order(6)
	public void pulloutAfterCreate() {
		Account acc = userList.get(4);
		Saving sav = savList.get(0);
		Account staff = accList.get(0);
		
		java.sql.Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		
		driver.findElement(By.xpath("//tbody/tr[2]/td[4]/div[1]/button[1]")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the title appears
		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//tr[@class='text-center']//td//h2"));
			}
		});
		driver.findElement(By.xpath("//button[@class='btn btn-success text-center']")).click();
		

		WebElement pageTitle = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='col-sm-12']//h1"));
			}
		});

		savList.get(0).setBalance(Float.parseFloat(driver.findElement(By.xpath("//tr[@class='text-center']//td[3]")).getText().replace(",", "")));
		if(sav.getType() == 1) {
			assertEquals("Lãnh lãi hàng tháng", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}else {
			assertEquals("Lãnh lãi cuối kỳ hạn", driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText());
		}
		
		assertAll("Saving info ",
	            () -> assertEquals("Rút sổ tiết kiệm", pageTitle.getText()),
	            () -> assertEquals(staff.getName(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(2) > tbody > tr:nth-child(4) > td:nth-child(2)\").innerText")),
	            
	            () -> assertEquals("ĐÃ RÚT", driver.findElement(By.xpath("//span[@id='stamp']")).getText()),

	            () -> assertEquals(acc.getName(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(2) > td:nth-child(2)\").innerText")),
	            () -> assertEquals(acc.getAddress(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(3) > td:nth-child(2)\").innerText")),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
	            () -> assertEquals(date.toString(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()),
	            
	            () -> assertEquals(date.toString(), (String) js.executeScript("return document.querySelector(\"body > div.row > div.col-sm-9.col-sm-offset-3.col-lg-10.col-lg-offset-2.main > div.row.add_user > div > table:nth-child(4) > tbody > tr:nth-child(8) > td > table > tbody > tr > td:nth-child(1)\").innerText")),
	            () -> assertEquals(String.valueOf(sav.getBalance()) + "0", driver.findElement(By.xpath("//tr[@class='text-center']//td[3]")).getText().replace(",", "")),
	            () -> assertEquals(String.valueOf(sav.getBalance()) + "0", driver.findElement(By.xpath("//tr[@class='text-center']//td[4]")).getText().replace(",", "")),
	            () -> assertEquals(sav.getInterest(), Float.parseFloat(driver.findElement(By.xpath("//td[5]")).getText())),
	            () -> assertEquals(String.valueOf(sav.getTime()), driver.findElement(By.xpath("//td[6]")).getText())
	            
				);
	}
	
}

