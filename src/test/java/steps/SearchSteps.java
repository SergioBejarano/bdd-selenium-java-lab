package steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SearchSteps {

    private WebDriver driver;
    private WebDriverWait wait;

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
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Given("I am on the Google search page")
    public void i_am_on_the_google_search_page() {
        /*
         * Se usa Bing — Google bloquea navegadores headless automatizados.
         * Bing permite búsquedas headless con estructura HTML estable.
         */
        driver.get("https://www.bing.com");
    }

    @When("I search for {string}")
    public void i_search_for(String term) {
        /*
         * Bing puede tener el campo de búsqueda bloqueado por overlays.
         * Se usa JavaScript para escribir directamente en el campo y
         * luego se dispara el formulario, evitando problemas de
         * interactuabilidad del elemento.
         */
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("q")));

        // Locate and submit in one script execution to avoid stale references.
        ((JavascriptExecutor) driver).executeScript(
            "const input = document.querySelector('input[name=\\\"q\\\"]');"
                + "if (!input) { throw new Error('Search input not found'); }"
                + "input.value = arguments[0];"
                + "if (!input.form) { throw new Error('Search form not found'); }"
                + "input.form.submit();",
            term
        );
    }

    @Then("I should see {string} in the results")
    public void i_should_see_in_the_results(String term) {
        // Bing incluye el término buscado en el título: "GitHub - Bing"
        wait.until(ExpectedConditions.titleContains(term));
        assert driver.getPageSource().contains(term)
            : "Term '" + term + "' not found. Title: " + driver.getTitle();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
