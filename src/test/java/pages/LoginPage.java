package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private static final String LOGIN_URL = "https://www.linkedin.com/login";
    private static final String LOWER =
            "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')";

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By usernameField = By.cssSelector("input[autocomplete*='username'], input[name='session_key'], input#username, form input[type='text'], form input[type='email']");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By signInButton  = By.xpath("//button[normalize-space()='Sign in']");
    private final By errorMessage  = By.xpath(
            "//*[@role='alert' or contains(" + LOWER + ",'wrong email or password')]"
            + "[not(.//*[contains(" + LOWER + ",'wrong email or password')])]");
    private final By searchBox = By.cssSelector(
            "input[data-testid='typeahead-input'], input[placeholder='Search']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    public LoginPage open() {
        driver.get(LOGIN_URL);
        return this;
    }

    /** LinkedIn renders two copies of the form (one hidden), so pick the displayed one. */
    private WebElement firstVisible(By locator) {
        return wait.until(d -> d.findElements(locator).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null));
    }

    public LoginPage login(String username, String password) {
        firstVisible(usernameField).sendKeys(username);
        firstVisible(passwordField).sendKeys(password);
        firstVisible(signInButton).click();
        return this;
    }

    public LoginPage waitForOutcome() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(15)).until(d ->
                    d.getCurrentUrl().contains("/feed")
                    || d.getCurrentUrl().contains("checkpoint")
                    || d.getCurrentUrl().contains("captcha")
                    || !d.findElements(errorMessage).isEmpty());
        } catch (TimeoutException ignored) {
        }
        return this;
    }

    public boolean isSecurityCheckpoint() {
        String url = driver.getCurrentUrl();
        return url.contains("checkpoint") || url.contains("captcha");
    }

    public void waitForManualVerification() {
        System.out.println(">>> LinkedIn asked for verification. Complete it manually (waiting up to 3 minutes).");
        new WebDriverWait(driver, Duration.ofMinutes(3))
                .until(ExpectedConditions.urlContains("/feed"));
    }

    public boolean isLoggedIn() {
        try {
            return wait.until(ExpectedConditions.urlContains("/feed"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public WebElement getVisibleSearchBox() {
        return new WebDriverWait(driver, Duration.ofSeconds(45)).until(d ->
                d.findElements(searchBox).stream()
                        .filter(WebElement::isDisplayed)
                        .findFirst()
                        .orElse(null));
    }

    public boolean isSearchBoxVisible() {
        try {
            return getVisibleSearchBox().isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        try {
            return wait.until(d -> d.findElements(errorMessage).stream()
                    .anyMatch(el -> el.isDisplayed() && !el.getText().trim().isEmpty()));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorText() {
        return driver.findElements(errorMessage).stream()
                .filter(el -> el.isDisplayed() && !el.getText().trim().isEmpty())
                .map(WebElement::getText)
                .findFirst()
                .orElse("");
    }
}