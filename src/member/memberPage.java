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

public class memberPage {
	String siteUrl = "https://depositweb.herokuapp.com";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();
	static ArrayList<Account> userList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		String userSheet = "Sheet2";
		
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
			acc.setUsername(row.getCell(0).getStringCellValue());
			acc.setPassword(row.getCell(1).getStringCellValue());
			acc.setName(row.getCell(2).getStringCellValue());
			accList.add(acc);
			System.out.print(acc.getUsername() + "||" + acc.getPassword());
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
		
		Thread.sleep(3500);
		
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).click();
		
		Thread.sleep(3000); 
	}

	@AfterEach
	public void afterTest() {
		driver.close();
	}
	
	@Test
	//Check if the sign up page displays correctly
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
	            () -> assertEquals("/ Đăng kí thành viên mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[1]/ol/li[3]")).getText()),
	            
	            //Form
	            () -> assertEquals("Đăng kí thành viên mới", driver.findElement(By.xpath("/html/body/div/div[2]/div[2]/div/h1")).getText()),
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
	
	@Test
	public void signUpValid() {
		
	}
	
	
}
