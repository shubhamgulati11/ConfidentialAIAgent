# Confidential AI Agent 🔒🤖

A privacy-focused AI agent that runs entirely locally using Ollama and Spring AI. Process confidential documents and ask questions without sending data to external APIs.

## 🌟 Features

- **100% Local Processing**: All AI operations run on your machine using Ollama
- **PDF Document Analysis**: Automatic PDF parsing and vector embedding
- **Retrieval Augmented Generation (RAG)**: Answers questions based on your documents
- **Vector Store Persistence**: Local JSON-based vector storage
- **REST API**: Simple endpoint for querying your documents
- **Privacy First**: No data leaves your machine

## 🛠️ Tech Stack

- **Java** - Core programming language
- **Spring Boot** - Application framework
- **Spring AI** - AI integration framework
- **Ollama** - Local LLM runtime
- **Gemma 3 (4B)** - Language model for responses
- **SimpleVectorStore** - Local vector database

## 📋 Prerequisites

1. **Java 17+** installed
2. **Maven** or Gradle for build
3. **Ollama** installed and running locally
   ```bash
   # Install Ollama (macOS/Linux)
   curl -fsSL https://ollama.com/install.sh | sh
   
   # Or visit: https://ollama.com/download
   ```

4. **Pull the Gemma3 model**
   ```bash
   ollama pull gemma3:4b
   ```

5. **Ensure Ollama is running**
   ```bash
   ollama serve
   # Should be running on http://localhost:11434
   ```

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/shubhamgulati11/ConfidentialAIAgent.git
cd ConfidentialAIAgent
```

### 2. Add Your PDF Document
Place your PDF file in `src/main/resources/` and name it `bob.pdf`, or update the filename in `PdfReader.java`:

```java
@Value("classpath:your-document.pdf")
private Resource pdfResource;
```

### 3. Build and Run
```bash
# Using Maven
mvn clean install
mvn spring-boot:run

# Or using Maven Wrapper
./mvnw clean install
./mvnw spring-boot:run
```

The application will:
- Start on `http://localhost:8080` (default Spring Boot port)
- Load and process your PDF on startup
- Create embeddings and store them in `vectorestore.json`

## 📡 API Usage

### Ask a Question

**Endpoint:** `POST /query`

**Request:**
```bash
curl -X POST http://localhost:8080/query \
  -H "Content-Type: application/json" \
  -d '{
    "question": "What is the main topic of the document?"
  }'
```

**Response:**
```
Based on the provided information, the main topic is...
```

### Example with Different Questions

```bash
# Specific fact extraction
curl -X POST http://localhost:8080/query \
  -H "Content-Type: application/json" \
  -d '{"question": "What are the key findings?"}'

# Summary request
curl -X POST http://localhost:8080/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Can you summarize the conclusion?"}'
```

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    User Request                          │
│                  POST /query {question}                  │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              ChatbotController                           │
│  • Receives question                                     │
│  • Performs similarity search on vector store            │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              VectorStore (SimpleVectorStore)             │
│  • Returns most relevant document chunks                 │
│  • Stored locally in vectorestore.json                   │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│              Ollama (Gemma3:4b)                          │
│  • Processes question + retrieved context                │
│  • Generates answer locally                              │
│  • Temperature: 0.3 (factual responses)                  │
└─────────────────────┬───────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────────┐
│                    Response                              │
│              Returns answer to user                      │
└─────────────────────────────────────────────────────────┘
```

### Component Details

#### **PdfReader.java**
- Loads PDF from resources on application startup
- Extracts text using `PagePdfDocumentReader`
- Splits text into chunks using `TokenTextSplitter`
- Stores embeddings in local vector store

#### **ChatbotController.java**
- REST endpoint for queries
- Performs semantic similarity search
- Cleans up metadata from retrieved documents
- Constructs prompt with context and question
- Calls Ollama model for response

#### **VectorStoreConfig.java**
- Configures local vector store with embedding model
- Uses Ollama embeddings (connects to `http://localhost:11434`)

