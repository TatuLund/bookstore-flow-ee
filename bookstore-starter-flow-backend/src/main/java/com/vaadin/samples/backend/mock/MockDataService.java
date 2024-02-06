package com.vaadin.samples.backend.mock;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Mock data model. This implementation has very simplistic locking and does not
 * notify users of modifications.
 */
@SuppressWarnings("serial")
@ApplicationScoped
public class MockDataService implements DataService {

    private List<Product> products;
    private List<Category> categories;
    private int nextProductId = 0;
    private int nextCategoryId = 0;
    private Random random = new Random();

    @Inject
    Logger logger;

    @Inject
    public MockDataService(MockDataGenerator mockDataGenerator) {
        categories = mockDataGenerator.createCategories();
        products = mockDataGenerator.createProducts(categories);
        nextProductId = products.size() + 1;
        nextCategoryId = categories.size() + 1;
    }

    @Override
    public synchronized List<Product> getAllProducts() {
        randomWait(12);
        return products.stream().map(p -> new Product(p))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<Category> getAllCategories() {
        randomWait(2);
        return categories;
    }

    @Override
    public synchronized Product updateProduct(Product product) {
        randomWait(1);
        var p = new Product(product);
        if (p.getId() < 0) {
            // New product
            p.setId(nextProductId++);
            products.add(p);
            logger.info("Saved a new product ({}) {}", p.getId(),
                    p.getProductName());
            return p;
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == p.getId()) {
                products.set(i, p);
                logger.info("Updated the product ({}) {}", p.getId(),
                        p.getProductName());
                return p;
            }
        }

        throw new IllegalArgumentException(
                "No product with id " + p.getId() + " found");
    }

    @Override
    public synchronized Product getProductById(int productId) {
        randomWait(1);
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == productId) {
                return new Product(products.get(i));
            }
        }
        return null;
    }

    @Override
    public void updateCategory(Category category) {
        randomWait(1);
        if (category.getId() < 0) {
            category.setId(nextCategoryId++);
            categories.add(category);
        }
    }

    @Override
    public void deleteCategory(int categoryId) {
        randomWait(1);
        if (categories.removeIf(category -> category.getId() == categoryId)) {
            getAllProducts().forEach(product -> {
                product.getCategory()
                        .removeIf(category -> category.getId() == categoryId);
            });
        }
    }

    @Override
    public synchronized void deleteProduct(int productId) {
        randomWait(1);
        Product p = getProductById(productId);
        if (p == null) {
            throw new IllegalArgumentException(
                    "Product with id " + productId + " not found");
        }
        products.remove(p);
        logger.info("Removed the product ({}) {}", p.getId(),
                p.getProductName());
    }

    private void randomWait(int count) {
        int wait = 50 + random.nextInt(150);
        try {
            Thread.sleep(wait * count);
        } catch (InterruptedException e) {
        }
    }
 }
