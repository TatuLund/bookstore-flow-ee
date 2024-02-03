package com.vaadin.samples;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.samples.authentication.AccessControl;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@SuppressWarnings("serial")
@Dependent
@CdiComponent
public class Menu extends FlexLayout implements LocaleChangeObserver {

    private static final String LOGOUT = "logout";
    private static final String MENU = "menu";
    private static final String BOOKSTORE = "bookstore";
    private static final String SHOW_TABS = "show-tabs";
    private SideNav sideNav;
    private H3 title;
    private Button showMenu;
    private Button logoutButton;

    @Inject
    public Menu(AccessControl accessControl) {
        setClassName("menu-bar");

        // Button for toggling the menu visibility on small screens
        showMenu = new Button(getTranslation(MENU), event -> {
            if (sideNav.getClassNames().contains(SHOW_TABS)) {
                sideNav.removeClassName(SHOW_TABS);
            } else {
                sideNav.addClassName(SHOW_TABS);
            }
        });
        showMenu.setClassName("menu-button");
        showMenu.addThemeVariants(ButtonVariant.LUMO_SMALL);
        showMenu.setIcon(new Icon(VaadinIcon.MENU));
        add(showMenu);

        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        top.setClassName("menu-header");

        title = new H3(getTranslation(BOOKSTORE));

        String resolvedImage = VaadinServletService.getCurrent()
                .resolveResource("img/table-logo.png");

        Image image = new Image(resolvedImage, "");
        top.add(image);
        top.add(title);
        add(top);

        // container for the navigation buttons, which are added by addView()
        sideNav = new SideNav();
        sideNav.addClassName(LumoUtility.Padding.MEDIUM);
        add(sideNav);

        // logout menu item
        logoutButton = new Button(getTranslation(LOGOUT),
                VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(event -> accessControl.signOut());
        logoutButton.addClassNames(LumoUtility.Margin.Top.AUTO,
                LumoUtility.Padding.MEDIUM);

        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        add(logoutButton);
    }

    /**
     * Add a view to the navigation menu
     *
     * @param viewClass
     *            that has a {@code Route} annotation
     * @param caption
     *            view caption in the menu
     * @param icon
     *            view icon in the menu
     */
    public void addView(Class<? extends Component> viewClass, String caption,
            Icon icon) {
        SideNavItem tab = new SideNavItem(caption, viewClass, icon);
        sideNav.addItem(tab);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        title.setText(getTranslation(BOOKSTORE));
        showMenu.setText(getTranslation(MENU));
        logoutButton.setText(getTranslation(LOGOUT));        
    }
}
