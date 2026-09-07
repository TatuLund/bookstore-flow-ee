package com.vaadin.samples;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;

@SuppressWarnings("serial")
@StyleSheet("styles.css")
@PWA(name = "Bookstore Starter", shortName = "Bookstore")
public class Configuration implements AppShellConfigurator {

}
