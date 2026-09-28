package com.vaadin.samples;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

@RouteAlias("")
@Route("bookstore-title")
public class BookstoreTitleView extends VerticalLayout {

    public BookstoreTitleView() {
        var title = new BookstoreTitle();
        add(title);
    }
}
