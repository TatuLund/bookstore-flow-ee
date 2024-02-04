package com.vaadin.samples.backend.mock;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.samples.backend.data.Product;
import com.vaadin.samples.backend.mock.MockDataGenerator;
import com.vaadin.samples.backend.mock.MockDataService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;

/**
 * Simple unit test for the back-end data service.
 */
public class DataServiceTest {

    private MockDataService service;

    @Before
    public void setUp() throws Exception {
    	service = new MockDataService(new MockDataGenerator());
    	service.logger = LoggerFactory.getLogger("mock");
    }

    @Test
    public void canFetchProducts() throws Exception {
        assertFalse(service.getAllProducts().isEmpty());
    }

    @Test
    public void canFetchCategories() throws Exception {
        assertFalse(service.getAllCategories().isEmpty());
    }

    @Test
    public void updateTheProduct() throws Exception {
        var oldSize = service.getAllProducts().size();
        var p = service.getAllProducts().iterator().next();
        p.setProductName("My Test Name");
        service.updateProduct(p);
        var p2 = service.getProductById(p.getId());
        assertEquals("My Test Name", p2.getProductName());
        assertEquals(oldSize, service.getAllProducts().size());
    }

    @Test
    public void addNewProduct() throws Exception {
        var oldSize = service.getAllProducts().size();
        Product p = new Product();
        p.setProductName("A new book");
        p.setPrice(new BigDecimal(10));
        assertEquals(-1, p.getId());
        var newProduct = service.updateProduct(p);
        assertNotEquals(-1, newProduct.getId());
        assertEquals(oldSize + 1, service.getAllProducts().size());
        
        var foundProduct = service.getProductById(newProduct.getId());
        assertTrue(foundProduct.equals(newProduct));
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateNonExistentProduct() {
        Product p = new Product();
        p.setProductName("A new book");
        p.setPrice(new BigDecimal(10));
        p.setId(1000);
        service.updateProduct(p);
    }

    @Test
    public void removeProduct() throws Exception {
        var oldSize = service.getAllProducts().size();
        var p = service.getAllProducts().iterator().next();
        var pid = p.getId();
        service.deleteProduct(pid);
        assertEquals(null, service.getProductById(pid));
        assertEquals(oldSize - 1, service.getAllProducts().size());
    }

    @Test
    public void findProductById() {
        assertNotEquals(null, service.getProductById(1));
    }

    @Test
    public void findProductByNonExistentId() {
        assertEquals(null, service.getProductById(1000));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeProductByNonExistentId() {
        service.deleteProduct(1000);
    }

}
