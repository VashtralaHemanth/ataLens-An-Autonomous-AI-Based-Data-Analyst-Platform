# 🔬 DataLens — Autonomous Data Analyst Platform

> Upload any dataset → get instant AI-powered EDA, interactive charts, and actionable insights.

---

## 🏗️ Architecture

```
┌──────────────────────┐        ┌──────────────────────┐        ┌─────────────────────────┐
│   React Frontend     │  HTTP  │  Spring Boot Backend  │  HTTP  │   Python AI Engine      │
│   (Vite + Tailwind)  │◄──────►│  (Java 17 + JWT)      │◄──────►│   (FastAPI + Pandas)    │
│   localhost:5173     │        │  localhost:8080        │        │   localhost:8000        │
└──────────────────────┘        └──────────┬───────────┘        └─────────────────────────┘
                                           │
                                    ┌──────▼──────┐
                                    │    MySQL     │
                                    │  Port 3306   │
                                    └─────────────┘
```

---

## 📁 Project Structure

```
autonomous-data-analyst-platform/
├── frontend/               React + Vite + TailwindCSS
│   └── src/
│       ├── pages/          LoginPage, RegisterPage, DashboardPage,
│       │                   UploadDatasetPage, DatasetAnalysisPage
│       ├── components/     Navbar, Sidebar, UploadForm, ChartCard,
│       │                   InsightsPanel, KPICards, DatasetCard
│       └── services/       api.js, authService.js, datasetService.js
│
├── backend/                Spring Boot Java 17
│   └── src/main/java/com/dataanalyst/
│       ├── controller/     AuthController, DatasetController, AnalysisController
│       ├── service/        AuthService, DatasetService, AnalysisService
│       ├── repository/     UserRepository, DatasetRepository, AnalysisRepository
│       ├── model/          User, Dataset, AnalysisResult
│       ├── security/       JwtFilter, JwtUtil, SecurityConfig
│       └── config/         AppConfig, GlobalExceptionHandler
│
├── ai-engine/              FastAPI Python 3.11+
│   ├── app.py              Main FastAPI application
│   └── analysis/
│       ├── data_profiler.py    Dataset profiling & statistics
│       ├── data_cleaner.py     Automated data cleaning
│       ├── eda_analysis.py     EDA → chart-ready JSON data
│       ├── chart_generator.py  Matplotlib/Seaborn → base64 PNGs
│       └── insight_generator.py  AI-generated text insights
│
└── schema.sql              MySQL database schema
```

---

## ⚡ Quick Start

### Prerequisites

| Tool        | Version  |
|-------------|----------|
| Node.js     | 18+      |
| Java (JDK)  | 17+      |
| Maven       | 3.8+     |
| Python      | 3.11+    |
| MySQL       | 8.0+     |

---

### 1 — Database Setup

```bash
# Start MySQL and run the schema
mysql -u root -p < schema.sql
```

This creates:
- Database: `data_analyst_db`
- Tables: `users`, `datasets`, `analysis_results`
- Default admin: `admin@datalens.io` / `admin123`

---

### 2 — Backend (Spring Boot)

```bash
cd backend

# Edit database credentials
nano src/main/resources/application.properties
# Set: spring.datasource.username and spring.datasource.password

# Build and run
./mvnw spring-boot:run
# OR on Windows:
mvnw.cmd spring-boot:run
```

Backend starts at **http://localhost:8080**

**Key `application.properties` settings:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/data_analyst_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

app.jwt.secret=YourSuperSecretJWTKey...
app.upload.dir=./uploads
app.ai.engine.url=http://localhost:8000
```

---

### 3 — AI Engine (FastAPI)

```bash
cd ai-engine

# Create virtual environment
python -m venv venv

# Activate
# Mac/Linux:
source venv/bin/activate
# Windows:
venv\Scripts\activate

# Install dependencies
pip install -r requirements.txt

# Run the service
uvicorn app:app --host 0.0.0.0 --port 8000 --reload
```

AI Engine starts at **http://localhost:8000**

Verify: open http://localhost:8000/docs for the Swagger UI.

---

### 4 — Frontend (React + Vite)

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

Frontend starts at **http://localhost:5173**

**Optional — set API URL:**
```bash
# Create .env file
echo "VITE_API_URL=http://localhost:8080" > .env
```

---

## 🔄 System Workflow

```
User Uploads CSV/Excel/JSON
         ↓
Spring Boot saves file to ./uploads/
         ↓
POST /analysis/run/{datasetId}
         ↓
Spring Boot calls Python: POST http://localhost:8000/analyze
         ↓
Python Pipeline:
  1. Load DataFrame (pandas)
  2. Profile raw data (data_profiler.py)
  3. Auto-clean data (data_cleaner.py)
  4. Run EDA → JSON charts (eda_analysis.py)
  5. Generate chart images (chart_generator.py)
  6. Generate text insights (insight_generator.py)
         ↓
