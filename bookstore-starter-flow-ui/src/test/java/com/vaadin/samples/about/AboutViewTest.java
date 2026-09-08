package com.vaadin.samples.about;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.samples.AbstractViewTest;
import com.vaadin.samples.BookstoreTitle;

class AboutViewTest extends AbstractViewTest {

    private AboutView aboutView;

    @BeforeEach 
    void setup() {
        login("admin", "admin");
        aboutView = navigate(AboutView.class);
    }

    @Test
    void shouldNavigateToAboutView() {
        // Assert that the current view is the AboutView
        assertNotNull(aboutView);
    }

    @Test
    void hasBookstoreTitle() {
        var title = find(BookstoreTitle.class).single();
        assertNotNull(aboutView);
        assertNotNull(title);
    }
}
