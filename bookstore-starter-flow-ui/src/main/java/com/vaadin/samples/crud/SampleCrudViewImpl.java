package com.vaadin.samples.crud;

import java.util.Collection;
import java.util.stream.Collectors;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.cdi.annotation.RouteScoped;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.samples.MainLayout;
import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

import jakarta.inject.Inject;

/**
 * A view for performing create-read-update-delete operations on products.
 *
 * See also {@link SampleCrudPresenter} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SuppressWarnings("serial")
@Route(value = "inventory", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RouteScoped
@CdiComponent
public class SampleCrudViewImpl extends HorizontalLayout
        implements HasUrlParameter<String>, SampleCrudView, BeforeLeaveObserver,
        AfterNavigationObserver, HasDynamicTitle, LocaleChangeObserver {

    private static final String UPDATED = "updated";
    private static final String CREATED = "created";
    private static final String REMOVED = "removed";
    private static final String WILL_DELETE = "will-delete";
    private static final String CONFIRM = "confirm";
    private static final String DELETE = "delete";
    private static final String NEW_PRODUCT = "new-product";
    private static final String FILTER = "filter";
    public static final String VIEW_NAME = "inventory";
    private ProductGrid grid;
    private ProductForm form;
    private TextField filter;

    private SampleCrudPresenter presenter;

    private Button newProduct;

    private ProductDataProvider dataProvider;

    @Inject
    public SampleCrudViewImpl(ProductDataProvider dataProvider,
            SampleCrudPresenter presenter) {
        this.dataProvider = dataProvider;
        this.presenter = presenter;
        presenter.setView(this);
        setSizeFull();
        HorizontalLayout topLayout = createTopBar();

        grid = new ProductGrid();
        grid.setDataProvider(dataProvider);
        grid.asSingleSelect().addValueChangeListener(
                event -> presenter.rowSelected(event.getValue()));

        form = new ProductForm(presenter);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        presenter.init();
    }

    public HorizontalLayout createTopBar() {
        filter = new TextField();
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());
        filter.setPlaceholder(getTranslation(FILTER));
        // Apply the filter to grid's data provider. TextField value is never
        // null
        filter.addValueChangeListener(
                event -> dataProvider.setFilter(event.getValue()));
        filter.addFocusShortcut(Key.KEY_F, KeyModifier.CONTROL);

        newProduct = new Button(getTranslation(NEW_PRODUCT));
        newProduct.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newProduct.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        newProduct.addClickListener(click -> presenter.newProduct());
        // CTRL+N will create a new window which is unavoidable
        newProduct.addClickShortcut(Key.KEY_N, KeyModifier.ALT);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(filter);
        topLayout.add(newProduct);
        topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        topLayout.expand(filter);
        return topLayout;
    }

    @Override
    public void showSaveNotification(String book) {
        Notification.show(getTranslation(CREATED, book));
    }

    @Override
    public void showUpdateNotification(String book) {
        Notification.show(getTranslation(UPDATED, book));
    }

    private void showNotification(String notification) {
        Notification.show(notification);
    }

    @Override
    public void setNewProductEnabled(boolean enabled) {
        newProduct.setEnabled(enabled);
    }

    @Override
    public void clearSelection() {
        setFragmentParameter("");
        form.clearProduct();
        grid.getSelectionModel().deselectAll();
    }

    @Override
    public void selectRow(Product row) {
        grid.getSelectionModel().select(row);
    }

    @Override
    public void updateProduct(Product product) {
        dataProvider.save(product);
        grid.scrollToIndex(grid.getListDataView().getItems()
                .collect(Collectors.toList()).indexOf(product));
    }

    @Override
    public void removeProduct(Product product) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setConfirmText(getTranslation(DELETE));
        confirm.setHeader(getTranslation(CONFIRM));
        confirm.setText(getTranslation(WILL_DELETE, product.getProductName()));
        confirm.setCancelable(true);
        confirm.setConfirmButtonTheme("warning");
        confirm.addConfirmListener(e -> {
            form.clearProduct();
            dataProvider.delete(product);
            showNotification(getTranslation(REMOVED, product.getProductName()));
        });
        confirm.addCancelListener(e -> {
            editProduct(product);
        });
        confirm.open();
    }

    @Override
    public void editProduct(Product product) {
        showForm(product != null);
        if (product == null) {
            setFragmentParameter("");
        } else if (product.isNewProduct()) {
            setFragmentParameter("new");
        } else if (!product.equals(getCurrentProduct())) {
            setFragmentParameter(product.getId() + "");
        }
        form.editProduct(product);
    }

    @Override
    public void showForm(boolean show) {
        form.setOpened(show);
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String productId) {
        String fragmentParameter;
        if (productId == null || productId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = productId;
        }

        getUI().ifPresent(
                ui -> ui.navigate(SampleCrudViewImpl.class, fragmentParameter));
    }

    @Override
    public void setParameter(BeforeEvent event,
            @OptionalParameter String parameter) {
        presenter.enter(parameter);
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (form.getCurrentProduct() != null && form.hasChanges()) {
            ContinueNavigationAction action = event.postpone();
            form.confirmDiscard(() -> action.proceed());
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation(VIEW_NAME);
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        newProduct.setText(getTranslation(NEW_PRODUCT));
        filter.setPlaceholder(getTranslation(FILTER));
    }

    @Override
    public void setCategories(Collection<Category> categories) {
        form.setCategories(categories);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // It is good practice to defer loading of the data until navigation is
        // complete hence performing it in after navigation
        dataProvider.loadData();
        presenter.requestCategories();
    }

    @Override
    public Product getCurrentProduct() {
        return form.getCurrentProduct();
    }
}