JSON result returned to Spring Boot
         ↓
Saved to analysis_results table
         ↓
Frontend polls → renders dashboard
```

---

## 🌐 API Reference

### Authentication

| Method | Endpoint           | Description        |
|--------|--------------------|--------------------|
| POST   | `/auth/register`   | Register new user  |
| POST   | `/auth/login`      | Login → get JWT    |

**Login request:**
```json
{ "email": "user@example.com", "password": "password123" }
```

**Login response:**
```json
{
  "token": "eyJhbGc...",
  "user": { "id": 1, "name": "Jane", "email": "user@example.com" }
}
```

### Datasets

| Method | Endpoint              | Description            |
|--------|-----------------------|------------------------|
| POST   | `/datasets/upload`    | Upload dataset file    |
| GET    | `/datasets`           | List user's datasets   |
| GET    | `/datasets/{id}`      | Get dataset details    |
| DELETE | `/datasets/{id}`      | Delete dataset         |

### Analysis

| Method | Endpoint                  | Description              |
|--------|---------------------------|--------------------------|
| POST   | `/analysis/run/{datasetId}` | Trigger AI analysis    |
| GET    | `/analysis/{datasetId}`   | Get analysis results     |

### Python AI Engine

| Method | Endpoint    | Description                 |
|--------|-------------|-----------------------------|
| POST   | `/analyze`  | Full EDA pipeline           |
| POST   | `/profile`  | Quick profile only          |
| GET    | `/health`   | Health check                |

---

## 📊 Charts Generated

| Chart Type           | Description                                       |
|----------------------|---------------------------------------------------|
| Correlation Heatmap  | Pearson correlation matrix for numeric columns    |
| Distribution Charts  | Histograms with mean/median overlay               |
| Categorical Bar      | Top-N value counts per categorical column         |
| Time Series          | Monthly aggregated trends for datetime columns    |
| Scatter Plot         | First two numeric columns vs each other           |
| Missing Values       | Bar chart of missing % per column                 |
| Box Plot Summary     | Min/Q1/Median/Q3/Max per numeric column           |

---

## 🤖 AI Insights Categories

| Tag         | Meaning                                     |
|-------------|---------------------------------------------|
| `[INFO]`    | Informational observation                   |
| `[WARNING]` | Data quality concern requiring attention    |
| `[SUCCESS]` | Positive quality signal                     |
| `[TREND]`   | Pattern or trend detected in the data       |

---

## 🔐 Security

- Passwords hashed with **BCrypt** (strength 10)
- JWT tokens expire after **24 hours**
- All dataset endpoints require `Authorization: Bearer <token>`
- CORS configured for `localhost:5173` (adjust for production)

---

## 🚀 Production Deployment

### Environment Variables (Backend)
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://prod-host:3306/data_analyst_db
SPRING_DATASOURCE_USERNAME=dbuser
SPRING_DATASOURCE_PASSWORD=securepass
APP_JWT_SECRET=512-bit-random-secret
APP_AI_ENGINE_URL=http://ai-engine:8000
APP_UPLOAD_DIR=/data/uploads
```

### Build for Production

**Frontend:**
```bash
cd frontend && npm run build
# Serve ./dist with nginx
```

**Backend:**
```bash
cd backend && ./mvnw package -DskipTests
java -jar target/autonomous-data-analyst-1.0.0.jar
```

**AI Engine:**
```bash
cd ai-engine
uvicorn app:app --host 0.0.0.0 --port 8000 --workers 4
```

---

## 🐛 Troubleshooting

| Problem                         | Solution                                               |
|---------------------------------|--------------------------------------------------------|
| MySQL connection refused        | Ensure MySQL is running: `sudo service mysql start`    |
| JWT invalid / 401 errors        | Check `app.jwt.secret` is set and ≥32 chars            |
| AI engine unreachable           | Verify FastAPI is running on port 8000                 |
| File upload fails               | Check `app.upload.dir` directory exists and is writable|
| CORS errors in browser          | Add your origin to `app.cors.allowed-origins`          |
| Python `ModuleNotFoundError`    | Activate venv: `source venv/bin/activate`              |

---

## 📦 Tech Stack

| Layer    | Technology                                               |
|----------|----------------------------------------------------------|
| Frontend | React 18, Vite 5, TailwindCSS 3, Recharts, Axios         |
| Backend  | Spring Boot 3.2, Java 17, Spring Security, JWT, JPA      |
| Database | MySQL 8.0                                                |
| AI Engine| FastAPI, Pandas, NumPy, Matplotlib, Seaborn, Scikit-learn|

---

*Built with ❤️ — DataLens Autonomous Data Analyst Platform*
