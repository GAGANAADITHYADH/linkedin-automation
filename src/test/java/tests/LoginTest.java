package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ConfigReader;

public class LoginTest extends BaseTest {

    @Test(description = "TC01 - valid login reaches the feed")
    public void validLogin() {
        String user = ConfigReader.username();
        String pass = ConfigReader.password();
        if (user.isEmpty() || pass.isEmpty() || user.equals("your-email@example.com")) {
            throw new SkipException("Credentials are not configured in config.properties");
        }

        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(user, pass);
        loginPage.waitForOutcome();

        if (loginPage.isSecurityCheckpoint()) {
            loginPage.waitForManualVerification();
        }

        Assert.assertTrue(loginPage.isLoggedIn(),
                "Expected the feed URL but was: " + driver.getCurrentUrl());
        Assert.assertTrue(loginPage.isSearchBoxVisible(),
                "Search box should be visible after login");
    }

    @Test(description = "TC02 - invalid password shows an error")
    public void invalidPassword() {
        String user = ConfigReader.username();
        if (user.isEmpty() || user.equals("your-email@example.com")) {
            throw new SkipException("Username is not configured in config.properties");
        }

        LoginPage loginPage = new LoginPage(driver).open();
        loginPage.login(user, "Wrong-Password-12345");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "An error message should be displayed for a wrong password");
        Assert.assertFalse(driver.getCurrentUrl().contains("/feed"),
                "User must not reach the feed with a wrong password");
    }
}