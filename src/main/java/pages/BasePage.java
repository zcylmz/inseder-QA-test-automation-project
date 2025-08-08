package pages;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.plaf.TableHeaderUI;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.actions = new Actions(driver);
    }

    public WebElement waitForElement(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            takeScreenshot("waitForElement_Failure");
            throw e;
        }
    }

    public void takeScreenshot(String fileName) {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, new File("screenshots/" + fileName + ".png"));
        } catch (IOException e) {
            System.out.println("ScreenShot alma hatasi.");
        }
    }

    public boolean verifyJobListings() {
        try {
            List<WebElement> jobTitles = driver.findElements(By.xpath("//div[@class='position-title']"));
            List<WebElement> jobDepartments = driver.findElements(By.xpath("//div[@class='position-department']"));
            List<WebElement> jobLocations = driver.findElements(By.xpath("//div[@class='position-location']"));

            for (int i = 0; i < jobTitles.size(); i++) {
                String title = jobTitles.get(i).getText();
                String department = jobDepartments.get(i).getText();
                String location = jobLocations.get(i).getText();
                if (!title.contains("Quality Assurance") || !department.contains("Quality Assurance") || !location.contains("Istanbul, Turkey")) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            takeScreenshot("verifyJobListings_Failure");
            throw e;
        }
    }

    public void scrollToElement(By locator) throws InterruptedException {

        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
    }

    public void acceptCookiesIfPresent() {
        try {
            By acceptCookiesButton = By.id("wt-cli-accept-all-btn");
            List<WebElement> cookieButton = driver.findElements(acceptCookiesButton);
            if (!cookieButton.isEmpty() && cookieButton.get(0).isDisplayed()) {
                waitForElement(acceptCookiesButton).click();
            }
        } catch (Exception e) {
            takeScreenshot("acceptCookies_Failure");
            throw e;
        }
    }

    public void navigateToCareersPage() {
        try {
            By companyMenu = By.xpath("//a[contains(text(),'Company')]");
            By careersLink = By.xpath("//a[contains(text(),'Careers')]");
            waitForElement(companyMenu);
            WebElement companyMenuButton = driver.findElement(companyMenu);
            companyMenuButton.click();
            waitForElement(careersLink).click();
        } catch (Exception e) {
            takeScreenshot("navigateToCareersPage_Failure");
            throw e;
        }
    }

    public boolean verifyCareerPageSections() throws InterruptedException {
        try {
            By locationsSection = By.xpath("//h3[@class='category-title-media ml-0']");
            By teamsSection = By.xpath("//a[@class='btn btn-outline-secondary rounded text-medium mt-5 mx-auto py-3 loadmore']");
            By lifeAtInsiderSection = By.xpath("//h2[normalize-space()='Life at Insider']");

            scrollToElement(locationsSection);
            scrollToElement(teamsSection);
            scrollToElement(lifeAtInsiderSection);

            boolean isVisible = driver.findElement(locationsSection).isDisplayed() &&
                    driver.findElement(teamsSection).isDisplayed() &&
                    driver.findElement(lifeAtInsiderSection).isDisplayed();

            return isVisible;
        } catch (Exception e) {
            takeScreenshot("verifyCareerPageSections_Failure");
            throw e;
        }
    }

    public void scrollByOffset(int xOffset, int yOffset) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollBy(arguments[0], arguments[1]);", xOffset, yOffset);
        } catch (Exception e) {
            takeScreenshot("scrollByOffset_Failure");
            throw e;
        }
    }

    public void selectDropdownOption(By dropdownLocator, String optionText) throws InterruptedException {
        try {
            By optionLocator = By.xpath("//li[contains(text(), '" + optionText + "')]");
            int maxAttempts = 10;
            int attempts = 0;
            while (attempts < maxAttempts) {
                WebElement elementDropdown = wait.until(ExpectedConditions.elementToBeClickable(dropdownLocator));
                elementDropdown.click();
                Thread.sleep(1000);
                List<WebElement> options = driver.findElements(optionLocator);
                if (!options.isEmpty()) {
                    options.get(0).click();
                    return;
                }
                Thread.sleep(1000);
                attempts++;
            }
            throw new RuntimeException("Dropdown içeriği yüklenmedi veya '" + optionText + "' seçeneği bulunamadı!");
        } catch (Exception e) {
            takeScreenshot("selectDropdownOption_Failure");
            throw e;
        }
    }

    public void filterJobs(String location, String department) throws InterruptedException {
        try {
            By locationDropdownLocator = By.xpath("//span[@id='select2-filter-by-location-container']");
            By departmentDropdownLocator = By.xpath("//span[@id='select2-filter-by-department-container']");

            Dimension screenSize = driver.manage().window().getSize();
            int rate = screenSize.getHeight() / 6;
            scrollByOffset(0, rate);

            waitForElement(locationDropdownLocator);

            selectDropdownOption(locationDropdownLocator, location);
            selectDropdownOption(departmentDropdownLocator, department);
            Thread.sleep(2000);

        } catch (Exception e) {
            takeScreenshot("filterJobs_failure");
            throw e;
        }
    }

    public boolean isHomePageDisplayed() {
        try {
            return driver.getTitle().contains("Insider");
        } catch (Exception e) {
            takeScreenshot("isHomePageDisplayed_Failure");
            throw e;
        }
    }

    public void jobList() {
        List<WebElement> jobs = driver.findElements(By.cssSelector("#jobs-list > div"));
        System.out.println("Total amount of job list: " + jobs.size());

        for (WebElement job : jobs) {
            System.out.println(job.getText());
        }

    }

    public void findYourDreamJobButton() {
        try {
            By findYourDreamJobLocator = By.xpath("//a[@class='btn btn-info rounded mr-0 mr-md-4 py-3']");
            waitForElement(findYourDreamJobLocator);
            WebElement element = driver.findElement(findYourDreamJobLocator);
            element.click();
        } catch (Exception e) {
            takeScreenshot("findYourDreamJobButton_Failure");
            throw e;
        }
    }

    public void firstJobClick() throws InterruptedException {
        try {
            By firstJobViewLocator = By.xpath("//section[@id='career-position-list']//div[@class='row']//div[1]//div[1]//a[1]");
            Dimension screenSize = driver.manage().window().getSize();
            int rate = screenSize.getHeight() / 2;
            scrollByOffset(0, rate);
            Thread.sleep(3000);
            List<WebElement> element = driver.findElements(firstJobViewLocator);
            element.get(0).click();
        } catch (Exception e) {
            takeScreenshot("firstJobClick_Failure");
            throw e;
        }
    }

    public boolean applyForThisJobButton() throws InterruptedException {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            By applyForThisJobButtonLocator = By.xpath("//div[@class='postings-btn-wrapper']//a[@class='postings-btn template-btn-submit shamrock'][normalize-space()='Apply for this job']");
            scrollToElement(applyForThisJobButtonLocator);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(applyForThisJobButtonLocator));
            return element.isDisplayed();
        } catch (Exception e) {
            takeScreenshot("applyForThisJobButton_Failure");
            throw e;
        }
    }

    public static void assertElementVisible(WebDriver driver, WebDriverWait wait, String xpath, String errorMessage) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        Assertions.assertTrue(element.isDisplayed(), errorMessage);
    }

    public boolean isJobDescriptionContains(String expectedText) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean webDriver = driver.getPageSource().contains(expectedText);
        return webDriver;

        //WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class=\"posting-headline\"]/h2[1]")));
        //return element.getText().contains(expectedText);

    }

    public void checkCareerPageContains() {
        try {
            assertElementVisible(driver, wait, "//section[contains(@class,'career-our-location')]", "Locations block is not found.");
        } catch (Exception e) {
            takeScreenshot("Career Page doesnt contains Locations.");
            throw e;
        }

        try {
            assertElementVisible(driver, wait, "//section[contains(@class,'career-find-our-calling')]", "Teams block is not found.");
        } catch (Exception e) {
            takeScreenshot("Career Page doesnt contains Teams.");
            throw e;
        }

        try {
            assertElementVisible(driver, wait, "//section[contains(@class,'career-life-at-insider')]", "Life at Insider blok not found.");
        } catch (Exception e) {
            takeScreenshot("Career Page doesnt contains Life.");
            throw e;
        }

    }

    public boolean careerPageDisplay() {

        try {
            return driver.getTitle().contains("Career");
        } catch (Exception e) {
            takeScreenshot("CareerPageDisplayed_Failure");
            throw e;
        }
    }

    public void dismissPrivacyNotice() {
        try {
            WebElement dismissButton= wait.until(ExpectedConditions
                    .elementToBeClickable(By.cssSelector(".message-buttons cc-desktop")));

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(dismissButton));
            button.click();
        } catch (TimeoutException e) {
            takeScreenshot("Privacy_Notice_Dismiss_Button_Failure");
            throw e;
        }
    }
}