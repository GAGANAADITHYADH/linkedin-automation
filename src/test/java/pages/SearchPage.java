package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SearchPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // LinkedIn has two copies of the search input (one hidden), so pick the displayed one.
    private final By searchBox = By.cssSelector(
            "input[data-testid='typeahead-input'], input[placeholder='Search']");
    // Result links: people (/in/), companies (/company/), or schools (/school/)
    private final By resultLinks = By.cssSelector(
            "a[href*='/in/'], a[href*='/company/'], a[href*='/school/']");

    public SearchPage(WebDriver driver) {
        this.driver = driver;
        // the page re-renders often, so stale elements are ignored and the lookup is simply repeated
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    private WebElement visibleSearchBox() {
        return wait.until(d -> d.findElements(searchBox).stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElse(null));
    }

    /** Clicks the global search box, types the term, checks it was typed completely, then presses Enter. */
    public SearchPage search(String term) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement box = visibleSearchBox();
                box.click();
                box.clear();
                box.sendKeys(term);
                // the suggestions box re-renders while typing and can swallow characters
                new WebDriverWait(driver, Duration.ofSeconds(8)).ignoring(StaleElementReferenceException.class)
                        .until(d -> term.equals(visibleSearchBox().getAttribute("value")));
                visibleSearchBox().sendKeys(Keys.ENTER);
                if (isResultsPageQuick()) {
                    return this;
                }
            } catch (TimeoutException | StaleElementReferenceException e) {
                System.out.println("DEBUG search attempt " + attempt + " did not complete, retrying");
            }
        }
        return this;
    }

    private boolean isResultsPageQuick() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(d -> d.getCurrentUrl().contains("/search/results"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Switches the current keyword search to the People results, so the first result is a profile (/in/...). */
    public SearchPage filterByPeople() {
        String current = driver.getCurrentUrl();
        String keywords = current.substring(current.indexOf("keywords=") + 9);
        int amp = keywords.indexOf('&');
        if (amp > 0) {
            keywords = keywords.substring(0, amp);
        }
        driver.get("https://www.linkedin.com/search/results/people/?keywords=" + keywords);
        wait.until(d -> d.getCurrentUrl().contains("/search/results/people"));
        return this;
    }

    public boolean isResultsPage() {
        try {
            return wait.until(d -> d.getCurrentUrl().contains("/search/results"));
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** First displayed result link that is not the signed-in user's own nav link. */
    public WebElement firstResultLink() {
        return wait.until(d -> d.findElements(resultLinks).stream()
                .filter(e -> {
                    try {
                        String href = e.getAttribute("href");
                        String text = e.getText();
                        return e.isDisplayed() && href != null && text != null
                                && !text.trim().isEmpty() && !href.contains("/in/me");
                    } catch (StaleElementReferenceException ex) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null));
    }

    public boolean isAnyResultVisible() {
        try {
            return firstResultLink().isDisplayed();
        } catch (TimeoutException | StaleElementReferenceException e) {
            return false;
        }
    }
}