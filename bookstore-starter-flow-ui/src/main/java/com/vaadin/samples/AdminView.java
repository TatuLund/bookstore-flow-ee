package com.vaadin.samples;

import java.util.ArrayList;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Category;

import jakarta.inject.Inject;

import static com.vaadin.samples.AdminView.VIEW_NAME;

/**
 * Admin view that is registered dynamically on admin user login.
 * <p>
 * Allows CRUD operations for the book categories.
 */
@SuppressWarnings("serial")
@RouteScoped
@RouteScopeOwner(MainLayout.class)
@CdiComponent
public class AdminView extends VerticalLayout
        implements HasDynamicTitle, LocaleChangeObserver {

    public static final String VIEW_NAME = "admin";

    private final VirtualList<Category> categoriesListing;
    private final ListDataProvider<Category> dataProvider;
    private final Button newCategoryButton;
    private DataService dataService;
    private H2 h2;
    private H4 h4;

    @Inject
    public AdminView(DataService dataService) {
        this.dataService = dataService;
        categoriesListing = new VirtualList<>();

        dataProvider = new ListDataProvider<Category>(
                new ArrayList<>(dataService.getAllCategories())) {
            @Override
            public Object getId(Category item) {
                return item.getId();
            }
        };
        categoriesListing.setDataProvider(dataProvider);
        categoriesListing.setRenderer(
                new ComponentRenderer<>(this::createCategoryEditor));

        newCategoryButton = new Button(getTranslation("add-new-category"),
                event -> {
                    Category category = new Category();
                    dataProvider.getItems().add(category);
                    dataProvider.refreshAll();
                });
        newCategoryButton.setDisableOnClick(true);

        h2 = new H2(getTranslation("admin"));
        h4 = new H4(getTranslation("edit-categories"));

        add(h2, h4, newCategoryButton, categoriesListing);
    }

    private Component createCategoryEditor(Category category) {
        TextField nameField = new TextField();
        if (category.getId() < 0) {
            nameField.focus();
        }

        Button deleteButton = new Button(VaadinIcon.MINUS_CIRCLE_O.create(),
                event -> {
                    dataService.deleteCategory(category.getId());
                    dataProvider.getItems().remove(category);
                    dataProvider.refreshAll();
                    Notification.show(getTranslation("category-deleted"));
                });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        BeanValidationBinder<Category> binder = new BeanValidationBinder<>(
                Category.class);
        binder.forField(nameField).bind("name");
        binder.setBean(category);
        binder.addValueChangeListener(event -> {
            if (binder.isValid()) {
                dataService.updateCategory(category);
                deleteButton.setEnabled(true);
                newCategoryButton.setEnabled(true);
                Notification.show(getTranslation("category-saved"));
            }
        });
        deleteButton.setEnabled(category.getId() > 0);

        HorizontalLayout layout = new HorizontalLayout(nameField, deleteButton);
        layout.setFlexGrow(1);
        return layout;
    }

    @Override
    public String getPageTitle() {
        return getTranslation(VIEW_NAME);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        h2.setText(getTranslation("admin"));
        h4.setText(getTranslation("edit-categories"));
        newCategoryButton.setText(getTranslation("add-new-category"));
    }
}
