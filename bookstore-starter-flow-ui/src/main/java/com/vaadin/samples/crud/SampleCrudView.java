package com.vaadin.samples.crud;

import java.util.Collection;

import com.vaadin.samples.backend.data.Category;
import com.vaadin.samples.backend.data.Product;

public interface SampleCrudView {

    public void setNewProductEnabled(boolean enabled);

    public void clearSelection();

    public void selectRow(Product row);

    public void showForm(boolean show);

    public void updateProduct(Product product);

    public void showSaveNotification(String book);

    public void showUpdateNotification(String book);

    public void editProduct(Product product);

    public void removeProduct(Product product);
    
    public void setCategories(Collection<Category> collection);

    public Product getCurrentProduct();

    public void showNotValidProductIdNotification(String productId);
}
