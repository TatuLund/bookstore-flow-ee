package com.vaadin.samples.backend.mock;

import java.util.Collections;
import java.util.List;

import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Mock data model. This implementation has very simplistic locking and does not
 * notify users of modifications.
 */
@ApplicationScoped
public class MockDataService implements DataService {

    private List<Product> products;
    private List<Category> categories;
    private int nextProductId = 0;
    private int nextCategoryId = 0;

    @Inject
    public MockDataService(MockDataGenerator mockDataGenerator) {
        categories = mockDataGenerator.createCategories();
        products = mockDataGenerator.createProducts(categories);
        nextProductId = products.size() + 1;
        nextCategoryId = categories.size() + 1;
    }

    @Override
    public synchronized List<Product> getAllProducts() {
        sleep(5);
        return Collections.unmodifiableList(products);
    }

    @Override
    public synchronized List<Category> getAllCategories() {
        sleep(3);
        return Collections.unmodifiableList(categories);
    }

    @Override
    public synchronized void updateProduct(Product p) {
        sleep(2);
        if (p.getId() < 0) {
            // New product
            p.setId(nextProductId++);
            products.add(p);
            return;
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == p.getId()) {
                products.set(i, p);
                return;
            }
        }

        throw new IllegalArgumentException(
                "No product with id " + p.getId() + " found");
    }

    @Override
    public synchronized Product getProductById(int productId) {
        sleep(1);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == productId) {
                return products.get(i);
            }
        }
        return null;
    }

    @Override
    public void updateCategory(Category category) {
        sleep(2);
        if (category.getId() < 0) {
            category.setId(nextCategoryId++);
            categories.add(category);
        }
    }

    @Override
    public void deleteCategory(int categoryId) {
        sleep(2);
        if (categories.removeIf(category -> category.getId() == categoryId)) {
            getAllProducts().forEach(product -> {
                product.getCategory()
                        .removeIf(category -> category.getId() == categoryId);
            });
        }
    }

    @Override
    public synchronized void deleteProduct(int productId) {
        sleep(2);
        Product p = getProductById(productId);
        if (p == null) {
            throw new IllegalArgumentException(
                    "Product with id " + productId + " not found");
        }
        products.remove(p);
    }

    private void sleep(int x) {
        try {
            Thread.sleep(x * 100);
        } catch (InterruptedException e) {
        }
    }
}
