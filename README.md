# 🔗 URL Shortener
> A full-stack URL shortening application built with **Angular 17** and **Spring Boot 3.4**.  
> Paste any long URL and get a compact, shareable link — with click tracking and optional expiration.
---
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-6DB33F?logo=springboot)
![Angular](https://img.shields.io/badge/Angular-17-DD0031?logo=angular)
![TypeScript](https://img.shields.io/badge/TypeScript-5.2-3178C6?logo=typescript)
![License](https://img.shields.io/badge/License-MIT-blue)
---
## ✨ Features
- **Instant URL shortening** — generates a unique Base62 code for any valid URL
- **Custom aliases** — choose your own short code instead of a random one
- **Link expiration** — set an optional TTL (in days) after which the link stops working
- **Click tracking** — each redirect increments a hit counter and records the last access time
- **Full CRUD** — list and delete your shortened URLs from the UI
- **Input validation** — rejects malformed URLs and duplicate custom aliases with clear error messages
---
 
## 🛠️ Tech Stack
 
| Layer | Technology |
|-------|------------|
| Frontend | Angular 17 (Standalone Components), TypeScript 5.2, TailwindCSS 3.4 |
| Backend | Java 21, Spring Boot 3.4, Spring Data JPA, Bean Validation |
| Database | H2 (in-memory, development) |
| Build tools | Maven (backend), Angular CLI (frontend) |
| Testing | JUnit 5 / Spring Boot Test (backend), Vitest (frontend) |
 
---
 
## 📋 Prerequisites
 
Make sure you have the following installed before running the project:
 
| Tool | Minimum version |
|------|----------------|
| Java JDK | 21 |
| Maven | 3.9+ |
| Node.js | 18+ |
| npm | 9+ |
| Angular CLI | 17 (`npm install -g @angular/cli@17`) |
 
---
## 🚀 Installation
### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/url-shortener.git
cd url-shortener
```
### 2. Start the backend
```bash
cd backend
mvn spring-boot:run
```
The API will be available at `http://localhost:8080`.
### 3. Start the frontend
Open a new terminal:
```bash
cd frontend
npm install
npm start
```
The app will be available at `http://localhost:4200`.
---
## 💡 Usage
### Via the web interface
1. Open `http://localhost:4200` in your browser.
2. Paste a long URL into the input field (must start with `http://` or `https://`).
3. Optionally set a **custom alias** and/or an **expiration in days**.
4. Click **Shorten** — your short link appears immediately.
5. Clicking the short link redirects to the original URL and records a hit.
### Via the REST API
**Create a short URL**
```http
POST /api/urls
Content-Type: application/json
{
  "url": "https://www.example.com/very/long/path?query=1",
  "customAlias": "my-link",   // optional
  "expirationDays": 7         // optional
}
```
**Response**
```json
{
  "code": "my-link",
  "targetUrl": "https://www.example.com/very/long/path?query=1",
  "shortUrl": "http://localhost:8080/my-link",
  "hits": 0,
  "createdAt": "2025-05-24T12:00:00Z",
  "expiresAt": "2025-05-31T12:00:00Z",
  "expired": false
}
```
**List all URLs**
```http
GET /api/urls
```
**Delete a URL**
```http
DELETE /api/urls/{code}
```
**Redirect (follow a short link)**
```http
GET /{code}
→ 302 to the original URL
```
---
## 📁 Project Structure
```
url-shortener/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/example/urlshortener/
│       ├── UrlShortenerApplication.java   # Entry point
│       ├── config/
│       │   └── CorsConfig.java            # CORS configuration
│       ├── exception/                     # Custom exceptions & global handler
│       │   ├── GlobalExceptionHandler.java
│       │   ├── DuplicateAliasException.java
│       │   ├── InvalidUrlException.java
│       │   ├── UrlNotFoundException.java
│       │   ├── UrlExpiredException.java
│       │   └── ShortCodeCollisionException.java
│       ├── url/
│       │   ├── controller/
│       │   │   ├── UrlController.java     # CRUD endpoints (/api/urls)
│       │   │   └── RedirectController.java # Redirect endpoint (/{code})
│       │   ├── dto/                       # Request / response records
│       │   ├── model/
│       │   │   └── ShortUrl.java          # JPA entity
│       │   ├── repository/
│       │   │   └── ShortUrlRepository.java
│       │   └── service/
│       │       └── UrlService.java        # Business logic
│       └── util/
│           ├── Base62.java                # Code generation
│           └── Base62Util.java
└── frontend/
    └── src/app/
        ├── features/
        │   └── url-shortener/             # Main feature module
        │       ├── components/
        │       │   ├── url-form/          # Form to create short URLs
        │       │   └── url-list/          # Table with existing URLs
        │       └── url-shortener.component.ts
        ├── models/                        # TypeScript interfaces
        ├── services/                      # HTTP client services
        ├── interceptors/                  # HTTP interceptors
        └── shared/                        # Reusable components
```
---
## 🤝 Contributing
Contributions are welcome! Here's how to get started:
1. **Fork** the repository
2. **Create a branch** for your feature or fix:
   ```bash
   git checkout -b feat/your-feature-name
   ```
3. **Commit** your changes using [Conventional Commits](https://www.conventionalcommits.org/):
   ```bash
   git commit -m "feat: add QR code generation for short URLs"
   ```
4. **Push** to your fork and open a **Pull Request** against `main`
Please make sure existing tests pass before submitting:
```bash
# Backend
cd backend && mvn test
# Frontend
cd frontend && npm test
```
---
## 📄 License
This project is licensed under the **MIT License**.  
See the [LICENSE](./LICENSE) file for details.
---
## 👤 Author
**[Lucithub]**  
---
> *This project was developed as part of an academic exercise presented through [OpenCode](https://opencode.dev).*
