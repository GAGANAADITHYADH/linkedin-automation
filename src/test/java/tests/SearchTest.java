package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.SearchPage;
import utils.ConfigReader;

public class SearchTest extends BaseTest {

    @Test(description = "TC03 - searching shows a results page")
    public void searchShowsResults() {
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
        Assert.assertTrue(loginPage.isLoggedIn(), "Login failed: " + driver.getCurrentUrl());

        SearchPage searchPage = new SearchPage(driver).search("software testing");

        Assert.assertTrue(searchPage.isResultsPage(),
                "Expected a search results URL but was: " + driver.getCurrentUrl());
        Assert.assertTrue(driver.getCurrentUrl().contains("software"),
                "Results URL should contain the search term: " + driver.getCurrentUrl());
        Assert.assertTrue(searchPage.isAnyResultVisible(),
                "At least one search result should be visible");
    }
}