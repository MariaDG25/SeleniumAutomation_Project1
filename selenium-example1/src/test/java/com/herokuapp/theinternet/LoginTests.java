package com.herokuapp.theinternet;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * 
 * @author María Dolz
 *
 */

public class LoginTests {
	private WebDriver driver;

	/**
	 * 
	 * setUp: Depending on the browser (parameter) specified on LoginTests.xml, it
	 * will select ChromeDriver as driver or FirefoxDriver instead
	 *
	 */
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
	 * 
	 * positiveLoginTest: Tests the correct functioning of the login and checks the
	 * success message
	 *
	 */

	@Test(priority = 1, groups = { "positiveTests" })
	public void positiveLoginTest() {
		System.out.println("*************Starting test**************");

		String url = "http://the-internet.herokuapp.com/login";

		// open the tested page
		driver.get(url);
		System.out.println("Page opened");

		// adding a little delay to see the changes
		sleep(1000);

		// entering the username
		WebElement username = driver.findElement(By.id("username"));
		username.sendKeys("tomsmith");

		// entering password
		WebElement password = driver.findElement(By.id("password"));
		password.sendKeys("SuperSecretPassword!");

		// clicking on login button
		WebElement logInButton = driver.findElement(By.tagName("button"));
		logInButton.click();
		sleep(3000);

		// maximize browser window
		driver.manage().window().maximize();

		WebElement logOutButton = driver.findElement(By.xpath("//a[@class='button secondary radius']"));
		assertTrue(logOutButton.isEnabled());

		// successful login message
		WebElement successMessage = driver.findElement(By.cssSelector("#flash"));
		String expectedMessage = "You logged into a secure area!";
		String actualMessage = successMessage.getText();

		Assert.assertTrue(actualMessage.contains(expectedMessage), "the message is not the expected.");
	}

	/**
	 * 
	 * negativeLoginTest: Tests the correct functioning of an unsuccessful login
	 * using an incorrect username/password and checking that the error message is
	 * shown
	 *
	 */

	@Parameters({ "username", "password", "expectedMessage" })
	@Test(priority = 2, groups = { "negativeTests", "smokeTests" })
	public void negativeLoginTest(String username, String password, String expectedErrorMessage) {
		System.out.println(
				" *************Starting negativeLoginTest with " + username + " and " + password + " **************");

		String url = "http://the-internet.herokuapp.com/login";

		driver.get(url);
		System.out.println("Page opened");

		WebElement usernameElement = driver.findElement(By.id("username"));
		usernameElement.sendKeys(username);

		WebElement passwordElement = driver.findElement(By.id("password"));
		passwordElement.sendKeys(password);

		WebElement logInButton = driver.findElement(By.tagName("button"));
		logInButton.click();

		driver.manage().window().maximize();

		// Checking error message
		WebElement errorMessage = driver.findElement(By.cssSelector("#flash"));
		String actualMessage = errorMessage.getText();
		Assert.assertTrue(actualMessage.contains(expectedErrorMessage), "the message is not the expected.");

	}

	@AfterTest(alwaysRun = true)
	private void tearDown() {
		driver.quit();
	}

	private void sleep(long n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
