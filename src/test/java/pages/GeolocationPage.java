package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class GeolocationPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(css = "button[onclick='getLocation()']")
    private WebElement getLocationButton;

    @FindBy(id = "lat-value")
    private WebElement latValue;

    @FindBy(id = "long-value")
    private WebElement longValue;

    @FindBy(linkText = "See it on Google Maps")
    private WebElement mapsLink;

    public GeolocationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("https://the-internet.herokuapp.com/geolocation");
    }

    public boolean isButtonVisible() {
        wait.until(ExpectedConditions.visibilityOf(getLocationButton));
        return getLocationButton.isDisplayed();
    }

    public String getButtonText() {
        wait.until(ExpectedConditions.visibilityOf(getLocationButton));
        return getLocationButton.getText();
    }

    public void clickGetLocation() {
        wait.until(ExpectedConditions.elementToBeClickable(getLocationButton)).click();
    }

    public String getLatitudeText() {
        wait.until(driver -> {
            String text = latValue.getText().trim();
            return !text.isEmpty();
        });
        return latValue.getText().trim();
    }

    public String getLongitudeText() {
        wait.until(driver -> {
            String text = longValue.getText().trim();
            return !text.isEmpty();
        });
        return longValue.getText().trim();
    }

    public String getMapsHref() {
        wait.until(ExpectedConditions.visibilityOf(mapsLink));
        return mapsLink.getAttribute("href");
    }
}
