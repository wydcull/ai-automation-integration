package org.wydcull.ai_chatbot_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wydcull.ai_chatbot_project.model.Product;
import org.wydcull.ai_chatbot_project.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductSearchService productSearchService;

    private Product laptop;
    private Product headphones;

    @BeforeEach
    void setUp() {
        laptop = new Product();
        laptop.setId(1L);
        laptop.setName("Gaming Laptop");
        laptop.setDescription("Powerful notebook");
        laptop.setPrice(new BigDecimal("999.99"));
        laptop.setCategory("Electronics");
        laptop.setStockQuantity(5);
        laptop.setSku("LAP-001");
        laptop.setInStock(true);

        headphones = new Product();
        headphones.setId(2L);
        headphones.setName("Wireless Headphones");
        headphones.setDescription("Noise cancelling");
        headphones.setPrice(new BigDecimal("199.99"));
        headphones.setCategory("Audio");
        headphones.setStockQuantity(10);
        headphones.setSku("HP-001");
        headphones.setInStock(true);
    }

    @Test
    void smartSearch_emptyQuery_returnsEmptyList() {
        assertThat(productSearchService.smartSearch("")).isEmpty();
        assertThat(productSearchService.smartSearch(null)).isEmpty();
        assertThat(productSearchService.smartSearch("   ")).isEmpty();
    }

    @Test
    void smartSearch_directMatch_returnsProducts() {
        when(productRepository.searchProducts("laptop")).thenReturn(List.of(laptop));

        List<Product> results = productSearchService.smartSearch("laptop");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Gaming Laptop");
    }

    @Test
    void smartSearch_synonymNotebook_searchesLaptop() {
        when(productRepository.searchProducts(anyString())).thenReturn(List.of());
        when(productRepository.searchProducts("laptop")).thenReturn(List.of(laptop));
        when(productRepository.searchProducts("notebook")).thenReturn(List.of(laptop));

        List<Product> results = productSearchService.smartSearch("I need a notebook");

        assertThat(results).isNotEmpty();
        assertThat(results).extracting(Product::getName).contains("Gaming Laptop");
    }

    @Test
    void extractProductKeywords_findsKnownTerms() {
        List<String> keywords = productSearchService.extractProductKeywords(
                "Looking for a phone and headphones");

        assertThat(keywords).contains("phone", "headphones");
    }

    @Test
    void extractProductKeywords_noMatch_returnsEmpty() {
        assertThat(productSearchService.extractProductKeywords("return policy")).isEmpty();
    }
}
