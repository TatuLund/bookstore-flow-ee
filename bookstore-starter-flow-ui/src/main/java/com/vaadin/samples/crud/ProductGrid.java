package com.vaadin.samples.crud;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Grid of products, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
@Uses(Icon.class)
public class ProductGrid extends Grid<Product> implements LocaleChangeObserver {

    private static final String CATEGORIES = "categories";
    private static final String IN_STOCK = "in-stock";
    private static final String AVAILABILITY = "availability";
    private static final String PRICE = "price";
    private static final String PRODUCT_NAME = "product-name";

    public ProductGrid() {
        setSizeFull();

        addColumn(Product::getProductName)
                .setHeader(getTranslation(PRODUCT_NAME)).setFlexGrow(20)
                .setKey(PRODUCT_NAME).setSortable(true)
                .setTooltipGenerator(item -> item.getProductName());

        // Format and add " €" to price
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        // To change the text alignment of the column, a template is used.
        final String priceTemplate = "<div style='text-align: right'>${item.price}</div>";
        addColumn(LitRenderer.<Product> of(priceTemplate).withProperty(PRICE,
                product -> decimalFormat.format(product.getPrice()) + " €"))
                        .setHeader(getTranslation(PRICE)).setKey(PRICE)
                        .setComparator(Comparator.comparing(Product::getPrice))
                        .setFlexGrow(3);

        // Add an traffic light icon in front of availability
        // Three css classes with the same names of three availability values,
        // Available, Coming and Discontinued, are defined in shared-styles.css
        // and are
        // used here in availabilityTemplate.
        final String availabilityTemplate = """
                <vaadin-icon icon="vaadin:circle" class="${item.class} mr-s"></vaadin-icon>
                <span class="availability-label">${item.availability}</span>
                """;
        var availability = addColumn(LitRenderer
                .<Product> of(availabilityTemplate)
                .withProperty("class",
                        product -> product.getAvailability().toString())
                .withProperty("availability", product -> getTranslation(
                        product.getAvailability().toString().toLowerCase())));
        availability.setHeader(getTranslation(AVAILABILITY))
                .setKey(AVAILABILITY)
                .setComparator(Comparator.comparing(Product::getAvailability))
                .setFlexGrow(2).setTooltipGenerator(item -> getTranslation(
                        item.getAvailability().toString().toLowerCase()));

        addColumn(product -> product.getStockCount() == 0 ? "-"
                : Integer.toString(product.getStockCount()))
                        .setHeader(getTranslation(IN_STOCK))
                        .setComparator(
                                Comparator.comparingInt(Product::getStockCount))
                        .setFlexGrow(2).setTextAlign(ColumnTextAlign.END);

        // Show all categories the product is in, separated by commas
        addColumn(this::formatCategories).setHeader(getTranslation(CATEGORIES))
                .setKey(CATEGORIES).setFlexGrow(12)
                .setTooltipGenerator(this::formatCategories);
    }

    public Product getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Product product) {
        getDataCommunicator().refresh(product);
    }

    private String formatCategories(Product product) {
        if (product.getCategory() == null || product.getCategory().isEmpty()) {
            return "";
        }
        return product.getCategory().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(Category::getName).collect(Collectors.joining(", "));
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        getColumns().forEach(
                column -> column.setHeader(getTranslation(column.getKey())));
    }
}
