package com.vaadin.samples.crud;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox.AutoExpandMode;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.StatusChangeEvent;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.samples.backend.data.Availability;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

/**
 * A form for editing a single product.
 */
@SuppressWarnings("serial")
public class ProductForm extends Dialog {

    // Localization constants
    private static final String AVAILABILITY_MISMATCH = "availability-mismatch";
    private static final String UNSAVED_CHANGES = "unsaved-changes";
    private static final String DISCARD_CHANGES = "discard-changes";
    private static final String SAVE = "save";
    private static final String CANCEL = "cancel";
    private static final String CANNOT_CONVERT = "cannot-convert";
    private static final String CATEGORIES = "categories";
    private static final String DISCARD = "discard";
    private static final String AVAILABILITY = "availability";
    private static final String IN_STOCK = "in-stock";
    private static final String PRICE = "price";
    private static final String PRODUCT_NAME = "product-name";

    private VerticalLayout content;

    private TextField productName;
    private TextField price;
    private TextField stockCount;
    private Select<Availability> availability;
    private MultiSelectComboBox<Category> category;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private SampleCrudPresenter presenter;
    private Binder<Product> binder;
    private Product currentProduct;

    private boolean hasChanges;

    private ComponentRenderer<Span, Availability> availabilityRenderer = new ComponentRenderer<Span, Availability>(
            item -> {
                Icon icon = VaadinIcon.CIRCLE.create();
                icon.addClassNames(item.toString(),
                        LumoUtility.Margin.Right.SMALL);
                Span span = new Span();
                span.add(icon, new Text(
                        getTranslation(item.toString().toLowerCase())));
                return span;
            });

    private static class PriceConverter extends StringToBigDecimalConverter {

        public PriceConverter(String errorMessage) {
            super(BigDecimal.ZERO, errorMessage);
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // Always display currency with two decimals
            NumberFormat format = super.getFormat(locale);
            if (format instanceof DecimalFormat) {
                format.setMaximumFractionDigits(2);
                format.setMinimumFractionDigits(2);
            }
            return format;
        }
    }

    private static class StockCountConverter extends StringToIntegerConverter {

        public StockCountConverter(String errorMessage) {
            super(0, errorMessage);
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            // Do not use a thousands separator, as HTML5 input type
            // number expects a fixed wire/DOM number format regardless
            // of how the browser presents it to the user (which could
            // depend on the browser locale).
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            format.setDecimalSeparatorAlwaysShown(false);
            format.setParseIntegerOnly(true);
            format.setGroupingUsed(false);
            return format;
        }
    }

    public ProductForm(SampleCrudPresenter sampleCrudLogic) {
        setClassName("product-form");

        content = new VerticalLayout();
        content.setSizeUndefined();
        content.setHeightFull();
        add(content);

        presenter = sampleCrudLogic;

        productName = new TextField(getTranslation(PRODUCT_NAME));
        productName.setWidth("100%");
        productName.setRequired(true);
        productName.setValueChangeMode(ValueChangeMode.EAGER);
        content.add(productName);

        price = new TextField(getTranslation(PRICE));
        price.setSuffixComponent(new Span("€"));
        price.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        price.setValueChangeMode(ValueChangeMode.EAGER);

        stockCount = new TextField(getTranslation(IN_STOCK));
        stockCount.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
        stockCount.setValueChangeMode(ValueChangeMode.EAGER);

        HorizontalLayout horizontalLayout = new HorizontalLayout(price,
                stockCount);
        horizontalLayout.addClassName(LumoUtility.FlexWrap.WRAP);
        horizontalLayout.setWidth("100%");
        horizontalLayout.setFlexGrow(1, price, stockCount);
        content.add(horizontalLayout);

        availability = new Select<>();
        availability.setLabel(getTranslation(AVAILABILITY));
        availability.setWidth("100%");
        availability.setItems(Availability.values());
        availability.setRenderer(availabilityRenderer);
        content.add(availability);

        category = new MultiSelectComboBox<>();
        category.setLabel(getTranslation(CATEGORIES));
        category.setWidth("100%");
        category.setId("category");
        category.setAutoExpand(AutoExpandMode.VERTICAL);
        content.add(category);

        binder = new BeanValidationBinder<>(Product.class);
        binder.forField(price)
                .withConverter(
                        new PriceConverter(getTranslation(CANNOT_CONVERT)))
                .bind(PRICE);
        binder.forField(stockCount)
                .withConverter(
                        new StockCountConverter(getTranslation(CANNOT_CONVERT)))
                .bind("stockCount");

        // Add bean level validation for Availability vs. Stock count cross
        // checking.
        binder.withValidator(this::checkAvailabilityVsStockCount, "Error");

        binder.bindInstanceFields(this);

        binder.getFields().forEach(field -> addDirtyCheck(field));

        // enable/disable save button while editing
        binder.addStatusChangeListener(this::handleBinderStatusChange);

        save = new Button(getTranslation(SAVE));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SMALL);
        save.addClickListener(event -> saveButtonClicked());
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
        save.setEnabled(false);

