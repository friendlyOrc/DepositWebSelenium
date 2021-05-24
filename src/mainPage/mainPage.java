package mainPage;
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



public class mainPage {
	String siteUrl = "https://depositweb.herokuapp.com";
	WebDriver driver;
	JavascriptExecutor js;

	static ArrayList<Account> accList = new ArrayList<>();

	@BeforeAll
	static void setUp() throws IOException {
		String filePath = "C:\\Users\\DELL\\eclipse-workspace\\BankAutomation\\data";
		String fileName = "data.xlsx";
		String accSheet = "Account";
		
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
	}

	@AfterEach
	public void afterTest() {
		driver.close();
	}
	
	@Test
	//Test display of main page
	public void mainDisplay() throws InterruptedException {
		assertAll("Main Page",
	            () -> assertEquals(siteUrl + "/", (String) driver.getCurrentUrl()),
	            () -> assertEquals(accList.get(0).getName(), driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a/span[1]")).getText()),
	            () -> assertEquals("BANKADMINISTRATION", driver.findElement(By.xpath("/html/body/nav/div/div/a")).getText()),

	            () -> assertEquals("Quản lý thành viên", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).getText()),
	            () -> assertEquals("Tạo sổ tiết kiệm", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).getText()),
	            () -> assertEquals("Rút sổ", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).getText()),
	            () -> assertEquals("Tính lãi", driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).getText()),

	            () -> assertEquals("QUẢN LÝ SỔ TIẾT KIỆM", driver.findElement(By.xpath("/html/body/div[1]/div[2]/h1")).getText()),
	            () -> assertEquals("TRANG CHỦ", driver.findElement(By.xpath("/html/body/div[1]/div[2]/h2")).getText()),
	            

	            () -> assertEquals(siteUrl + "/img/logo.png", driver.findElement(By.xpath("/html/body/div[1]/div[2]/img")).getAttribute("src"))
	            
	        );
	}
	
	//Test navigation
	@Test
	//Sign up navigation
	public void signupNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[1]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/members"));
	}

	@Test
	//Create navigation
	public void createNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[2]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/create"));
	}
	
	@Test
	//Pull out navigation
	public void pulloutNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[3]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/pullout"));
	}
	
	@Test
	//Calculation navigation
	public void calcNavigation() throws InterruptedException {
		driver.findElement(By.xpath("//*[@id=\"sidebar-collapse\"]/ul/li[4]/a")).click();
		
		Thread.sleep(3000); 
		assertTrue(((String) driver.getCurrentUrl()).contains(siteUrl + "/calc"));
	}
	
	@Test
	//Home navigation
	public void homeNavigation() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/nav/div/div/a")).click();
		
		Thread.sleep(3000); 
        assertEquals(siteUrl + "/", (String) driver.getCurrentUrl());
	}
	
	//Test logout
	@Test
	//Sign up navigation
	public void logout() throws InterruptedException {
		driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/a")).click();
		driver.findElement(By.xpath("/html/body/nav/div/div/ul/li/ul/li")).click();
		
		Thread.sleep(3000); 
		assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login"), "URL Verify");

		driver.navigate().back();
		Thread.sleep(3000);

		driver.findElement(By.xpath("/html/body/nav/div/div/a")).click();
		Thread.sleep(3000);
		
		assertEquals(true, ((String) driver.getCurrentUrl()).contains(siteUrl + "/login"), "URL Verify After Back");
	}
}
