package com.vaadin.samples.authentication;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.Form;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.samples.AdminView;
import com.vaadin.samples.MainLayout;

import jakarta.inject.Inject;

/**
 * UI content when the user is not logged in yet.
 */
@Route("Login")
@RouteScoped
@CdiComponent
public class LoginScreen extends FlexLayout implements HasDynamicTitle {

    private AccessControl accessControl;

    @Inject
    public LoginScreen(AccessControl accessControl) {
        this.accessControl = accessControl;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setClassName("login-screen");

        // login form, centered in the available part of the screen
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(
                event -> Notification.show(getTranslation("hint")));

        loginForm.setI18n(getI18n());

        // layout to center login form when there is sufficient screen space
        FlexLayout centeringLayout = new FlexLayout();
        centeringLayout.setSizeFull();
        centeringLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        centeringLayout.setAlignItems(Alignment.CENTER);
        centeringLayout.add(loginForm);

        // information text about logging in
        Component loginInformation = buildLoginInformation();

        add(loginInformation);
        add(centeringLayout);
    }

    private LoginI18n getI18n() {
        LoginI18n i18n = new LoginI18n();
        Form form = new Form();
        form.setForgotPassword(getTranslation("forgot-password"));
        form.setPassword(getTranslation("password"));
        form.setUsername(getTranslation("username"));
        form.setTitle(getTranslation("login"));
        form.setSubmit(getTranslation("login-button"));
        i18n.setForm(form);
        return i18n;
    }

    private Component buildLoginInformation() {
        VerticalLayout loginInformation = new VerticalLayout();
        loginInformation.setClassName("login-information");

        H1 loginInfoHeader = new H1(getTranslation("login-info"));
        loginInfoHeader.setWidth("100%");
        Span loginInfoText = new Span(getTranslation("login-info-text"));
        loginInfoText.setWidth("100%");
        loginInformation.add(loginInfoHeader);
        loginInformation.add(loginInfoText);

        return loginInformation;
    }

    private void login(LoginForm.LoginEvent event) {
        if (accessControl.signIn(event.getUsername(), event.getPassword())) {
            VaadinServletRequest request = (VaadinServletRequest) VaadinService
                    .getCurrentRequest();
            request.getHttpServletRequest().changeSessionId();
            registerAdminViewIfApplicable();
            getUI().get().navigate("");
        } else {
            event.getSource().setError(true);
        }
    }

    private void registerAdminViewIfApplicable() {
        // register the admin view dynamically only for any admin user logged in
        if (accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)
                && !RouteConfiguration.forSessionScope()
                        .isRouteRegistered(AdminView.class)) {
            RouteConfiguration.forSessionScope().setRoute(AdminView.VIEW_NAME,
                    AdminView.class, MainLayout.class);
            // as logout will purge the session route registry, no need to
            // unregister the view on logout
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation("login");
    }

}
