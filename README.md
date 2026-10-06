# InternMatch: AI-Powered Internship & Career Matching Platform

**InternMatch** is an enterprise-grade AI matching platform that parses student resumes with Apache PDFBox, calculates a comprehensive 0-100 ATS readiness score, performs dynamic graduation-batch and skill-normalization matching, and schedules automated hourly background updates to discover authentic opportunities while eliminating consultancies and fake job postings.

---

## 🌟 Key Highlights

- **ATS Resume Scoring Engine**: Extracts personal data, educational credentials, graduation years, technical skills, projects, and certifications. Computes an authentic 0-100 score with targeted feedback.
- **Dynamic Graduation Year Engine**: Dynamically calculates eligibility (e.g. Internships vs. PPO vs. Full-Time) relative to the calendar year without fragile hardcoding.
- **Skill Normalization & Semantic Matcher**: Normalizes synonyms (e.g., `AI` ↔ `Artificial Intelligence`, `JS` ↔ `JavaScript`, `ReactJS` ↔ `React`) and delivers detailed percentage matches with matched & missing skill badges.
- **Anti-Fraud & Domain Verification**: Rejects consultancies, training fees, paid internships, and unverified personal emails. Whitelists enterprise ATS portals (Workday, Greenhouse, Lever, Taleo, Ashby, official career portals).
- **Scheduled Automated Hourly Collection**: Spring Scheduler executes every hour at minute 0 (`Asia/Kolkata` timezone) to discover new opportunities, update changed criteria, and mark expired postings without deleting application histories.
- **Application & Bookmark Tracker**: Enables students to apply directly through authentic portals while tracking interview statuses (`APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`, `WITHDRAWN`).
- **Administrative Governance Portal**: Manage verified companies, curate listings, monitor collector execution logs, and trigger collection cycles on-demand.

---

## 🏛️ System Architecture

```
+------------------------------------------------------------------------+
|                   FRONTEND: React + Vite (InternMatch)                 |
|  - Student Dashboard (ATS Score, Top Matches, Applications, Alerts)    |
|  - Resume Analysis & Upload Center                                     |
|  - Intelligent Match Explorer (Match %, Missing Skills, Filters)       |
|  - Official Application Tracker & Saved Opportunities                  |
|  - Admin Dashboard (Company Verification, Job Curation, Logs)          |
+-----------------------------------+------------------------------------+
                                    | REST APIs (JSON / JWT Bearer)
                                    v
+------------------------------------------------------------------------+
|                 BACKEND: Spring Boot 3 (Java 17 LTS)                    |
|  - Security & Auth: Spring Security + JWT (JJWT 0.12) + BCrypt         |
|  - PDF Processing: Apache PDFBox Text Extraction                       |
|  - ATS Scoring Engine: Resume Structure, Keyword & Completeness Scorer |
|  - Matching Engine: Dynamic Eligibility, Skill Normalization, Rules    |
|  - Hourly Scheduler: Automated Verification, Scraping/Feeds, Expiry    |
|  - Anti-Fraud Engine: Suspicious Domain, Consultancy & Fee Detector    |
+-----------------------------------+------------------------------------+
                                    | Spring Data JPA / Hibernate
                                    v
+------------------------------------------------------------------------+
|                      DATABASE: MySQL 8.0 Server                        |
|  - users, resumes, skills, user_skills, opportunities, companies,      |
|    applications, saved_opportunities, ats_results, verification_logs   |
+------------------------------------------------------------------------+
```

---

## 📂 Repository Structure

```
.
├── .github/
│   └── workflows/
│       ├── backend.yml         # GitHub Actions CI for Maven build & tests
│       └── frontend.yml        # GitHub Actions CI for Vite build
├── backend/
│   ├── pom.xml                 # Maven build configuration
│   ├── Dockerfile              # Production multi-stage Docker container
│   └── src/
│       ├── main/
│       │   ├── java/com/tilak/internship_platform/
│       │   │   ├── config/     # Security, CORS, Seeders
│       │   │   ├── security/   # JWT Provider, Auth Filter, UserPrincipal
│       │   │   ├── entity/     # User, Resume, Opportunity, Company, etc.
│       │   │   ├── repository/ # Spring Data JPA Repositories
│       │   │   ├── dto/        # Request & Response Data Transfer Objects
│       │   │   ├── parser/     # PDFBox Parser & Resume NLP Analyzer
│       │   │   ├── matcher/    # Normalizer, Eligibility & Matching Engines
│       │   │   ├── verification/ # Anti-Fraud Heuristics & Verification
│       │   │   ├── collector/  # Multi-source Connectors & Deduplication
│       │   │   ├── scheduler/  # Spring Hourly Opportunity Sync
│       │   │   ├── service/    # Business services
│       │   │   ├── controller/ # REST API Controllers
│       │   │   └── exception/  # Global Exception Handler (@RestControllerAdvice)
│       │   └── resources/
│       │       ├── application.yml
│       │       └── schema.sql  # MySQL DDL Schema Script
│       └── test/               # JUnit 5 Unit & Integration tests
├── frontend/
│   ├── package.json            # React, Vite, Axios, Lucide Icons
│   ├── vite.config.js          # Vite config with backend proxy
│   ├── Dockerfile              # Nginx production container
│   ├── vercel.json             # Vercel SPA routing
│   └── src/
│       ├── api/                # Centralized Axios client & endpoints
│       ├── context/            # AuthContext (JWT management)
│       ├── components/         # Navbar, Footer, OpportunityCard, AtsGauge
│       └── pages/              # Dashboard, Resume, Matches, Admin, etc.
├── docker-compose.yml          # Full-stack container orchestration
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started Locally

### 1. Database Setup (MySQL)
Open your MySQL terminal or MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS internship_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Run Backend (Spring Boot)
In VS Code terminal:
```powershell
cd backend
mvn spring-boot:run
```
*Backend starts on `http://localhost:8080`.*

### 3. Run Frontend (React + Vite)
In a second VS Code terminal:
```powershell
cd frontend
npm install
npm run dev
```
*Frontend opens on `http://localhost:5173`.*

---

## 🔑 Default Credentials

- **Admin Account**: `admin@internmatch.com` / `Admin@123`
- **Student Account**: Register any new account through `/register` or the registration form.

---

## 🐳 Docker Compose (One-Command Launch)

To start MySQL 8.0, Backend, and Frontend together:
```bash
docker compose up --build
```
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- MySQL: `localhost:3306`

---

## 🌐 Production Cloud Deployment

### Deploy Backend (Render / Railway / AWS)
1. Link your GitHub repository.
2. Select Root Directory: `backend`.
3. Build Command: `mvn clean package -DskipTests`.
4. Start Command: `java -jar target/*.jar`.
5. Environment Variables:
   - `DB_URL`: JDBC connection string to managed MySQL.
   - `DB_USERNAME`: Database username.
   - `DB_PASSWORD`: Database password.
   - `JWT_SECRET`: 256-bit secret key.

### Deploy Frontend (Vercel / Netlify)
1. Link your GitHub repository.
2. Root Directory: `frontend`.
3. Framework Preset: `Vite`.
4. Build Command: `npm run build`.
5. Output Directory: `dist`.
6. Environment Variable:
   - `VITE_API_URL`: Your deployed backend URL (e.g. `https://your-backend.onrender.com`).
