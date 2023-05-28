package com.vaadin.samples.crud;

import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.samples.backend.data.Product;

public interface SampleCrudView {

    public void setNewProductEnabled(boolean enabled);

    public void clearSelection();

    public void selectRow(Product row);

    public void showForm(boolean show);

    public void updateProduct(Product product);

    public void showSaveNotification(String msg);

    public void editProduct(Product product);

    public void removeProduct(Product product);
}
