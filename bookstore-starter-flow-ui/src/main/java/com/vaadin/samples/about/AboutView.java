package com.vaadin.samples.about;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Version;
import com.vaadin.samples.BookstoreTitle;
import com.vaadin.samples.MainLayout;

@SuppressWarnings("serial")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout implements HasDynamicTitle {

    private static final String INFO_TEXT = "info-text";
    public static final String VIEW_NAME = "about";

    public AboutView() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.add(VaadinIcon.INFO_CIRCLE.create());
        hl.add(new Span(getTranslation(INFO_TEXT, Version.getFullVersion())));

        hl.setSizeFull();
        hl.setJustifyContentMode(JustifyContentMode.CENTER);
        hl.setAlignItems(Alignment.CENTER);
        add(new BookstoreTitle(), hl);
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
    }

    @Override
    public String getPageTitle() {
        return getTranslation(VIEW_NAME);
    }
}
