package com.vaadin.samples.backend.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import com.vaadin.samples.backend.data.Product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

/**
 * Simple unit test for the back-end data service.
 */
class DataServiceTest {

    private MockDataService service;

    @BeforeEach
    void setUp() {
        service = new MockDataService(new MockDataGenerator());
        service.logger = LoggerFactory.getLogger("mock");
    }

    @Test
    void canFetchProducts() {
        assertFalse(service.getAllProducts().isEmpty());
    }

    @Test
    void canFetchCategories() {
        assertFalse(service.getAllCategories().isEmpty());
    }

    @Test
    void updateTheProduct() {
        var oldSize = service.getAllProducts().size();
        var p = service.getAllProducts().iterator().next();
        p.setProductName("My Test Name");
        service.updateProduct(p);
        var p2 = service.getProductById(p.getId());
        assertEquals("My Test Name", p2.getProductName());
        assertEquals(oldSize, service.getAllProducts().size());
    }

    @Test
    void addNewProduct() {
        var oldSize = service.getAllProducts().size();
        Product p = new Product();
        p.setProductName("A new book");
        p.setPrice(new BigDecimal(10));
        assertEquals(-1, p.getId());
        var newProduct = service.updateProduct(p);
        assertNotEquals(-1, newProduct.getId());
        assertEquals(oldSize + 1, service.getAllProducts().size());

        var foundProduct = service.getProductById(newProduct.getId());
        assertEquals(foundProduct, newProduct);
    }

    @Test
    void updateNonExistentProduct() {
        Product p = new Product();
        p.setProductName("A new book");
        p.setPrice(new BigDecimal(10));
        p.setId(1000);
        assertThrows(IllegalArgumentException.class,
                () -> service.updateProduct(p));
    }

    @Test
    void removeProduct() {
        var oldSize = service.getAllProducts().size();
        var p = service.getAllProducts().iterator().next();
        var pid = p.getId();
        service.deleteProduct(pid);
        assertEquals(null, service.getProductById(pid));
        assertEquals(oldSize - 1, service.getAllProducts().size());
    }

    @Test
    void findProductById() {
        assertNotEquals(null, service.getProductById(1));
    }

    @Test
    void findProductByNonExistentId() {
        assertEquals(null, service.getProductById(1000));
    }

    @Test
    void removeProductByNonExistentId() {
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteProduct(1000));
    }

}
