package com.vaadin.samples.authentication;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.samples.AbstractViewTest;
import org.junit.jupiter.api.Assertions;
import com.vaadin.samples.MainLayoutElement;
import com.vaadin.testbench.BrowserTest;

@Execution(ExecutionMode.SAME_THREAD)
public class LoginScreenIT extends AbstractViewTest {

    @BrowserTest
    public void loginForm_isLumoThemed() {
        LoginFormElement loginForm = $(LoginFormElement.class).single();
        assertThemePresentOnElement(loginForm, Lumo.class);
    }

    @BrowserTest
    public void loginAsAdmin_hasAdminViewLink() {
        // when authenticating as admin
        $(LoginFormElement.class).single().login("admin", "admin");

        // then there is a link to admin's view
        Assertions.assertTrue(
                $(MainLayoutElement.class).single().hasMenuLink("admin"),
                "Expected link to admin view");
    }

    @BrowserTest
    public void loginAsUser_noAdminViewLink() {
        // when authenticating as a regular user
        $(LoginFormElement.class).single().login("user", "user");

        // then there is no link to admin's view
        Assertions.assertFalse(
                $(MainLayoutElement.class).single().hasMenuLink("admin"),
                "Expected no link to admin view");
    }
}
