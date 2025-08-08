package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.BasePage;

import java.time.Duration;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static pages.BasePage.assertElementVisible;

public class InsiderTests{
    WebDriver driver;
    BasePage basePage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.get("https://useinsider.com/");
        basePage = new BasePage(driver);
    }

    @Test
    @Order(1)
    public void testHomePageIsDisplayed() {
        assertTrue(basePage.isHomePageDisplayed(), "Insider home page is not displayed.");

    }

    @Test
    @Order(2)
    public void testCareersPageDisplay() throws InterruptedException {
        basePage.acceptCookiesIfPresent();
        basePage.navigateToCareersPage();
        assertTrue(basePage.careerPageDisplay(), "Career page is not dislplayed.");
    }

    @Test
    @Order(3)
    public void testCareersPageNavigation() throws InterruptedException {
        basePage.acceptCookiesIfPresent();
        basePage.navigateToCareersPage();
        assertTrue(basePage.verifyCareerPageSections(), "Career page sections are not dislplayed.");
    }


    @Test
    @Order(4)
    public void testJobFiltering() throws InterruptedException {
        basePage.acceptCookiesIfPresent();
        basePage.navigateToCareersPage();
        basePage.findYourDreamJobButton();
        basePage.filterJobs("Istanbul, Turkiye", "Quality Assurance");
        Thread.sleep(2000);
        basePage.firstJobClick();
        assertTrue(basePage.verifyJobListings(), "No positions avaliable.");
    }

    @Test
    @Order(5)
    public void testViewRoleNavigation() throws InterruptedException {
        basePage.acceptCookiesIfPresent();
        basePage.navigateToCareersPage();
        basePage.findYourDreamJobButton();
        basePage.filterJobs("Istanbul, Turkiye", "Quality Assurance");
        basePage.firstJobClick();
        driver.switchTo().window(driver.getWindowHandles().toArray()[1].toString());
        assertTrue(basePage.applyForThisJobButton(), "View button is not visiable or avaliable");
    }

    @Test
    public void testJobDescriptionPage() throws InterruptedException{
        basePage.acceptCookiesIfPresent();
        basePage.navigateToCareersPage();
        basePage.findYourDreamJobButton();
        basePage.filterJobs("Istanbul, Turkiye", "Quality Assurance");
        basePage.firstJobClick();
        Thread.sleep(3000);

        boolean containsQA = basePage.isJobDescriptionContains("Quality Assurance");
        Thread.sleep(3000);

        assertTrue(containsQA,"Job doesnt match the Quality Assurance positions.");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}