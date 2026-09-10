package com.vaadin.samples;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.flow.component.sidenav.testbench.SideNavElement;
import com.vaadin.flow.component.sidenav.testbench.SideNavItemElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.annotations.Attribute;
import com.vaadin.testbench.elementsbase.Element;

@Element("div")
@Attribute(name = "class", contains = "main-layout")
public class MainLayoutElement extends TestBenchElement {

    public List<SideNavItemElement> findMenuLinks() {
        return $(SideNavElement.class).single().getItems();
    }

    public boolean hasMenuLink(String label) {
        return $(SideNavElement.class).single().getItemByLabel(label) != null;
    }

    public void clickMenuLink(String label) {
        if (hasMenuLink(label)) {
            $(SideNavElement.class).single().getItemByLabel(label).click();
            return;
        } else {
            throw new IllegalArgumentException("No " + label);
        }
    }
}
