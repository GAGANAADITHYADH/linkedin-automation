package utils;

import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Field;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            Object instance = result.getInstance();
            Class<?> c = instance.getClass();
            while (c != null) {
                try {
                    Field f = c.getDeclaredField("driver");
                    f.setAccessible(true);
                    WebDriver driver = (WebDriver) f.get(instance);
                    if (driver != null) {
                        String path = ScreenshotUtil.capture(driver, "FAIL_" + result.getName());
                        System.out.println("SCREENSHOT on failure: " + path);
                    }
                    return;
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                }
            }
        } catch (Exception e) {
            System.out.println("Screenshot listener failed: " + e.getMessage());
        }
    }
}