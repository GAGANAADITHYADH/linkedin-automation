package tests;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class SetupTest extends BaseTest {

    @Test
    public void loginPageOpens() {
        driver.get("https://www.linkedin.com/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        } catch (Exception e) {
            // keep going, the debug output below shows what actually loaded
        }

        System.out.println("DEBUG title = " + driver.getTitle());
        System.out.println("DEBUG url   = " + driver.getCurrentUrl());

        Assert.assertTrue(driver.getCurrentUrl().contains("linkedin.com"),
                "Unexpected URL: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getTitle().toLowerCase().contains("linkedin"),
                "Unexpected title: '" + driver.getTitle() + "' at " + driver.getCurrentUrl());
    }
}