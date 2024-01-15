package com.vaadin.samples;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@SuppressWarnings("serial")
@Theme("bookstore")
@PWA(name = "Bookstore Starter", shortName = "Bookstore")
public class Configuration implements AppShellConfigurator {

}
