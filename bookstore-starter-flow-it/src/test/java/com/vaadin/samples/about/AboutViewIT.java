package com.vaadin.samples.about;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.Assertions;

import com.vaadin.flow.component.html.testbench.SpanElement;
import com.vaadin.samples.AbstractViewTest;
import com.vaadin.samples.MainLayoutElement;
import com.vaadin.samples.authentication.LoginFormElement;
import com.vaadin.testbench.BrowserTest;

@Execution(ExecutionMode.SAME_THREAD)
public class AboutViewIT extends AbstractViewTest {

    @BrowserTest
    public void openAboutView_showsFlowVersion() {
        // given authenticated as a regular user
        $(LoginFormElement.class).single().login("user", "user");

        // when selecting "About" from the sidebar menu
        final MainLayoutElement mainElem = $(MainLayoutElement.class).single();
        mainElem.clickMenuLink("About");

        // then the view contents a span with Flow version information
        final SpanElement aboutSpan = mainElem.$(SpanElement.class).last();

        Assertions.assertTrue(aboutSpan.getText().contains("Flow"),
                "Expected link to admin view");
    }
}
