package com.vaadin.samples.authentication;

import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;

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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.samples.AdminView;
import com.vaadin.samples.CookieUtil;
import com.vaadin.samples.CustomI18NProvider;
import com.vaadin.samples.MainLayout;

import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
@Route("Login")
@RouteScoped
@CdiComponent
public class LoginView extends FlexLayout implements HasDynamicTitle,
        LocaleChangeObserver, AfterNavigationObserver {

    private static final String LOGIN_INFO = "login-info";
    private static final String LOGIN_INFO_TEXT = "login-info-text";
    private static final String LOGIN_BUTTON = "login-button";
    private static final String LOGIN = "login";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String FORGOT_PASSWORD = "forgot-password";
    private static final String HINT = "hint";
    private AccessControl accessControl;
    private LoginForm loginForm;
    private Logger logger;
    private Span loginInfoText;
    private H1 loginInfoHeader;
    private Optional<Locale> locale;
    private Select<Locale> lang;;

    @Inject
    public LoginView(AccessControl accessControl, Logger logger) {
        this.logger = logger;
        this.accessControl = accessControl;
        buildUI();
    }

    private void buildUI() {
        setSizeFull();
        setClassName("login-screen");

        // login form, centered in the available part of the screen
        loginForm = new LoginForm();
        loginForm.addLoginListener(this::login);
        loginForm.addForgotPasswordListener(
                event -> Notification.show(getTranslation(HINT)));

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
        form.setForgotPassword(getTranslation(FORGOT_PASSWORD));
        form.setPassword(getTranslation(PASSWORD));
        form.setUsername(getTranslation(USERNAME));
        form.setTitle(getTranslation(LOGIN));
        form.setSubmit(getTranslation(LOGIN_BUTTON));
        i18n.setForm(form);
        return i18n;
    }

    private Component buildLoginInformation() {
        VerticalLayout loginInformation = new VerticalLayout();
        loginInformation.setClassName("login-information");

        loginInfoHeader = new H1(getTranslation(LOGIN_INFO));
        loginInfoHeader.setWidth("100%");
        loginInfoText = new Span(getTranslation(LOGIN_INFO_TEXT));
        loginInfoText.setWidth("100%");
        loginInformation.add(loginInfoHeader);
        loginInformation.add(loginInfoText);
        lang = new Select<>();
        lang.setItems(CustomI18NProvider.locales);
        lang.setItemLabelGenerator(item -> item.toString());
        loginInformation.add(lang);
        lang.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                getUI().ifPresent(ui -> {
                    ui.getSession().setAttribute("locale",
                            e.getValue().getLanguage());
                    ui.setLocale(e.getValue());
                    logger.info("Changing locale to {}",
                            e.getValue().getLanguage());
                });
            }
        });

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

    @SuppressWarnings("unchecked")
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
        return getTranslation(LOGIN);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        loginForm.setI18n(getI18n());
        loginInfoText.setText(getTranslation(LOGIN_INFO_TEXT));
        loginInfoHeader.setText(getTranslation(LOGIN_INFO));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        Cookie localeCookie = CookieUtil.getCookieByName("language",
                VaadinRequest.getCurrent());
        if (localeCookie != null && localeCookie.getValue() != null) {
            logger.info("Using stored locale {} from cookie.",
                    localeCookie.getValue());
            locale = CustomI18NProvider.locales.stream()
                    .filter(loc -> loc.getLanguage()
                            .equals(localeCookie.getValue()))
                    .findFirst();
            lang.setValue(locale.get());
            event.getLocationChangeEvent().getUI().setLocale(locale.get());
        }
    }

}
