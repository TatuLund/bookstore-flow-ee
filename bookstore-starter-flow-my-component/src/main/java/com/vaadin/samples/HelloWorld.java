package com.vaadin.samples;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;

@Tag("hello-world")
@JsModule("./src/hello-world.js")
@CssImport(value = "./styles/my-styles.css", themeFor = "hello-world")
public class HelloWorld extends Component {

}
