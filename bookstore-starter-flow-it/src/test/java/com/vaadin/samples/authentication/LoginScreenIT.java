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
    public void application_isLumoThemed() {
        assertTheme(Lumo.class);
    }

    @BrowserTest
    public void loginAsAdmin_hasAdminViewLink() {
        // when authenticating as admin
        $(LoginViewElement.class).single().login("admin", "admin");

        // then there is a link to admin's view
        Assertions.assertTrue(
                $(MainLayoutElement.class).single().hasMenuLink("Admin"),
                "Expected link to admin view");
    }

    @BrowserTest
    public void loginAsUser_noAdminViewLink() {
        // when authenticating as a regular user
        $(LoginViewElement.class).single().login("user", "user");

        // then there is no link to admin's view
        Assertions.assertFalse(
                $(MainLayoutElement.class).single().hasMenuLink("Admin"),
                "Expected no link to admin view");
    }
}
