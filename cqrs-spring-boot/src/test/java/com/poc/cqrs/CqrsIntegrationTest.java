package com.poc.cqrs;

import com.poc.cqrs.command.service.ProductCommandService;
import com.poc.cqrs.query.model.ProductView;
import com.poc.cqrs.query.service.ProductQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CqrsIntegrationTest {

    @Autowired
    private ProductCommandService commandService;

    @Autowired
    private ProductQueryService queryService;

    @Test
    void createCommand_shouldProjectToReadModel() {
        var id = commandService.createProduct("Laptop", "Gaming laptop", new BigDecimal("999.99"), 10);

        ProductView view = queryService.getById(id);

        assertEquals("Laptop", view.getName());
        assertEquals(new BigDecimal("999.99"), view.getPrice());
        assertTrue(view.isAvailable());
        assertEquals("PREMIUM", view.getPriceRange());
    }

    @Test
    void updateCommand_shouldUpdateReadModel() {
        var id = commandService.createProduct("Mouse", "Wireless mouse", new BigDecimal("29.99"), 5);

        commandService.updateProduct(id, "Mouse Pro", "Ergonomic wireless mouse", new BigDecimal("79.99"), 0);

        ProductView view = queryService.getById(id);
        assertEquals("Mouse Pro", view.getName());
        assertEquals("MID_RANGE", view.getPriceRange());
        assertFalse(view.isAvailable());
    }

    @Test
    void deleteCommand_shouldRemoveFromReadModel() {
        var id = commandService.createProduct("Keyboard", "Mechanical", new BigDecimal("149.99"), 3);
        assertNotNull(queryService.getById(id));

        commandService.deleteProduct(id);

        assertThrows(IllegalArgumentException.class, () -> queryService.getById(id));
    }

    @Test
    void queryByPriceRange_shouldReturnFilteredResults() {
        commandService.createProduct("Budget Item", "Cheap", new BigDecimal("10.00"), 100);
        commandService.createProduct("Premium Item", "Expensive", new BigDecimal("500.00"), 2);

        var budget = queryService.getByPriceRange("BUDGET");
        var premium = queryService.getByPriceRange("PREMIUM");

        assertTrue(budget.stream().anyMatch(p -> p.getName().equals("Budget Item")));
        assertTrue(premium.stream().anyMatch(p -> p.getName().equals("Premium Item")));
    }

    @Test
    void search_shouldFindByPartialName() {
        commandService.createProduct("Ergonomic Chair", "Office chair", new BigDecimal("350.00"), 7);

        var results = queryService.search("ergonomic");

        assertFalse(results.isEmpty());
        assertEquals("Ergonomic Chair", results.get(0).getName());
    }
}
