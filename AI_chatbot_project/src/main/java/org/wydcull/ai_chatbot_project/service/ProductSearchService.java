package org.wydcull.ai_chatbot_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wydcull.ai_chatbot_project.model.Product;
import org.wydcull.ai_chatbot_project.repository.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

    private final ProductRepository productRepository;

    // Synonym mappings
    private static final Map<String, List<String>> SYNONYMS = Map.of(
            "laptop", Arrays.asList("notebook", "computer"),
            "phone", Arrays.asList("mobile", "smartphone"),
            "headphones", Arrays.asList("earphones", "earbuds"),
            "tv", Arrays.asList("television")
    );

    /**
     * Smart search with synonyms and ranking
     */
    public List<Product> smartSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        Set<Product> results = new LinkedHashSet<>();
        String searchQuery = query.toLowerCase().trim();

        // Direct search
        results.addAll(productRepository.searchProducts(searchQuery));

        // Synonym search
        SYNONYMS.forEach((key, synonyms) -> {
            if (searchQuery.contains(key)) {
                results.addAll(productRepository.searchProducts(key));
            }
            synonyms.forEach(syn -> {
                if (searchQuery.contains(syn)) {
                    results.addAll(productRepository.searchProducts(key));
                }
            });
        });

        // Rank and return top 5
        return rankResults(new ArrayList<>(results), searchQuery)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Product> rankResults(List<Product> products, String query) {
        return products.stream()
                .sorted((p1, p2) -> {
                    int score1 = calculateScore(p1, query);
                    int score2 = calculateScore(p2, query);
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

    private int calculateScore(Product p, String query) {
        int score = 0;
        String name = p.getName().toLowerCase();

        if (name.equals(query)) score += 100;
        if (name.startsWith(query)) score += 50;
        if (name.contains(query)) score += 30;
        if (p.getInStock()) score += 5;

        return score;
    }

    public List<String> extractProductKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        String lower = message.toLowerCase();

        SYNONYMS.keySet().forEach(key -> {
            if (lower.contains(key)) keywords.add(key);
        });

        return keywords;
    }
}