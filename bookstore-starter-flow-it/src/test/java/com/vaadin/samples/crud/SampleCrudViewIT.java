package com.vaadin.samples.crud;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.grid.testbench.GridElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.samples.AbstractViewTest;
import com.vaadin.samples.MainLayoutElement;
import com.vaadin.samples.authentication.LoginViewElement;
import com.vaadin.testbench.BrowserTest;

@Execution(ExecutionMode.SAME_THREAD)
public class SampleCrudViewIT extends AbstractViewTest {

    @BrowserTest
    public void userSelectsProduct_cannotEditProductInformation() {
        // given authenticated as a regular user
        $(LoginViewElement.class).single().login("user", "user");

        // given "Inventory" is selected from the sidebar menu
        final MainLayoutElement mainLayout = $(MainLayoutElement.class)
                .single();
        mainLayout.clickMenuLink("Inventory");

        // when selecting an item from the product grid
        GridElement grid = $(GridElement.class).single();
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
        final MainLayoutElement mainElem = $(MainLayoutElement.class).single();
        mainElem.clickMenuLink("Inventory");

        // when selecting an item from the product grid
        GridElement grid = $(GridElement.class).single();
        grid.getCell(0, 0).click();

        // when altering the product name and clicking the save button
        final ProductFormElement prodForm = $(ProductFormElement.class)
                .single();
        waitUntil(_ -> prodForm.isOpen());
        Assertions.assertTrue(prodForm.isOpen(),
                "Product form should be visible");
        final String newTitle = "Cronan's Guide to Nanomixology";
        prodForm.getProductNameElement().setValue(newTitle);
        prodForm.getSaveButtonElement().click();

        // then the grid cell is updated to the new title
        Assertions.assertEquals(newTitle, grid.getCell(0, 0).getText(),
                "Title in grid not updated");
    }

    @BrowserTest
    public void adminCreatesNewProduct_productIsAvailableInGird() {
        // given authenticated as an admin
        $(LoginViewElement.class).single().login("admin", "admin");

        // given "Inventory" is selected from the sidebar menu
        final MainLayoutElement mainElem = $(MainLayoutElement.class).single();
        mainElem.clickMenuLink("Inventory");

        // when clicking the "New product" button
        $(ButtonElement.class).withAttribute("theme", "primary").single()
                .click();

        // when entering new product data and saving the product
        final ProductFormElement prodForm = $(ProductFormElement.class)
                .single();
        waitUntil(_ -> prodForm.isOpen());
        Assertions.assertTrue(prodForm.isOpen(),
                "Product form should be visible");
        final String newTitle = "Cronan's Guide to Nanomixology, 2nd ed.";
        prodForm.getProductNameElement().setValue(newTitle);
        prodForm.getSaveButtonElement().click();

        // then the new title is in the grid
        GridElement grid = $(GridElement.class).single();
        final boolean foundInGrid = IntStream.range(0, grid.getRowCount())
                .mapToObj(row -> grid.getCell(row, 0).getText())
                .anyMatch(newTitle::equals);
        Assertions.assertTrue(foundInGrid, "Title not found in grid");
    }
}
