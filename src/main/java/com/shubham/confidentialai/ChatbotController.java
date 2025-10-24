package com.shubham.confidentialai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatbotController {

    private final VectorStore vectorStore;
    private final ChatModel chatModel;

    public ChatbotController(VectorStore vectorStore, ChatModel chatModel) {
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
    }

    @PostMapping("/query")
    public String chat(@RequestBody QuestionRequest question) {
        List<Document> similarDocs = vectorStore.similaritySearch(question.getQuestion());
//        String information = similarDocs.stream()
//                .map(Document::getFormattedContent)
//                .collect(Collectors.joining(System.lineSeparator()));

        String information = similarDocs.stream()
                .map(Document::getFormattedContent)
                .map(text -> text.replaceAll("(?m)^distance:.*$","")       // remove distance lines
                        .replaceAll("(?m)^file_name:.*$","")      // remove file_name lines
                        .replaceAll("(?m)^page_number:.*$","")    // remove page_number lines
                        .replaceAll("(?m)\\d+ \\|  P a g e","")  // remove page numbers
                        .replaceAll("(?m)^\\s+","")               // trim leading spaces
                )
                .collect(Collectors.joining("\n"));

        HashMap<String,Object> map = new HashMap<>();
        map.put("question",question.getQuestion());
        map.put("information",information);
        SystemPromptTemplate systemPromptTemplate = SystemPromptTemplate.builder()
                .template("You are a helpful assistant. Use the following information to answer the question:\n" +
                        "{information}\n" +
                        "Question: {question}  \n" +".If you dont know the answer, simply say that you don't know. Do not try to make up an answer.")
                .variables(map)
                .build();

        String promptText = systemPromptTemplate.render(map);

        ChatResponse response = chatModel.call(new Prompt(promptText , OllamaOptions.builder()
                .model("gemma3:4b")
                .temperature(0.3)
                .build())
               );
       System.out.println(response);
         return response.getResult().getOutput().getText();
    }
}
