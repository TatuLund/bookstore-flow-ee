package com.vaadin.samples;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.Assertions;
import com.vaadin.testbench.BrowserTest;

@Execution(ExecutionMode.SAME_THREAD)
public class BookstoreTitleIT extends AbstractComponentTest {

    public BookstoreTitleIT() {
        super("bookstore-title");
    }

    @BrowserTest
    void testSplashAnimation() {
        var title = $(BookstoreTitleElement.class).waitForFirst();

        String animationName = (String) executeScript("""
                return getComputedStyle(arguments[0]).animationName;
                    """, title.getTitle());

        Assertions.assertEquals("title-color-splash", animationName,
                "BookstoreTitle should have a splash animation");
    }
}
