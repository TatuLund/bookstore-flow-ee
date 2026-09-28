package com.vaadin.samples;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import com.vaadin.testbench.BrowserTest;

@Execution(ExecutionMode.SAME_THREAD)
public class BookstoreTitleIT extends AbstractComponentTest {

    public BookstoreTitleIT() {
        super("bookstore-title");
    }

    @BrowserTest
    void testSplashAnimation() {
        var title = $(BookstoreTitleElement.class).waitForFirst();

        Boolean hasAnimation = (Boolean) executeScript("""
                return arguments[0]
                    .getAnimations({ subtree: true }).length > 0;
                """, title.getTitle());

        assertTrue(hasAnimation,
                "BookstoreTitle should have a splash animation");
    }
}
