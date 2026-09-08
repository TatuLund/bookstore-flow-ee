package com.vaadin.samples;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

}
