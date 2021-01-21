package com.herokuapp.theinternet;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 * @author María Dolz
 *
 */
public class ExceptionsTests {
	private WebDriver driver;

	@Parameters({ "browser" })
	@BeforeMethod(alwaysRun = true)
	private void setUp(String browser) {

		switch (browser) {
		case "chrome":
			System.out.println("starting browser: " + browser);
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		case "firefox":
			System.out.println("starting browser: " + browser);
			System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
			driver = new FirefoxDriver();
			break;
		default:
			System.out.println("could not start" + browser + ", starting Chrome instead");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver = new ChromeDriver();
			break;
		}

	}

	/**
	 * notVisibleTest: After start button is clicked, there will be a 10 seconds
	 * delay until the element is present. Then, a comparison will be done in order
	 * to know if the text is the expected one
	 */
	@Test(priority = 1, groups = { "waitExample" })
	public void notVisibleTest() {
		driver.get("http://the-internet.herokuapp.com/dynamic_loading/2");

		WebElement startButton = driver.findElement(By.xpath("//div[@id='start']/button"));
		startButton.click();

		WebDriverWait wait = new WebDriverWait(driver, 10);

		Assert.assertTrue(
				wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("finish"), "Hello World!")),
				"Does not match");
	}

	/**
	 * timeOutTest: This test is forced to fail in order to manage the exception. 10
	 * seconds are needed to get message "Hello Word!" but only 2 seconds are
	 * passed. When exception is managed, test passes
	 */

	@Test(priority = 2, groups = { "timeOutTest" })
	public void timeOutTest() {
		driver.get("http://the-internet.herokuapp.com/dynamic_loading/1");

		WebElement startButton = driver.findElement(By.xpath("//div[@id='start']/button"));
		startButton.click();

		WebDriverWait wait = new WebDriverWait(driver, 2);

		try {
			wait.until(ExpectedConditions.visibilityOf((WebElement) By.id("finish")));
		} catch (TimeoutException e) {
			System.out.println("Exception managed: " + e.getMessage());
			sleep(3000);
		}

		String currentText = driver.findElement(By.id("finish")).getText();
		Assert.assertTrue(currentText.contains("Hello World!"));

	}

	/**
	 * staleElementTest: When clicking on remove, the checkbox is deleted and the
	 * message "It's gone!" is shown. In order to make the test pass, stalenessOf
	 * condition is used as checkbox is not longer attached to the DOM. Once Add
	 * button is clicked, the checkbox will be attached again after a delay
	 */

	@Test(priority = 3, groups = { "stalenessTest" })
	public void staleElementTest() {
		driver.get("http://the-internet.herokuapp.com/dynamic_controls");
		WebElement checkboxEx = driver.findElement(By.id("checkbox"));
		WebElement removeButton = driver.findElement(By.xpath("//button[contains(., 'Remove')]"));
		removeButton.click();

		WebDriverWait wait = new WebDriverWait(driver, 10);
		Assert.assertTrue(wait.until(ExpectedConditions.stalenessOf(checkboxEx)),
				"This checkbox is still visible, but shouldn't be");
		Assert.assertEquals(driver.findElement(By.xpath("//p[@id='message']")).getText(), "It's gone!");

		WebElement addButton = driver.findElement(By.xpath("//button[contains(., 'Add')]"));
		addButton.click();

		WebElement checkboxEx2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checkbox")));
		Assert.assertTrue(checkboxEx2.isDisplayed(), "Checkbox is not displayed but it should.");

	}

	/**
	 * disabledElementTest: Before typing on the text field, it is checked that this
	 * element is not disabled anymore after clicking on enable button.
	 */
	@Test(priority = 4, groups = { "disabledTest" })
	public void disabledElementTest() {

		driver.get("http://the-internet.herokuapp.com/dynamic_controls");

		WebElement enableButton = driver.findElement(By.xpath("(//button)[2]"));
		enableButton.click();

		WebElement textField = driver.findElement(By.xpath("//input[@type='text']"));

		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(textField));

		Assert.assertTrue(textField.isEnabled(), "Element is not enable already");

		textField.sendKeys("Just a test");

		Assert.assertEquals(textField.getAttribute("value"), "Just a test");

		WebElement disableButton = driver.findElement(By.xpath("//button[contains(.,'Disable')]"));
		disableButton.click();

	}

	@AfterMethod(alwaysRun = true)
	private void tearDown() {
		driver.quit();
	}

	private void sleep(long n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
