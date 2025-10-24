package com.shubham.confidentialai;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;

@Configuration
public class VectorStoreConfig {

    private static final String OLLAMA_BASE_URL = "http://localhost:11434";


    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Creates a simple, local JSON-based vector store
        return SimpleVectorStore.builder(embeddingModel)
                .build();
    }

//    @Bean
//    public EmbeddingModel embeddingModel() {
//        return new OllamaEmbeddingModel(new Ollam);
//    }


}
