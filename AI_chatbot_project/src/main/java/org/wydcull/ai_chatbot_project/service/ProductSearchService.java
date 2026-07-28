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
        log.info("🔍 Starting smart product search for query: '{}'", query);
        long startTime = System.currentTimeMillis();

        if (query == null || query.trim().isEmpty()) {
            log.warn("Empty search query received");
            return Collections.emptyList();
        }

        String searchQuery = query.toLowerCase().trim();
        Set<Product> results = new LinkedHashSet<>();

        // Strategy 1: Direct search
        log.debug("Strategy 1: Direct search for '{}'", searchQuery);
        List<Product> exactMatches = productRepository.searchProducts(searchQuery);
        results.addAll(exactMatches);
        log.debug("  → Found {} exact matches", exactMatches.size());

        // Strategy 2: Synonym search
        log.debug("Strategy 2: Synonym search");
        List<Product> synonymMatches = searchWithSynonyms(searchQuery);
        int synonymCount = synonymMatches.size();
        results.addAll(synonymMatches);
        log.debug("  → Found {} synonym matches", synonymCount);

        // Strategy 3: Word-by-word search
        String[] words = searchQuery.split("\\s+");
        if (words.length > 1) {
            log.debug("Strategy 3: Searching individual words: {}", String.join(", ", words));
            for (String word : words) {
                if (word.length() > 2) {
                    List<Product> wordMatches = productRepository.searchProducts(word);
                    results.addAll(wordMatches);
                }
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("✅ Search completed in {}ms: Found {} unique products for query '{}'",
                duration, results.size(), query);

        // Rank and return top 5
        List<Product> rankedResults = rankResults(new ArrayList<>(results), searchQuery)
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        if (!rankedResults.isEmpty()) {
            log.info("Top result: '{}' (score calculated)", rankedResults.get(0).getName());
        }

        return rankedResults;
    }

    /**
     * Search using synonyms
     */
    private List<Product> searchWithSynonyms(String query) {
        Set<Product> results = new HashSet<>();

        for (Map.Entry<String, List<String>> entry : SYNONYMS.entrySet()) {
            String mainTerm = entry.getKey();
            List<String> synonyms = entry.getValue();

            // Check if query contains main term or any synonym
            if (query.contains(mainTerm)) {
                results.addAll(productRepository.searchProducts(mainTerm));
            }

            for (String synonym : synonyms) {
                if (query.contains(synonym)) {
                    results.addAll(productRepository.searchProducts(mainTerm));
                    results.addAll(productRepository.searchProducts(synonym));
                }
            }
        }

        return new ArrayList<>(results);
    }

    /**
     * Rank results by relevance
     */
    private List<Product> rankResults(List<Product> products, String query) {
        return products.stream()
                .sorted((p1, p2) -> {
                    int score1 = calculateScore(p1, query);
                    int score2 = calculateScore(p2, query);
                    return Integer.compare(score2, score1);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score for ranking
     */
    private int calculateScore(Product p, String query) {
        int score = 0;
        String name = p.getName().toLowerCase();

        if (name.equals(query)) score += 100;
        if (name.startsWith(query)) score += 50;
        if (name.contains(query)) score += 30;
        if (p.getInStock()) score += 5;

        return score;
    }

    /**
     * Extract product keywords from message
     */
    public List<String> extractProductKeywords(String message) {
        List<String> keywords = new ArrayList<>();
        String lower = message.toLowerCase();

        SYNONYMS.keySet().forEach(key -> {
            if (lower.contains(key)) keywords.add(key);
        });

        return keywords;
    }
}