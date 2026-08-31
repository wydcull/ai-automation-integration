package com.rag.AIrag.dto;


import lombok.Data;
import java.util.List;
@Data
public class EmbeddingResponse {
    private List<Item> data;
    @Data
    public static class Item {
        private List<Double> embedding;
    }
    public float[] getFirstEmbedding() {
        List<Double> embeddings = data.get(0).getEmbedding();
        float[] result = new float[embeddings.size()];
        for (int i = 0; i < embeddings.size(); i++) {
            result[i] = embeddings.get(i).floatValue();
        }
        return result;
    }
}