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

        // Windows GHA runners may prefer reduced motion even when the local
        // development environment does not.
        Boolean prefersReducedMotion = (Boolean) executeScript("""
                return matchMedia('(prefers-reduced-motion: reduce)').matches;
                """);

        // The component intentionally disables its splash animation when the
        // browser requests reduced motion.
        String expectedAnimation = prefersReducedMotion ? "none"
                : "title-color-splash";
        Assertions.assertEquals(expectedAnimation, animationName,
                "BookstoreTitle should respect the browser's motion preference");
    }
}