        discard = new Button(getTranslation(DISCARD));
        discard.addThemeNames("warning", "small");
        discard.addClickListener(event -> {
            presenter.editProduct(currentProduct);
            hasChanges = false;
        });
        discard.setEnabled(false);

        cancel = new Button(getTranslation(CANCEL));
        cancel.addClickListener(event -> cancelProduct());
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addThemeVariants(ButtonVariant.LUMO_SMALL);
        Shortcuts
                .addShortcutListener(this, event -> cancelProduct(), Key.ESCAPE)
                .listenOn(this);

        delete = new Button(getTranslation("delete"));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_SMALL);
        delete.addClickListener(event -> {
            if (currentProduct != null) {
                presenter.deleteProduct(currentProduct);
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setPadding(false);
        buttons.setWidth("100%");
        buttons.add(delete, discard, cancel, save);
        buttons.addClassNames(LumoUtility.JustifyContent.BETWEEN,
                LumoUtility.Margin.Top.AUTO, LumoUtility.FlexWrap.WRAP);
        buttons.setFlexGrow(1, save);
        content.add(buttons);

        addDialogCloseActionListener(e -> {
            if (binder.hasChanges()) {
                confirmDiscard(() -> close());
            } else {
                close();
            }
        });
    }

    private void saveButtonClicked() {
        if (currentProduct != null && binder.writeBeanIfValid(currentProduct)) {
            presenter.saveProduct(currentProduct);
        } else if (binderHasInvalidFieldsBound()) {
            flagStockCountAndAvailabilityInvalid();

        }
    }

    private void flagStockCountAndAvailabilityInvalid() {
        stockCount.setInvalid(true);
        stockCount.setErrorMessage(getTranslation(AVAILABILITY_MISMATCH));
        availability.setInvalid(true);
        availability.setErrorMessage(getTranslation(AVAILABILITY_MISMATCH));
    }

    private boolean binderHasInvalidFieldsBound() {
        return binder.getFields()
                .filter(field -> ((HasValidation) field).isInvalid())
                .count() == 0;
    }

    private void handleBinderStatusChange(StatusChangeEvent event) {
        boolean isValid = !event.hasValidationErrors();
        hasChanges = binder.hasChanges();
        if (!hasChanges) {
            binder.getFields().forEach(
                    field -> ((Component) field).removeClassName("dirty"));
        }
        save.setEnabled(hasChanges && isValid);
        discard.setEnabled(hasChanges);
    }

    private boolean checkAvailabilityVsStockCount(Product product) {
        return (product.getAvailability() == Availability.AVAILABLE
                && product.getStockCount() > 0)
                || (product.getAvailability() == Availability.DISCONTINUED
                        && product.getStockCount() == 0)
                || (product.getAvailability() == Availability.COMING
                        && product.getStockCount() == 0);
    }

    private void cancelProduct() {
        if (hasChanges) {
            confirmDiscard(() -> {
                currentProduct = null;
                presenter.cancelProduct();
            });
        } else {
            currentProduct = null;
            presenter.cancelProduct();
        }
    }

    /**
     * Set the categories used in categories selector.
     * 
     * @param categories
     *            Collection of categories
     */
    public void setCategories(Collection<Category> categories) {
        category.setItems(categories);
    }

    /**
     * Edit a product. If product is null, then new product will be created.
     * 
     * @param product
     *            Product to edit, can be null.
     */
    public void editProduct(Product product) {
        if (product == null) {
            product = new Product();
        }
        delete.setVisible(!product.isNewProduct());
        currentProduct = product;
        binder.readBean(product);
    }

    /**
     * Display confirm dialog with discard edits message, if confirm clicked
     * confirmAction is run.
     * 
     * @param confirmAction
     *            Runnable
     */
    public void confirmDiscard(SerializableRunnable confirmAction) {
        Objects.requireNonNull(confirmAction);
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setConfirmText(getTranslation(DISCARD));
        confirm.setCancelText(getTranslation(CANCEL));
        confirm.setHeader(getTranslation(DISCARD_CHANGES));
        confirm.setText(getTranslation(UNSAVED_CHANGES));
        confirm.setCancelable(true);
        confirm.setConfirmButtonTheme("warning");
        confirm.addConfirmListener(e -> {
            hasChanges = false;
            confirmAction.run();
        });
        confirm.open();
    }

    /**
     * Check if the form has changes.
     * 
     * @return boolean value
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    private static void addDirtyCheck(HasValue<?, ?> field) {
        field.addValueChangeListener(e -> {
            if (e.isFromClient()) {
                ((Component) field).addClassName("dirty");
            }
        });
    }

    /**
     * Get current product, can be null if cancelled, not loaded.
     * 
     * @return Product or null
     */
    public Product getCurrentProduct() {
        return currentProduct;
    }

    public void clearProduct() {
        currentProduct = null;
    }

}
