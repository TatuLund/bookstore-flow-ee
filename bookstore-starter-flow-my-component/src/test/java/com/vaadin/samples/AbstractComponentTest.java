package com.vaadin.samples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.vaadin.testbench.BrowserTestBase;
import com.vaadin.testbench.DriverSupplier;
import com.vaadin.testbench.Parameters;
import com.vaadin.testbench.ScreenshotOnFailureExtension;

/**
 * Base class for ITs
 * <p>
 * The tests use Chrome driver (see pom.xml for integration-tests profile) to
 * run integration tests on a headless Chrome. If a property {@code test.use
 * .hub} is set to true, {@code AbstractComponentTest} will assume that the
 * TestBench test is running in a CI environment. In order to keep the this
 * class light, it makes certain assumptions about the CI environment (such as
 * available environment variables). It is not advisable to use this class as a
 * base class for you own TestBench tests.
 * <p>
 * To learn more about TestBench, visit <a href=
 * "https://vaadin.com/docs/v10/testbench/testbench-overview.html">Vaadin
 * TestBench</a>.
 */
public abstract class AbstractComponentTest extends BrowserTestBase
        implements DriverSupplier {
    private static final int SERVER_PORT = 8080;

    private final String route;

    @RegisterExtension
    public ScreenshotOnFailureExtension screenshotOnFailureExtension = new ScreenshotOnFailureExtension(
            this, true);

    public AbstractComponentTest() {
        this("");
    }

    protected AbstractComponentTest(String route) {
        this.route = route;
    }

    @Override
    public WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu");
        return new ChromeDriver(options);
    }

    @BeforeEach
    public void setup() {
        getDriver().get(getURL(route));

        Parameters.setScreenshotComparisonTolerance(0.05);
        Parameters.setScreenshotComparisonCursorDetection(true);
        testBench().resizeViewPortTo(1280, 800);
        Parameters.setMaxScreenshotRetries(3);
        Parameters.setScreenshotRetryDelay(1000);

        waitForDevServer();

        disableDevTools();
        disableCopilot();
    }

    private void disableDevTools() {
        try {
            $("vaadin-dev-tools").single().setProperty("hidden", true);
        } catch (NotFoundException _) {
            // Ignore if elements are not found
        }
    }

    private void disableCopilot() {
        try {
            $("copilot-main").single().setProperty("hidden", true);
        } catch (NotFoundException _) {
            // Ignore if elements are not found
        }
    }

    /**
     * Property set to true when running on a test hub.
     */
    private static final String USE_HUB_PROPERTY = "test.use.hub";

    /**
     * Returns deployment host name concatenated with route.
     *
     * @return URL to route
     */
    private static String getURL(String route) {
        return String.format("http://%s:%d/%s", getDeploymentHostname(),
                SERVER_PORT, route);
    }

    /**
     * 
     * Returns whether we are using a test hub. This means that the starter is
     * running tests in Vaadin's CI environment, and uses TestBench to connect
     * to the testing hub.
     *
     * @return whether we are using a test hub
     */
    private static boolean isUsingHub() {
        return Boolean.TRUE.toString()
                .equals(System.getProperty(USE_HUB_PROPERTY));
    }

    /**
     * If running on CI, get the host name from environment variable HOSTNAME
     *
     * @return the host name
     */
    private static String getDeploymentHostname() {
        return isUsingHub() ? System.getenv("HOSTNAME") : "localhost";
    }
}
