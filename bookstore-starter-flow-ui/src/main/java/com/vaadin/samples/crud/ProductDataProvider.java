package com.vaadin.samples.crud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;

import com.vaadin.cdi.annotation.CdiComponent;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Product;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

@SuppressWarnings("serial")
@Dependent
@CdiComponent
public class ProductDataProvider extends ListDataProvider<Product> {

    private DataService dataService;

    /** Text filter that can be changed separately. */
    private String filterText = "";

    private Collection<Product> products;
    private long timestamp = 0;

    @Inject
    public ProductDataProvider(DataService dataService) {
        super(new ArrayList<>());
        this.dataService = dataService;
    }

    public void loadData() {
        // Cache time 15mins
        long age = (System.currentTimeMillis() - timestamp) / 1000 / 60;
        if (timestamp == 0 || age > 15) {
            products = dataService.getAllProducts();
        }
        timestamp = System.currentTimeMillis();
        getItems().clear();
        getItems().addAll(products);
        refreshAll();
    }

    /**
     * Store given product to the backing data service.
     * 
     * @param product
     *            the updated or new product
     */
    public void save(Product product) {
        boolean isNewProduct = product.isNewProduct();

        Product newProduct = dataService.updateProduct(product);
        if (isNewProduct) {
            getItems().add(newProduct);
            refreshAll();
        } else {
            refreshItem(product);
        }
    }

    /**
     * Delete given product from the backing data service.
     * 
     * @param product
     *            the product to be deleted
     */
    public void delete(Product product) {
        getItems().remove(product);
        dataService.deleteProduct(product.getId());
        refreshAll();
    }

    /**
     * Sets the filter to use for this data provider and refreshes data.
     * <p>
     * Filter is compared for product name, availability and category.
     * 
     * @param filterText
     *            the text to filter by, never null
     */
    public void setFilter(String filterText) {
        Objects.requireNonNull(filterText, "Filter text cannot be null.");
        if (Objects.equals(this.filterText, filterText.trim())) {
            return;
        }
        this.filterText = filterText.trim();

        setFilter(product -> passesFilter(product.getProductName(), filterText)
                || passesFilter(product.getAvailability(), filterText)
                || passesFilter(product.getCategory(), filterText));
    }

    @Override
    public Integer getId(Product product) {
        Objects.requireNonNull(product,
                "Cannot provide an id for a null product.");

        return product.getId();
    }

    private boolean passesFilter(Object object, String filterText) {
        return object != null && object.toString().toLowerCase(Locale.ENGLISH)
                .contains(filterText);
    }
}
