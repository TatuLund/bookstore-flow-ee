package com.vaadin.samples.crud;

import com.vaadin.cdi.annotation.NormalRouteScoped;
import com.vaadin.cdi.annotation.RouteScopeOwner;
import com.vaadin.flow.component.UI;
import com.vaadin.samples.MainLayout;
import com.vaadin.samples.authentication.AccessControl;
//import com.vaadin.samples.authentication.AccessControlFactory;
import com.vaadin.samples.backend.DataService;
import com.vaadin.samples.backend.data.Product;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the product editor form and the data source, including
 * fetching and saving products.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
//@NormalRouteScoped
//@RouteScopeOwner(MainLayout.class)
@Dependent
public class SampleCrudLogic implements Serializable {

    private SampleCrudView view;

    private DataService dataService;    
    private AccessControl accessControl; 
    
    @Inject
    public SampleCrudLogic(DataService dataService, AccessControl accessControl) {
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
        setFragmentParameter("");
        view.clearSelection();
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

        UI.getCurrent().navigate(SampleCrudView.class, fragmentParameter);
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
                    view.selectRow(product);
                } catch (NumberFormatException e) {
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
        view.clearSelection();
        view.updateProduct(product);
        setFragmentParameter("");
        view.showSaveNotification(product.getProductName()
                + (newProduct ? " created" : " updated"));
    }

    public void deleteProduct(Product product) {
        view.clearSelection();
        view.removeProduct(product);
        setFragmentParameter("");
        view.showSaveNotification(product.getProductName() + " removed");
    }

    public void editProduct(Product product) {
        if (product == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(product.getId() + "");
        }
        view.editProduct(product);
    }

    public void newProduct() {
        view.clearSelection();
        setFragmentParameter("new");
        view.editProduct(new Product());
    }

    public void rowSelected(Product product) {
        if (accessControl.isUserInRole(AccessControl.ADMIN_ROLE_NAME)) {
            editProduct(product);
        }
    }
}
