package converter;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;
/**
 * ClassName   : CurrencyConverter
 * Description : This TestNG class has various functions to initialize driver, Launch
 * application, Execute scenarios of currency conversion and close the driver 
 * @author Mallisetty_S
 */
public class CurrencyConverter {
	public String baseUrl = "https://www.westpac.co.nz/";
	String driverPath = "D:\\CACTUS\\Jyothish\\WestPAc\\Drivers\\chromedriver.exe";
	public WebDriver driver;
	public String expected = null;
	public String actual = null;
	Select droplist;

	/**
	 * Definitions of the various objects in the page
	 */

	By menuFx = By.id("ubermenu-section-link-fx-travel-and-migrant-ps");
	By menuCurrencyConvertor = By.id("ubermenu-item-cta-currency-converter-ps");
	By selConvertFrom = By.id("ConvertFrom");
	By txtAmount = By.id("Amount");
	By selConvertTo = By.id("ConvertTo");
	By btnConvert = By.id("convert");
	By lblSucessMsg = By.xpath("//div[@id='resultsdiv']/em");
	By lblErrorMsg = By.xpath("//div[@id='errordiv']//li");

	
	/**
	 * Function Name : launchBrowser
	 * Description : This is the function which  Launches application
	 * @return void
	 * @author Mallisetty_S
	 */
	@BeforeTest
	public void launchBrowser() {
		System.setProperty("webdriver.chrome.driver", driverPath);
		driver = new ChromeDriver();
	
		driver.manage().window().maximize();
		driver.get(baseUrl);
	}
	/**
	 * Function Name : moveToCurrencyConvPage 
	 * Description : This method will navigate to Currency Converter
	 * @return void
	 * @author Mallisetty_S
	 */
	@Test(priority = 0)
	public void moveToCurrencyConvPage() {
		WebElement menuFxTravelMigrant = driver.findElement(menuFx);
		Actions action = new Actions(driver);
		action.moveToElement(menuFxTravelMigrant).perform();
		WebElement btnCurrConv = driver.findElement(menuCurrencyConvertor);
		action.moveToElement(btnCurrConv);
		action.click();
		action.perform();
		driver.manage().timeouts().implicitlyWait(10000, TimeUnit.SECONDS);
		driver.switchTo().frame("westpac-iframe");
		expected = "Currency converter | International & Migrant - Westpac NZ";
		actual = driver.getTitle();
		Assert.assertEquals(actual, expected);
	}

	/**
	 * Function Name : convertWithoutCurrency 
	 * Description : This method will validate the error message If there is no amount entered
	 * @return void
	 * @author Mallisetty_S
	 * @throws InterruptedException 
	 */
	@Test(priority = 1)
	public void convertWithoutCurrency() throws InterruptedException {
		driver.findElement(btnConvert).click();
		Thread.sleep(3000);
		expected = "Please enter the amount you want to convert.";
		actual = driver.findElement(lblErrorMsg).getText();
		Assert.assertEquals(actual, expected);
	}

	/**
	 * Function Name : convertCurrency 
	 * Description : This method will get inputs
	 * from Datasheet and will do currency conversion
	 * @return void
	 * @throws Exception
	 * @author Mallisetty_S
	 */
	@Test(priority = 2)
	public void convertCurrency() throws Exception {
		try {
			File src = new File("config/tst_DataSheet.xlsx");
			FileInputStream fis = new FileInputStream(src);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sh1 = wb.getSheetAt(0);

			String[][] data = new String[4][4];

			int rowCount = sh1.getLastRowNum() - sh1.getFirstRowNum();
			for (int i = 0; i < rowCount; i++) {
				Row rowValue = sh1.getRow(i + 1);
				for (int j = 0; j < rowValue.getLastCellNum(); j++) {
					data[i][j] = rowValue.getCell(j).getStringCellValue();
				}
				int k =0;
				droplist = new Select(driver.findElement(selConvertFrom));
				droplist.selectByVisibleText(data[i][k]);
				driver.findElement(txtAmount).clear();
				driver.findElement(txtAmount).sendKeys(data[i][k+1]);
				droplist = new Select(driver.findElement(selConvertTo));
				droplist.selectByVisibleText(data[i][k+2]);
				driver.findElement(btnConvert).click();
				Thread.sleep(5000);
				String conversionValue = driver.findElement(lblSucessMsg).getText();
				Reporter.log(conversionValue+"<br>");
			}
			fis.close();
		}

		catch (Exception e) {
			Reporter.log(e.getMessage());
		}

	}
	/**
	 * Function Name : terminateBrowser 
	 * Description : This method will close browser 
	 * @return void
	 * @author Mallisetty_S
	 */
	@AfterTest
	public void terminateBrowser() {
		driver.quit();
	}
}