#### **QuestionRequest.java**
- Simple POJO for API request body

## ⚙️ Configuration

### Change the AI Model

Edit `ChatbotController.java`:
```java
OllamaOptions.builder()
    .model("gemma3:4b")  // Change to llama3, mistral, etc.
    .temperature(0.3)     // Adjust creativity (0.0-1.0)
    .build()
```

Available models (after pulling with Ollama):
- `gemma3:4b` (default, lightweight)
- `llama3`
- `mistral`
- `phi3`

### Adjust Response Temperature

- **0.0-0.3**: More factual, deterministic
- **0.4-0.7**: Balanced
- **0.8-1.0**: More creative, varied

### Change Vector Store Location

Edit `PdfReader.java`:
```java
((SimpleVectorStore)vectorStore).save(new File("path/to/vectorstore.json"));
```

## 🔒 Privacy & Security

✅ **All processing happens locally** - No external API calls  
✅ **Your documents never leave your machine**  
✅ **Open-source components** - Fully auditable  
✅ **Local vector storage** - Data persists in `vectorstore.json`  
✅ **No telemetry** - Complete data sovereignty  

**Perfect for:**
- Legal documents
- Medical records
- Financial reports
- Internal company documents
- Research papers
- Personal notes

## 📂 Project Structure

```
ConfidentialAIAgent/
├── src/main/java/com/shubham/confidentialai/
│   ├── ConfidentialAiAgentApplication.java  # Main application
│   ├── ChatbotController.java               # REST API endpoint
│   ├── PdfReader.java                       # PDF processing
│   ├── VectorStoreConfig.java               # Vector store setup
│   └── QuestionRequest.java                 # Request DTO
├── src/main/resources/
│   ├── application.properties               # Config
│   └── bob.pdf                              # Your PDF document
└── vectorestore.json                        # Generated vector DB
```

## 🧪 Testing

### Test the API
```bash
# Health check (if you add one)
curl http://localhost:8080/actuator/health

# Query test
curl -X POST http://localhost:8080/query \
  -H "Content-Type: application/json" \
  -d '{"question": "Test question about the document"}'
```

### Verify Ollama
```bash
# Check Ollama is running
curl http://localhost:11434/api/tags

# Test model directly
ollama run gemma3:4b "Hello, how are you?"
```

## 🐛 Troubleshooting

### Ollama Connection Error
```
Error: Connection refused to localhost:11434
```
**Solution:** Ensure Ollama is running:
```bash
ollama serve
```

### Model Not Found
```
Error: Model 'gemma3:4b' not found
```
**Solution:** Pull the model:
```bash
ollama pull gemma3:4b
```

### PDF Not Loading
```
Error: Resource not found
```
**Solution:** 
- Check PDF is in `src/main/resources/`
- Verify filename matches in `PdfReader.java`
- Rebuild the project

### Out of Memory
**Solution:** Increase JVM heap size:
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx4g"
```

## 🔄 Future Enhancements

- [ ] Support multiple PDF uploads
- [ ] Web UI for easier interaction
- [ ] Support for Word documents, text files
- [ ] Conversation history
- [ ] Multiple vector store backends
- [ ] Docker containerization
- [ ] Citation/source tracking in responses
- [ ] Batch question processing

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 🙏 Acknowledgments

- [Spring AI](https://docs.spring.io/spring-ai/reference/) - AI integration framework
- [Ollama](https://ollama.com/) - Local LLM runtime
- [Google Gemma](https://ai.google.dev/gemma) - Open language model

## 📧 Contact

**Shubham Gulati**
- GitHub: [@shubhamgulati11](https://github.com/shubhamgulati11)

---

⭐ If you find this project helpful, please consider giving it a star!

**Built with privacy in mind. Your data stays yours.** 🔒
