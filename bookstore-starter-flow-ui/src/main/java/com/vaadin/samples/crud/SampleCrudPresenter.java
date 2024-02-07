package com.vaadin.samples.crud;

import com.vaadin.samples.authentication.AccessControl;
import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Product;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import java.io.Serializable;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
@SuppressWarnings("serial")
@Dependent
public class SampleCrudPresenter implements Serializable {

    private SampleCrudView view;

    private DataService dataService;
    private AccessControl accessControl;

    @Inject
    public SampleCrudPresenter(DataService dataService,
            AccessControl accessControl) {
        this.dataService = dataService;
        this.accessControl = accessControl;
    }

    public void setView(SampleCrudView simpleCrudView) {
        view = simpleCrudView;
    }

    public void init() {
        editProduct(null);
        // Hide and disable if not admin
        if (!accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            view.setNewProductEnabled(false);
        }
    }

    public void cancelProduct() {
        view.clearSelection();
    }

    public void requestCategories() {
        view.setCategories(dataService.getAllCategories());
    }

    public void enter(String productId) {
        if (productId != null && !productId.isEmpty()) {
            if (productId.equals("new")) {
                newProduct();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(productId);
                    Product product = findProduct(pid);
                    if (product != null) {
                        view.selectRow(product);
                    } else {
                        view.showNotValidProductIdNotification(productId);                        
                    }
                } catch (NumberFormatException e) {
                    view.showNotValidProductIdNotification(productId);
                }
            }
        } else {
            view.showForm(false);
        }
    }

    private Product findProduct(int productId) {
        return dataService.getProductById(productId);
    }

    public void saveProduct(Product product) {
        boolean newProduct = product.isNewProduct();
        view.updateProduct(product);
        if (newProduct) {
            view.showSaveNotification(product.getProductName());
        } else {
            view.showUpdateNotification(product.getProductName());
        }
        view.clearSelection();
    }

    public void deleteProduct(Product product) {
        view.removeProduct(product);
        view.clearSelection();
    }

    public void editProduct(Product product) {
        view.editProduct(product);
    }

    public void newProduct() {
        view.clearSelection();
        view.editProduct(new Product());
    }

    public void rowSelected(Product product) {
        if (accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editProduct(product);
        }
    }
}
