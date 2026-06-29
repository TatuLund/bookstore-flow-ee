package com.vaadin.samples;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SuppressWarnings("serial")
@Theme("bookstore")
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@PWA(name = "Bookstore Starter", shortName = "Bookstore")
public class Configuration implements AppShellConfigurator {

}
