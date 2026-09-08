package com.vaadin.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;

class AdminViewTest extends AbstractViewTest {

    private AdminView adminView;

    @BeforeEach
    void setup() {
        login("admin", "admin");
        adminView = navigate(AdminView.class);
    }

    @Test
    void shouldNavigateToAdminView() {
        // Assert that the current view is the AdminView
        assertNotNull(adminView);
    }

    @Test
    void addAndRemoveCategory() {
        var categories = find(VirtualList.class).single();
        var originalSize = test(categories).size();

        test(find(Button.class).withText("Add new category").single()).click();

        assertEquals(originalSize + 1, test(categories).size());
        var newCategory = test(categories).getItemComponent(originalSize);
        var nameField = find(TextField.class).from(newCategory).single();
        var deleteButton = find(Button.class).from(newCategory).single();

        nameField.setValue("New category");

        // assert save notification
        var notification = find(Notification.class).last();
        assertEquals("Category saved.", test(notification).getText());

        assertEquals("New category", nameField.getValue());
        assertTrue(deleteButton.isEnabled());

        deleteButton.click();

        assertEquals(originalSize, test(categories).size());
    }
}
