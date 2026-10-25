package utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;

public class DebugUtil {

    /** Prints what the page really contains, with input values removed. */
    public static void dump(WebDriver driver, String tag) {
        try {
            System.out.println("DEBUG[" + tag + "] title=" + driver.getTitle() + " url=" + driver.getCurrentUrl());
            System.out.println("DEBUG[" + tag + "] screenshot=" + ScreenshotUtil.capture(driver, tag));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            Object inputs = js.executeScript(
                "return Array.from(document.querySelectorAll('input')).map(e => e.outerHTML"
                + ".replace(/value=\"[^\"]*\"/g,'value=\"\"').replace(/class=\"[^\"]*\"/g,'')).join('\\n')");
            System.out.println("DEBUG[" + tag + "] inputs:\n" + inputs);
            Object search = js.executeScript(
                "return Array.from(document.querySelectorAll('[role=search],[aria-label*=earch],[placeholder*=earch],[data-testid*=earch]'))"
                + ".slice(0,8).map(e => e.tagName + ' | ' + (e.getAttribute('aria-label')||'') + ' | ' + (e.getAttribute('placeholder')||'') + ' | ' + (e.getAttribute('role')||'')).join('\\n')");
            System.out.println("DEBUG[" + tag + "] search-like elements:\n" + search);
            Files.writeString(Path.of("debug_" + tag + ".html"), driver.getPageSource());
        } catch (Exception e) {
            System.out.println("DEBUG dump failed: " + e.getMessage());
        }
    }
}