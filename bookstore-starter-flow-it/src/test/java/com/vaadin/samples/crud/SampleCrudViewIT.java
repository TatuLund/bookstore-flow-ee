package com.vaadin.samples.crud;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.samples.AbstractViewTest;
import com.vaadin.samples.MainLayoutElement;
import com.vaadin.samples.authentication.LoginViewElement;
import com.vaadin.testbench.BrowserTest;
import com.vaadin.testbench.screenshot.ImageFileUtil;

@Execution(ExecutionMode.SAME_THREAD)
public class SampleCrudViewIT extends AbstractViewTest {

    @BrowserTest
    public void userSelectsProduct_cannotEditProductInformation() {
        // given authenticated as a regular user
        $(LoginViewElement.class).single().login("user", "user");

        // given "Inventory" is selected from the sidebar menu
        var mainLayout = $(MainLayoutElement.class).single();
        mainLayout.clickMenuLink("Inventory");

        // when selecting an item from the product grid
        var grid = $(GridElement.class).single();
        grid.getCell(0, 0).click();

        // then the product data is not editable
        var form = $(DialogElement.class).withAttribute("class", "product-form")
                .single();

        Assertions.assertFalse(form.isOpen(),
                "Product form should not be visible");
    }

    @BrowserTest
    public void adminSelectsProduct_canUpdateProductInformation() {
        // given authenticated as an admin
        $(LoginViewElement.class).single().login("admin", "admin");

        // given "Inventory" is selected from the sidebar menu
        var mainElem = $(MainLayoutElement.class).single();
        mainElem.clickMenuLink("Inventory");

        // when selecting an item from the product grid
        var grid = $(GridElement.class).single();
        grid.getCell(0, 0).click();

        // when altering the product name and clicking the save button
        var prodForm = $(ProductFormElement.class).single();
        waitUntil(_ -> prodForm.isOpen());
        Assertions.assertTrue(prodForm.isOpen(),
                "Product form should be visible");
        var oldTitle = grid.getCell(0, 0).getText();
        var newTitle = "Cronan's Guide to Nanomixology";
        prodForm.getProductNameElement().setValue(newTitle);
        prodForm.getSaveButtonElement().click();

        // then the grid cell is updated to the new title
        Assertions.assertEquals(newTitle, grid.getCell(0, 0).getText(),
                "Title in grid not updated");

        // Revert the product title to the old value
        grid.getCell(0, 0).click();
        // Assert that vale is the new title in the form
        Assertions.assertEquals(newTitle,
                prodForm.getProductNameElement().getValue(),
                "Product form should have the new title before reverting");
        prodForm.getProductNameElement().setValue(oldTitle);
        prodForm.getSaveButtonElement().click();
        waitUntil(_ -> !prodForm.isOpen());
        Assertions.assertFalse(prodForm.isOpen(),
                "Product form should not be visible");
    }

    @BrowserTest
    public void adminCreatesNewProduct_productIsAvailableInGird() {
        // given authenticated as an admin
        $(LoginViewElement.class).single().login("admin", "admin");

        // given "Inventory" is selected from the sidebar menu
        var mainElem = $(MainLayoutElement.class).single();
        mainElem.clickMenuLink("Inventory");

        // when clicking the "New product" button
        $(ButtonElement.class).withAttribute("theme", "primary").single()
                .click();

        // when entering new product data and saving the product
        var prodForm = $(ProductFormElement.class).single();
        waitUntil(_ -> prodForm.isOpen());
        Assertions.assertTrue(prodForm.isOpen(),
                "Product form should be visible");
        var newTitle = "Cronan's Guide to Nanomixology, 2nd ed.";
        prodForm.getProductNameElement().setValue(newTitle);
        prodForm.getSaveButtonElement().click();

        // then the new title is in the grid
        var grid = $(GridElement.class).single();
        var foundInGrid = IntStream.range(0, grid.getRowCount())
                .mapToObj(row -> grid.getCell(row, 0).getText())
                .anyMatch(newTitle::equals);
        Assertions.assertTrue(foundInGrid, "Title not found in grid");
    }

    @BrowserTest
    public void accessibilityCheck() {
        var axeBuilder = new AxeBuilder();
        // False positives for these elements
        axeBuilder.exclude("vaadin-connection-indicator");
        axeBuilder.exclude("vaadin-select-value-button");
        axeBuilder.exclude("vaadin-vertical-layout > span");

        var axeResults = axeBuilder.analyze(getDriver());
        logViolations(axeResults);
        assertTrue(axeResults.violationFree());
    }

    @BrowserTest
    public void responsiveVisualTest() throws IOException {
        // given authenticated as an admin
        $(LoginViewElement.class).single().login("admin", "admin");

        Assertions.assertTrue(testBench().compareScreen(ImageFileUtil
                .getReferenceScreenshotFile("crud-view-large.png")));
        testBench().resizeViewPortTo(800, 600);
        $(ButtonElement.class).withAttribute("class", "menu-button").single()
                .click();
        Assertions.assertTrue(testBench().compareScreen(ImageFileUtil
                .getReferenceScreenshotFile("crud-view-small.png")));

    }
}
