package com.vaadin.samples.crud;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToBigDecimalConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.samples.backend.data.Availability;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

/**
 * A form for editing a single product.
 */
public class ProductForm extends Dialog {

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
    private CheckboxGroup<Category> category;
    private Button save;
    private Button discard;
    private Button cancel;
    private Button delete;

    private SampleCrudPresenter presenter;
    private Binder<Product> binder;
    private Product currentProduct;

    private boolean hasChanges;

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
        horizontalLayout.setWidth("100%");
        horizontalLayout.setFlexGrow(1, price, stockCount);
        content.add(horizontalLayout);

        availability = new Select<>();
        availability.setLabel(getTranslation(AVAILABILITY));
        availability.setWidth("100%");
        availability.setItems(Availability.values());
        availability.setRenderer(new ComponentRenderer<Span, Availability>(item -> {
            Icon icon = VaadinIcon.CIRCLE.create();
            icon.addClassNames(item.toString(), LumoUtility.Margin.Right.SMALL);
            Span span = new Span();
            span.add(icon, new Text(item.toString()));
            return span;
        }));
        content.add(availability);

        category = new CheckboxGroup<>();
        category.setLabel(getTranslation(CATEGORIES));
        category.addClassName("scroll");
        category.setWidth("100%");
        category.setId("category");
        category.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        content.add(category);

        binder = new BeanValidationBinder<>(Product.class);
        binder.forField(price)
                .withConverter(
                        new PriceConverter(getTranslation(CANNOT_CONVERT)))
                .bind(PRICE);
        binder.forField(stockCount).withConverter(
                new StockCountConverter(getTranslation(CANNOT_CONVERT)))
                .bind("stockCount");
        binder.bindInstanceFields(this);

        binder.getFields().forEach(field -> addDirtyCheck(field));

        // enable/disable save button while editing
        binder.addStatusChangeListener(event -> {
            boolean isValid = !event.hasValidationErrors();
            hasChanges = binder.hasChanges();
            if (!hasChanges) {
                binder.getFields().forEach(
                        field -> ((Component) field).removeClassName("dirty"));
            }
            save.setEnabled(hasChanges && isValid);
            discard.setEnabled(hasChanges);
        });

        save = new Button(getTranslation(SAVE));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            if (currentProduct != null
                    && binder.writeBeanIfValid(currentProduct)) {
                presenter.saveProduct(currentProduct);
            }
        });
        save.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        discard = new Button(getTranslation(DISCARD));
        discard.addThemeName("warning");
        discard.addClickListener(event -> {
            hasChanges = false;
            presenter.editProduct(currentProduct);
        });

        cancel = new Button(getTranslation(CANCEL));
        cancel.addClickListener(event -> presenter.cancelProduct());
        cancel.addClickShortcut(Key.ESCAPE);
        getElement()
                .addEventListener("keydown", event -> presenter.cancelProduct())
                .setFilter("event.key == 'Escape'");

        delete = new Button(getTranslation("delete"));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_PRIMARY);
        delete.addClickListener(event -> {
            if (currentProduct != null) {
                presenter.deleteProduct(currentProduct);
            }
        });

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setPadding(false);
        buttons.setWidth("100%");
        buttons.add(delete, discard, cancel, save);
        buttons.addClassName(LumoUtility.JustifyContent.BETWEEN);
        content.add(buttons);

        addDialogCloseActionListener(e -> {
            if (binder.hasChanges()) {
                confirmDiscard(() -> close());
            } else {
                close();
            }
        });
    }

    public void setCategories(Collection<Category> categories) {
        category.setItems(categories);
    }

    public void editProduct(Product product) {
        if (product == null) {
            product = new Product();
        }
        delete.setVisible(!product.isNewProduct());
        currentProduct = product;
        binder.readBean(product);
    }

    public void confirmDiscard(SerializableRunnable action) {
        ConfirmDialog confirm = new ConfirmDialog();
        confirm.setConfirmText(getTranslation(DISCARD));
        confirm.setCancelText(getTranslation(CANCEL));
        confirm.setHeader(getTranslation(DISCARD_CHANGES));
        confirm.setText(getTranslation(UNSAVED_CHANGES));
        confirm.setCancelable(true);
        confirm.setConfirmButtonTheme("warning");
        confirm.addConfirmListener(e -> {
            hasChanges = false;
            action.run();
        });
        confirm.open();
    }

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
}
