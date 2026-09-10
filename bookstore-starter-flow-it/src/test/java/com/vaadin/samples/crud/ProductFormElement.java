package com.vaadin.samples.crud;

import com.vaadin.flow.component.button.testbench.ButtonElement;
import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
import com.vaadin.flow.component.dialog.testbench.DialogElement;
import com.vaadin.testbench.annotations.Attribute;

@Attribute(name = "class", contains = "product-form")
public class ProductFormElement extends DialogElement {

    public TextFieldElement getProductNameElement() {
        return $(TextFieldElement.class).id("product-name");
    }

    public ButtonElement getSaveButtonElement() {
        return $(ButtonElement.class).id("save-button");
    }


}
