package com.vaadin.samples.crud;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.samples.AbstractViewTest;

class SampleCrudViewTest extends AbstractViewTest {

    private SampleCrudViewImpl view;

    @BeforeEach
    void setup() {
        login("admin", "admin");
        view = navigate(SampleCrudViewImpl.class);
    }

    @Test
    void shouldNavigateToSampleCrudView() {
        assertNotNull(view);
    }

    @Test
    void filterShouldWork() {
        var grid = find(Grid.class).single();
        var size = test(grid).size();
        test(find(TextField.class).id("filter")).setValue("beginners");
        var newSize = test(grid).size();
        assertNotEquals(size, newSize);
        assertTrue(newSize < size);
    }

    @Test
    void addAndRemoveBook() {
        test(find(Button.class).id("new-product")).click();
        assertNotNull(find(Dialog.class).single());

        test(find(TextField.class).id("product-name")).setValue("New Product");
        test(find(TextField.class).id("price")).setValue("10");
        test(find(TextField.class).id("stock-count")).setValue("0");
        var availability = find(Select.class).id("availability");
        test(availability).selectItem("Coming");
        var category = find(MultiSelectComboBox.class).id("category");
        test(category).selectItem("Children's books", "Best sellers");

        test(find(Button.class).id("save-button")).click();

        assertEquals("\"New Product\" created",
                test(find(Notification.class).last()).getText());

        test(find(TextField.class).id("filter")).setValue("new product");

        var grid = find(Grid.class).single();
        assertEquals("New Product", test(grid).getCellText(0, 0));
        assertEquals("10,00 €", test(grid).getLitRendererPropertyValue(0, 1,
                "price", String.class));
        assertEquals("Coming", test(grid).getLitRendererPropertyValue(0, 2,
                "availability", String.class));
        assertEquals("-", test(grid).getCellText(0, 3));
        assertEquals("Children's books, Best sellers",
                test(grid).getCellText(0, 4));

        test(grid).select(0);
        assertNotNull(find(Dialog.class).single());
        test(find(Button.class).id("delete-button")).click();

        test(find(ConfirmDialog.class).single()).confirm();

        test(find(TextField.class).id("filter")).setValue("New Product");
        var newSize = test(grid).size();
        assertEquals(0, newSize);
    }

    @Test
    void editBookAndRevertEdit() {
        var grid = find(Grid.class).single();
        var originalName = test(grid).getCellText(0, 0);
        var editedName = originalName + " edited";

        test(grid).select(0);
        test(find(TextField.class).id("product-name")).setValue(editedName);
        test(find(Button.class).id("save-button")).click();

        assertEquals(editedName, test(grid).getCellText(0, 0));

        test(grid).select(0);
        test(find(TextField.class).id("product-name")).setValue(originalName);
        test(find(Button.class).id("save-button")).click();

        assertEquals(originalName, test(grid).getCellText(0, 0));
    }

    @Test
    void bookFormRevertButton() {
        var grid = find(Grid.class).single();
        var originalName = test(grid).getCellText(0, 0);

        test(grid).select(0);
        var form = find(Dialog.class).single();
        var nameField = find(TextField.class).id("product-name");

        test(nameField).setValue(originalName + " edited");
        assertTrue(nameField.getClassNames().contains("dirty"));

        test(find(Button.class).id("discard-button")).click();
        assertEquals(originalName, nameField.getValue());
        assertFalse(nameField.getClassNames().contains("dirty"));

        test(find(Button.class).id("cancel-button")).click();
        assertFalse(form.isOpened());
    }

    @Test
    void bookFormCancelButton() {
        var grid = find(Grid.class).single();
        var originalName = test(grid).getCellText(0, 0);

        test(grid).select(0);
        var form = find(Dialog.class).single();
        var nameField = find(TextField.class).id("product-name");

        test(nameField).setValue(originalName + " edited");
        assertTrue(nameField.getClassNames().contains("dirty"));

        test(find(Button.class).id("cancel-button")).click();
        test(find(ConfirmDialog.class).single()).confirm();
        assertFalse(form.isOpened());
    }

    @Test
    void stockCountAvailabilityCrossValidation() {
        var grid = find(Grid.class).single();
        test(grid).select(0);

        var stockCount = find(TextField.class).id("stock-count");
        var availability = find(Select.class).id("availability");
        var save = find(Button.class).id("save-button");

        test(stockCount).setValue("1");
        test(availability).selectItem("Coming");
        test(save).click();

        assertTrue(stockCount.isInvalid());
        assertTrue(availability.isInvalid());

        test(find(Button.class).id("discard-button")).click();

        test(stockCount).setValue("0");
        test(availability).selectItem("Available");
        test(save).click();

        assertTrue(stockCount.isInvalid());
        assertTrue(availability.isInvalid());
    }
}
