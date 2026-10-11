# 💼 InternMatch — AI-Powered Internship & Career Matching Platform

[![Frontend CI](https://github.com/SANTANATILAK/ai-internship-matching-platform/actions/workflows/frontend.yml/badge.svg)](https://github.com/SANTANATILAK/ai-internship-matching-platform/actions/workflows/frontend.yml)
[![Backend CI](https://github.com/SANTANATILAK/ai-internship-matching-platform/actions/workflows/backend.yml/badge.svg)](https://github.com/SANTANATILAK/ai-internship-matching-platform/actions/workflows/backend.yml)
[![Live Demo](https://img.shields.io/badge/Live_Demo-Vercel-black?logo=vercel)](https://frontend-ruddy-eta-66.vercel.app)
[![Java 17](https://img.shields.io/badge/Java-17_LTS-orange?logo=openjdk)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue?logo=react)](https://react.dev)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)](https://mysql.com)

> An end-to-end full-stack platform that analyzes resumes with Apache PDFBox, scores ATS readiness (0–100), and matches students across **all engineering branches** to verified, active internships and entry-level jobs — backed by an autonomous **10-minute crawler** that keeps opportunities fresh and fraud-free.

---

## 🔗 Live Links & Demo

* 🚀 **Live Web App (Vercel)**: [https://frontend-ruddy-eta-66.vercel.app](https://frontend-ruddy-eta-66.vercel.app)
* ⚙️ **Cloud Backend (Live Tunnel)**: [https://capture-mentioned-folk-giving.trycloudflare.com](https://capture-mentioned-folk-giving.trycloudflare.com/api/opportunities)
* 📂 **GitHub Repository**: [https://github.com/SANTANATILAK/ai-internship-matching-platform](https://github.com/SANTANATILAK/ai-internship-matching-platform)

### 🔑 Demo Accounts to Test
| Role | Email | Password |
| :--- | :--- | :--- |
| **Student** | `tilak@gmail.com` | `password123` |
| **Admin** | `admin@internmatch.com` | `Admin@123` |

---

## 💡 Why I Built This

Every placement season, college students deal with the same frustrating cycle:
1. **Endless Spam & Fake Postings**: Most public job boards are flooded with consultancies asking for "registration fees" or unpaid training disguised as internships.
2. **One-Size-Fits-All Job Boards**: Nearly every platform caters exclusively to Computer Science, leaving students in **Mechanical, Civil, ECE, EEE, and Chemical Engineering** with almost no targeted opportunities.
3. **Black Box Applications**: Students submit hundreds of generic resumes without knowing why they get rejected or what skills they're actually missing.

**InternMatch** was built to solve this. It combines resume parsing, transparent ATS scoring, cross-branch semantic matching, and automated 10-minute web crawlers that verify official company portals (Workday, Lever, Greenhouse, official enterprise career pages) before listing them.

---

## ✨ Key Features

### 1. 📄 Intelligent ATS Resume Analyzer (0–100 Score)
- Upload PDF resumes with drag-and-drop or file browser.
- Uses **Apache PDFBox** to extract plain text, section headings, graduation batch, and technical skills.
- Evaluates four key dimensions:
  - **Keyword & Skill Density** (35%)
  - **Structure & Formatting** (25%)
  - **Section Completeness** (Projects, Education, Experience) (25%)
  - **Contact & Profile Integrity** (15%)
- Delivers an explainable score with personalized strengths, weaknesses, and targeted improvement suggestions.

### 2. 🎯 Multi-Branch AI Matching Engine
Unlike CS-only platforms, InternMatch covers **120+ specialized skills** across 7 major engineering disciplines:
* **Computer Science & IT**: Python, Java, Spring Boot, React, Node.js, Docker, AWS, SQL
* **AI & Data Science**: PyTorch, TensorFlow, Scikit-Learn, Pandas, Computer Vision, NLP
* **Electronics & Comm (ECE) / EEE**: VLSI, Verilog, Embedded Systems, Microcontrollers, MATLAB, IoT, PCB Design
* **Mechanical Engineering**: AutoCAD, SolidWorks, CATIA, ANSYS, GD&T, FEA, Thermal Systems, CNC
* **Civil Engineering**: STAAD.Pro, AutoCAD Civil 3D, Revit, Structural Analysis, Geotechnical, Surveying
* **Chemical & Biotech**: Aspen Plus, Chemical Process Simulation, Mass Transfer, Reaction Kinetics

For each opening, candidates see:
- Exact **Match Percentage** (e.g. `92% — Strong Match`)
- **Matched Skills** (highlighted in emerald green)
- **Missing Skills** (highlighted in amber so students know what to learn)
- Batch & Branch Eligibility status

### 3. ⏱️ 10-Minute Autonomous Background Sync
- A Spring Boot scheduler (`@Scheduled`) runs every **10 minutes** in the background.
- Crawls and synchronizes official career feeds (Google, Microsoft, Amazon, Tata Motors, L&T, Siemens, Qualcomm, etc.).
- Verifies company domains, updates stipends/deadlines, marks expired listings as closed, and deduplicates records using canonical URLs and source IDs.
- Tracks sync history and execution logs in MySQL.

### 4. 🛡️ Anti-Fraud & Direct Enterprise Links
- Automated heuristics detect and block suspicious keywords (`consultancy`, `training fee`, `registration fee`, `pay to apply`, `mlm`).
- Ensures **"Apply Now"** buttons only redirect to authentic employer career portals.
- Integrated application tracker so students can log submissions and track status (`APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`).

### 5. 🎨 Cyber Dark SaaS UI
- Built with React 18, Vite, and Lucide icons.
- Obsidian dark palette with electric cyan accents, subtle glassmorphism cards, and animated SVG gauges for ATS scores.
- Fully responsive across mobile, tablet, and widescreen monitors.

---

## 🏗️ Architecture & Data Flow

```
                                +-----------------------------+
                                |  React + Vite Web Client    |
                                |  (Obsidian Cyber Theme)     |
                                +--------------+--------------+
                                               |
                                     REST APIs / JWT Token
                                               |
                                               v
+-----------------------------------------------------------------------------------------+
|                              Spring Boot 3 Backend Server                               |
|                                                                                         |
|  +--------------------+   +-----------------------+   +------------------------------+  |
|  | Spring Security    |   | Apache PDFBox Parser  |   | AI Matching Engine           |  |
|  | - Stateless JWT    |   | - Text Extraction     |   | - 120+ Multi-branch Skills   |  |
|  | - BCrypt Passwords |   | - Section Recognizer  |   | - Fuzzy Alias Normalizer     |  |
|  | - Role-based Auth  |   | - ATS Score Engine    |   | - Batch Eligibility Check    |  |
|  +--------------------+   +-----------------------+   +------------------------------+  |
|                                                                                         |
|  +------------------------------------------------+   +------------------------------+  |
|  | 10-Minute Background Scheduler                 |   | Anti-Fraud Verification      |  |
|  | - Opportunity Crawler & Official Feeds         |   | - Whitelist Enterprise ATS   |  |
|  | - Automated Company Discovery & Sync           |   | - Consultancy Filter         |  |
|  +------------------------------------------------+   +------------------------------+  |
+----------------------------------------------+------------------------------------------+
                                               |
                                     Spring Data JPA / Hibernate
                                               |
                                               v
                                +-----------------------------+
                                |      MySQL 8.0 Database     |
                                | (Users, Resumes, Companies, |
                                |  Opportunities, Tracker)    |
                                +-----------------------------+
```

---

## 🛠️ Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Frontend** | React 18, Vite, React Router 6, Axios, Lucide React, Modern CSS3 |
| **Backend** | Java 17 LTS, Spring Boot 3 / 4, Spring Security, JJWT 0.12, Apache PDFBox |
| **Database** | MySQL 8.0 Server (also compatible with PostgreSQL on Render) |
| **ORM / Data** | Spring Data JPA, Hibernate ORM |
| **DevOps / CI** | GitHub Actions (CI for Maven & Vite), Docker, Vercel, Cloudflare Tunnel |

---

## 🚀 Running the Project Locally

### Prerequisites
- **Java 17+** (JDK)
- **Node.js 18+** & npm
- **MySQL 8.0** Server running on `localhost:3306`

---

### Step 1: Clone the Repository
```bash
git clone https://github.com/SANTANATILAK/ai-internship-matching-platform.git
cd ai-internship-matching-platform
```

### Step 2: Set Up MySQL Database
Create the database in MySQL (tables are automatically created by Hibernate on startup):
```sql
CREATE DATABASE IF NOT EXISTS internship_platform;
```

*(Optional: configure username/password in `backend/src/main/resources/application.properties` if different from default `root` / `NANI@123`)*

### Step 3: Run the Backend (Spring Boot)
```bash
cd backend
./mvnw clean spring-boot:run
```
*Backend starts on `http://localhost:9090`.*

### Step 4: Run the Frontend (React + Vite)
In a separate terminal window:
```bash
cd frontend
npm install
npm run dev
```
*Frontend opens on `http://localhost:5173`.*

---

## 📂 Project Structure

```
ai-internship-matching-platform/
├── .github/workflows/
│   ├── backend.yml               # GitHub Actions CI for Maven compile & tests
│   └── frontend.yml              # GitHub Actions CI for Vite build
├── backend/
│   ├── pom.xml                   # Maven dependencies & plugins
│   ├── Dockerfile                # Multi-stage production container
│   └── src/
│       ├── main/java/com/tilak/internship_platform/
│       │   ├── config/           # CORS, GlobalExceptionHandler, Database configs
│       │   ├── controller/       # REST endpoints (Matching, Opportunities, Users, Resume, etc.)
│       │   ├── entity/           # JPA entities (User, Resume, Opportunity, Company, etc.)
│       │   ├── repository/       # Spring Data JPA repositories
│       │   ├── security/         # JWT filter, JwtService, SecurityConfig
│       │   └── service/          # Core services (Matching, ATS Parser, 10-Min Scheduler, etc.)
│       └── test/                 # JUnit 5 test suites (Matching, Opportunity, PDF, User tests)
├── frontend/
│   ├── package.json              # React, Vite, Axios, Lucide dependencies
│   ├── vite.config.js            # Vite build configuration
│   └── src/
│       ├── api/                  # Axios instance with JWT interceptor & endpoints
│       ├── components/           # Reusable UI components (OpportunityCard, AtsGauge, Navbar, etc.)
│       ├── context/              # AuthContext (login, register, token persistence)
│       └── pages/                # Main views (Dashboard, Matches, Opportunities, Resume, etc.)
└── README.md
```

---

## 🧪 Testing

The backend includes a comprehensive JUnit 5 test suite verifying skill matching, PDF parsing, opportunity eligibility, and user security:

```bash
cd backend
./mvnw test
```

---

## 👨‍💻 Author

**Tilak Santan**  
* GitHub: [@SANTANATILAK](https://github.com/SANTANATILAK)  
* Project: [ai-internship-matching-platform](https://github.com/SANTANATILAK/ai-internship-matching-platform)

---

## 📜 License

This project is open-source and available under the [MIT License](LICENSE).
