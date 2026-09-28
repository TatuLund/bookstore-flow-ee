package com.vaadin.samples;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elementsbase.Element;

@Element("bookstore-title")
public class BookstoreTitleElement extends TestBenchElement {


    public TestBenchElement getTitle() {
        return $(TestBenchElement.class).id("title");
    }
}
