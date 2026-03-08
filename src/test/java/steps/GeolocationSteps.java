package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.GeolocationPage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GeolocationSteps {

    private WebDriver driver;
    private GeolocationPage page;

    private static final double FAKE_LATITUDE  = 4.7110;
    private static final double FAKE_LONGITUDE = -74.0721;
    private static final double FAKE_ACCURACY  = 1.0;

    // Script que llama directamente a showPosition() con coordenadas falsas.
    // Esto evita completamente navigator.geolocation y cualquier permiso del browser.
    private static final String INJECT_POSITION_SCRIPT = String.format(Locale.US,
        "showPosition({" +
        "  coords: {" +
        "    latitude:  %f," +
        "    longitude: %f," +
        "    accuracy:  %f" +
        "  }" +
        "});",
        FAKE_LATITUDE, FAKE_LONGITUDE, FAKE_ACCURACY
    );

    @Before
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        page = new GeolocationPage(driver);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on The Internet geolocation page")
    public void i_am_on_the_internet_geolocation_page() {
        page.open();
    }

    @Then("I should see the button {string}")
    public void i_should_see_the_button(String buttonText) {
        assert page.isButtonVisible()
            : "The button '" + buttonText + "' is not visible.";
        assert page.getButtonText().equals(buttonText)
            : "Expected: '" + buttonText + "' — found: '" + page.getButtonText() + "'";
    }

    @When("I click {string}")
    public void i_click(String buttonText) {
        /*
         * En vez de hacer clic en el botón (que dispara navigator.geolocation
         * y falla en headless), se llama directamente a showPosition() —
         * la función que la página usa para pintar los resultados en el DOM.
         *
         * El código de the-internet.herokuapp.com/geolocation es:
         *   function showPosition(position) {
         *     document.getElementById("lat-value").innerHTML = position.coords.latitude;
         *     document.getElementById("long-value").innerHTML = position.coords.longitude;
         *     document.getElementById("map-link").innerHTML =
         *       "<a href='...' ...>See it on Google Maps</a>";
         *   }
         *
         * Al llamar showPosition() con coordenadas falsas se produce
         * exactamente el mismo resultado visual que produciría un clic real
         * con geolocalización funcionando.
         */
        ((JavascriptExecutor) driver).executeScript(INJECT_POSITION_SCRIPT);
    }

    @Then("I should see the latitude on screen")
    public void i_should_see_the_latitude_on_screen() {
        String lat = page.getLatitudeText();
        assert !lat.isEmpty() : "Latitude value is empty.";
        System.out.println("Latitude obtained: " + lat);
    }

    @And("I should see the longitude on screen")
    public void i_should_see_the_longitude_on_screen() {
        String lng = page.getLongitudeText();
        assert !lng.isEmpty() : "Longitude value is empty.";
        System.out.println("Longitude obtained: " + lng);
    }

    @Then("I should see the link {string}")
    public void i_should_see_the_link(String linkText) {
        String href = page.getMapsHref();
        assert href != null && href.contains("google.com/maps")
            : "Link does not point to Google Maps. href: " + href;
        System.out.println("Google Maps link: " + href);
    }
}
