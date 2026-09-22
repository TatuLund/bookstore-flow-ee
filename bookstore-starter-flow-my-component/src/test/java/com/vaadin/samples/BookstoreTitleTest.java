package com.vaadin.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class BookstoreTitleTest {

    @Test
    void testTitle() {
        var title = new BookstoreTitle();
        assertEquals("bookstore-title", title.getElement().getTag());
    }
}
