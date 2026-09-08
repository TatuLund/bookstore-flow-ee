package com.vaadin.samples.authentication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.flow.component.select.Select;
import com.vaadin.samples.AbstractViewTest;

class LoginViewTest extends AbstractViewTest {

    private LoginView loginView;

    @BeforeEach
    void setup() {
        loginView = navigate(LoginView.class);
    }

    @Test
    void shouldNavigateToLoginView() {
        // Assert that the current view is the LoginView
        assertNotNull(loginView);
    }

    @Test
    void changeLanguage() {
        var language = find(Select.class).id("language");
        test(language).selectItem("en_GB");
        assertEquals("Username", loginView.getI18n().getForm().getUsername());
        assertEquals("Password", loginView.getI18n().getForm().getPassword());
        test(language).selectItem("fi_FI");
        assertEquals("Käyttäjänimi",
                loginView.getI18n().getForm().getUsername());
        assertEquals("Salasana", loginView.getI18n().getForm().getPassword());
    }

}
