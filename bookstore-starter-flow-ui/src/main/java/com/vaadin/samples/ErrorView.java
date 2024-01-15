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

@ParentLayout(MainLayout.class)
public class ErrorView extends VerticalLayout
        implements HasErrorParameter<NotFoundException> {

    private Span explanation;

    public ErrorView() {
        H1 header = new H1(getTranslation("view-not-found"));
        header.addClassName(LumoUtility.TextColor.ERROR);
        add(header);

        explanation = new Span();
        add(explanation);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
            ErrorParameter<NotFoundException> parameter) {
        explanation.setText(
                getTranslation("cant-navigate", event.getLocation().getPath()));
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
