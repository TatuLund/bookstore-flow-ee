package com.vaadin.samples.authentication;

import com.vaadin.flow.component.login.testbench.LoginFormElement;
import com.vaadin.flow.component.select.testbench.SelectElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Element("div")
@Attribute(name = "class", contains = "login-screen")
public class LoginViewElement extends TestBenchElement {

    public void login(String username, String password) {
        waitUntil(ExpectedConditions
                .presenceOfElementLocated(By.id("vaadinLoginUsername")));

        $(SelectElement.class).single().selectByText("en_GB");

        var form = $(LoginFormElement.class).single();
        form.getUsernameField().setValue(username);
        form.getPasswordField().setValue(password);
        form.submit();
    }
}
