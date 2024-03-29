package com.vaadin.samples;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.servlet.http.HttpServletResponse;

/**
 * View shown when trying to navigate to a view that does not exist using
 */

@SuppressWarnings("serial")
@ParentLayout(MainLayout.class)
public class ErrorView extends VerticalLayout
        implements HasErrorParameter<NotFoundException> {

    private static final String CANT_NAVIGATE = "cant-navigate";
    private static final String VIEW_NOT_FOUND = "view-not-found";

    private Span explanation;

    public ErrorView() {
        H1 header = new H1(getTranslation(VIEW_NOT_FOUND));
        header.addClassName(LumoUtility.TextColor.ERROR);
        add(header);

        explanation = new Span();
        add(explanation);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        explanation.setText(
                getTranslation(CANT_NAVIGATE, event.getLocation().getPath()));
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
