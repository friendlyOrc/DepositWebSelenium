package member;

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
import org.springframework.test.annotation.Rollback;

import dao.DAO;
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
public class memberPage {
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
		String userSheet = "User";
		
		System.out.print("=====================================");
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
			System.out.print(acc.getUsername() + "||" + acc.getPassword());
		}
		
		sheet = workbook.getSheet(userSheet);
		
		rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
		

		// Create a loop over all the rows of excel file to read it

		for (int i = 1; i < rowCount + 1; i++) {

			Row row = sheet.getRow(i);

			// Create a loop to print cell values in a row

			Account acc = new Account();
			acc.setName(row.getCell(0).getStringCellValue());
			acc.setDob(Date.valueOf(row.getCell(1).getStringCellValue()));
			acc.setSex((int) row.getCell(2).getNumericCellValue());
			acc.setAddress(row.getCell(3).getStringCellValue());
			acc.setIdcard(row.getCell(4).getStringCellValue());
			acc.setEmail(row.getCell(5).getStringCellValue());
			
			userList.add(acc);
			System.out.print(acc.getUsername() + "||" + acc.getPassword());
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

		// Wait for the main page appears
		WebElement btn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a"));
			}
		});
		
		btn.click();
		
		WebElement mainBtn = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("/html/body/nav/div/div/a"));
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
//	Check if the sign up page displays correctly
    @Order(24)
	public void signUpDisplay() {
		assertAll("Sign up Page",
				//URL and navigation bar
	            () -> assertEquals(siteUrl + "/members", (String) driver.getCurrentUrl()),
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
	            () -> assertEquals("/ Đăng ký khách hàng mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[1]/ol/li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Đăng ký khách hàng mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div/h1")).getText()),
	            () -> assertEquals("Họ và tên", driver.findElement(By.xpath("/html/body/div/div[2]/div[3]/div/div/form/fieldset/div[1]/label")).getText()),
	            () -> assertEquals("Họ và tên", driver.findElement(By.xpath("//*[@id=\"name\"]")).getAttribute("placeholder")),
	            () -> assertEquals("Ngày tháng năm sinh", driver.findElement(By.xpath("//label[@for='dob']")).getText()),
	            () -> assertNotNull(driver.findElement(By.xpath("//input[@id='dob']"))),
	            () -> assertEquals("Chọn giới tính", driver.findElement(By.xpath(" //div[@class='row add_user']//div[3]//label[1]")).getText()),
	            () -> assertNotNull(driver.findElement(By.xpath("//input[@id='sex1']"))),
	            () -> assertNotNull(driver.findElement(By.xpath("//input[@id='sex2']"))),
	            () -> assertNotNull(driver.findElement(By.xpath("//input[@id='sex3']"))),
	            () -> assertEquals("true", driver.findElement(By.xpath("//input[@id='sex1']")).getAttribute("checked")),
	            () -> assertEquals("Địa chỉ", driver.findElement(By.xpath("//label[@for='addr']")).getText()),
	            () -> assertEquals("Địa chỉ", driver.findElement(By.xpath("//input[@id='address']")).getAttribute("placeholder")),
	            () -> assertEquals("Số chứng minh thư/CCCD", driver.findElement(By.xpath("//label[@for='idcard']")).getText()),
	            () -> assertEquals("Chứng minh thư/CCCD", driver.findElement(By.xpath("//input[@id='idcard']")).getAttribute("placeholder")),
	            () -> assertEquals("Email:", driver.findElement(By.xpath("//label[@for='username']")).getText()),
	            () -> assertEquals("Email", driver.findElement(By.xpath("//input[@id='email']")).getAttribute("placeholder"))
	            
	        );
	}
	
	// Successfully create an account - Male
	@Test
    @Order(1)
	public void signUpValid() throws InterruptedException {	
		Account acc = userList.get(0);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex1']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		assertAll("Create Successfully",
				() -> assertEquals("Đăng ký thành công!", msg.getText()),
				() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
				() -> assertEquals(acc.getDob().toString(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
				() -> assertEquals("Nam", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
				() -> assertEquals(acc.getAddress(), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
				() -> assertEquals(acc.getIdcard(), driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText()),
				() -> assertEquals(acc.getEmail(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()));
		
	}
	

	// Successfully create an account - Female
	@Test
    @Order(2)
	public void signUpValidFemale() throws InterruptedException {	
		Account acc = userList.get(1);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		assertAll("Create Successfully",
				() -> assertEquals("Đăng ký thành công!", msg.getText()),
				() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
				() -> assertEquals(acc.getDob().toString(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
				() -> assertEquals("Nữ", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
				() -> assertEquals(acc.getAddress(), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
				() -> assertEquals(acc.getIdcard(), driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText()),
				() -> assertEquals(acc.getEmail(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()));
		
	}
	

	// Successfully create an account - Other
	@Test
    @Order(3)
	public void signUpValidOther() throws InterruptedException {	
		Account acc = userList.get(2);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-success']"));
			}
		});
		
		assertAll("Create Successfully",
				() -> assertEquals("Đăng ký thành công!", msg.getText()),
				() -> assertEquals(acc.getName(), driver.findElement(By.xpath("//tbody/tr[2]/td[2]")).getText()),
				() -> assertEquals(acc.getDob().toString(), driver.findElement(By.xpath("//tbody/tr[3]/td[2]")).getText()),
				() -> assertEquals("Khác", driver.findElement(By.xpath("//tbody/tr[4]/td[2]")).getText()),
				() -> assertEquals(acc.getAddress(), driver.findElement(By.xpath("//tbody/tr[5]/td[2]")).getText()),
				() -> assertEquals(acc.getIdcard(), driver.findElement(By.xpath("//tbody/tr[6]/td[2]")).getText()),
				() -> assertEquals(acc.getEmail(), driver.findElement(By.xpath("//tbody/tr[7]/td[2]")).getText()));
		
	}
	

	@Test
    @Order(4)
	// Test Create without name
	public void createEmptyName() throws InterruptedException {
		Account acc = userList.get(2);
		
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='name']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}

	@Test
    @Order(5)
	// Test Create without Dob
	public void createEmptyDob() throws InterruptedException {
		Account acc = userList.get(2);

		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='dob']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}

	@Test
    @Order(6)
	// Test Create without address
	public void createEmptyAddr() throws InterruptedException {
		Account acc = userList.get(2);

		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='address']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	

	@Test
    @Order(7)
	// Test Login with empty password
	public void createEmptyIdcard() throws InterruptedException {
		Account acc = userList.get(2);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='idcard']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	

	@Test
    @Order(8)
	// Test Login with empty password
	public void createEmptyEmail() throws InterruptedException {
		Account acc = userList.get(2);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex3']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='email']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	// Fail create account - Name contains number
	@Test
    @Order(9)
	public void invalidNameNumber() {
		Account acc = userList.get(3);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Tên không được chứa ký tự đặc biệt hoặc số!", msg.getText());
	}
	
	// Fail create account - Name contains special character
	@Test
    @Order(10)
	public void invalidNameSpe() {
		Account acc = userList.get(5);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Tên không được chứa ký tự đặc biệt hoặc số!", msg.getText());
	}
	
	// Fail create account - Name too Long
	@Test
    @Order(11)
	public void invalidNameLong() {
		Account acc = userList.get(4);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Tên không được vượt quá 25 ký tự!", msg.getText());
	}
	
	// Fail create account - Age unders 18
	@Test
    @Order(12)
	public void invalidAge() {
		Account acc = userList.get(6);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Người đăng ký phải đủ 18 tuổi!", msg.getText());
	}
	
	// Fail create account - Address has special character
	@Test
    @Order(13)
	public void invalidAddress() {
		Account acc = userList.get(7);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Địa chỉ không được chứa ký tự đặc biệt!", msg.getText());
	}
	
	// Fail create account - Id card duplicate
	@Test
    @Order(14)
	public void invalidCardDup() {
		Account acc = userList.get(17);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Số chứng minh thư đã Đăng ký trước đó!", msg.getText());
	}
	
	// Fail create account - Id card too short
	@Test
    @Order(15)
	public void invalidCardShort() {
		Account acc = userList.get(8);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Số CMT/CCCD không hợp lệ!", msg.getText());
	}
	
	// Fail create account - Id card contains words
	@Test
    @Order(16)
	public void invalidCardWord() {
		Account acc = userList.get(9);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Số CMT/CCCD không hợp lệ!", msg.getText());
	}
	
	// Fail create account - Id card contains Special character
	@Test
    @Order(17)
	public void invalidCardSpe() {
		Account acc = userList.get(10);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Số CMT/CCCD không được chứa ký tự đặc biệt!", msg.getText());
	}
	
	// Fail create account - Id card too Long
	@Test
    @Order(18)
	public void invalidCardLong() {
		Account acc = userList.get(11);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Số CMT/CCCD không hợp lệ!", msg.getText());
	}
	
	// Fail create account - Email without .com
	@Test
    @Order(19)
	public void invalidEmailCom() {
		Account acc = userList.get(12);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Email không hợp lệ!", msg.getText());
	}
	
	// Fail create account - Email contains special character
	@Test
    @Order(20)
	public void invalidEmailSpe() {
		Account acc = userList.get(13);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofSeconds(15))
				.pollingEvery(Duration.ofSeconds(2)).ignoring(NoSuchElementException.class);

		// Wait for the name of user appears
		WebElement msg = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(By.xpath("//div[@class='alert alert-danger']"));
			}
		});
		
		assertEquals("Email không hợp lệ!", msg.getText());
	}
	
	// Fail create account - Email without prefix
	@Test
    @Order(21)
	public void invalidEmailPrefix() throws InterruptedException {
		Account acc = userList.get(14);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='email']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	// Fail create account - Email without postfix
	@Test
    @Order(22)
	public void invalidEmailPostfix() throws InterruptedException {
		Account acc = userList.get(15);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();
		
		String message = driver.findElement(By.xpath("//input[@id='email']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
	// Fail create account - Email only has text
	@Test
    @Order(23)
	public void invalidEmailText() throws InterruptedException {
		Account acc = userList.get(16);
		
		driver.findElement(By.xpath("//input[@id='name']")).sendKeys(acc.getName());
		js.executeScript("document.querySelector(\"#dob\").value = \"" + acc.getDob().toString() + "\"");
		driver.findElement(By.xpath("//input[@id='sex2']")).click();
		driver.findElement(By.xpath("//input[@id='address']")).sendKeys(acc.getAddress());
		driver.findElement(By.xpath("//input[@id='idcard']")).sendKeys(acc.getIdcard());
		driver.findElement(By.xpath("//input[@id='email']")).sendKeys(acc.getEmail());
		

		driver.findElement(By.xpath("//button[@name='sbm']")).click();

		String message = driver.findElement(By.xpath("//input[@id='email']"))
				.getAttribute("validationMessage");

		assertNotNull(message, "Msg is absent");
		Thread.sleep(1000);
	}
	
}
