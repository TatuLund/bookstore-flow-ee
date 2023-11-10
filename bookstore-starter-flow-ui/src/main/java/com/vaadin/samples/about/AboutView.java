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

@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout implements HasDynamicTitle {

    public static final String VIEW_NAME = "about";

    public AboutView() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.add(VaadinIcon.INFO_CIRCLE.create());
        hl.add(new Span(" This application is using Vaadin Flow "
                + Version.getFullVersion() + "."));

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
