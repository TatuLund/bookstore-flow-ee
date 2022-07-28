package com.vaadin.samples;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("bookstore-title")
@JsModule("./src/bookstore-title.js")
@CssImport(value = "./styles/my-styles.css", themeFor = "bookstore-title")
public class BookstoreTitle extends Component {

}
