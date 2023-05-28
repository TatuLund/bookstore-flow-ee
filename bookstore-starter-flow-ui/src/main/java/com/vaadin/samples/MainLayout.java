package com.vaadin.samples;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.Command;
import com.vaadin.samples.about.AboutView;
import com.vaadin.samples.authentication.AccessControl;
import com.vaadin.samples.crud.SampleCrudViewImpl;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

/**
 * The main layout. Contains the navigation menu.
 */
@Dependent
@CdiComponent
public class MainLayout extends FlexLayout implements RouterLayout {

    private Menu menu;

    private AccessControl accessControl;

    private Command addAdminMenuItemCommand;

    @Inject
    public MainLayout(Menu menu, AccessControl accessControl) {
        this.accessControl = accessControl;
        this.menu = menu;
        setSizeFull();
        setClassName("main-layout");
        menu.addView(SampleCrudViewImpl.class, SampleCrudViewImpl.VIEW_NAME,
                VaadinIcon.EDIT.create());
        menu.addView(AboutView.class, AboutView.VIEW_NAME,
                VaadinIcon.INFO_CIRCLE.create());
        add(menu);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        attachEvent.getUI().addShortcutListener(() -> accessControl.signOut(),
                Key.KEY_L, KeyModifier.CONTROL);

        // add the admin view menu item if/when it is registered dynamically
        if (addAdminMenuItemCommand == null) {
            addAdminMenuItemCommand = () -> menu.addView(AdminView.class,
                    AdminView.VIEW_NAME, VaadinIcon.DOCTOR.create());
        }
        RouteConfiguration sessionScopedConfiguration = RouteConfiguration
                .forSessionScope();
        if (sessionScopedConfiguration.isRouteRegistered(AdminView.class)) {
            addAdminMenuItemCommand.execute();
        } else {
            sessionScopedConfiguration.addRoutesChangeListener(event -> {
                for (RouteBaseData data : event.getAddedRoutes()) {
                    if (data.getNavigationTarget().equals(AdminView.class)) {
                        addAdminMenuItemCommand.execute();
                    }
                }
            });
        }
    }
}
