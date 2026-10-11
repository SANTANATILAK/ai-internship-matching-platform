# 💼 InternMatch

A full-stack web platform that parses student resumes, calculates an ATS readiness score (0–100), and matches students across all engineering branches to verified internships and job openings.

**[🌐 Live Demo](https://frontend-ruddy-eta-66.vercel.app)** • **[⚙️ API Tunnel](https://capture-mentioned-folk-giving.trycloudflare.com/api/opportunities)** • **[📦 GitHub Repo](https://github.com/SANTANATILAK/ai-internship-matching-platform)**

---

## 🚀 Live Demo & Test Credentials

- **Website URL**: [https://frontend-ruddy-eta-66.vercel.app](https://frontend-ruddy-eta-66.vercel.app)
- **Student Login**: `tilak@gmail.com` / `password123`
- **Admin Login**: `admin@internmatch.com` / `Admin@123`

---

## ⚡ Features

- **ATS Resume Scoring (0–100)**: Upload a PDF resume to extract skills, education, and projects with Apache PDFBox, and get an instant score with actionable feedback.
- **Multi-Branch Matching**: Matches opportunities for students in **CSE, AI/ML, ECE, EEE, Mechanical, Civil, and Chemical Engineering**.
- **10-Minute Background Sync**: An automated Spring Boot scheduler discovers new openings, syncs companies, and purges expired listings every 10 minutes.
- **Fraud & Consultancy Filter**: Blocks fake postings, placement agency fees, and ensures "Apply Now" buttons redirect straight to verified employer portals.
- **Application Tracker**: Bookmark jobs, log submissions, and track statuses from `Applied` to `Offer`.
- **Dark Mode UI**: Clean, responsive interface built with React, Vite, and Lucide icons.

---

## 🛠️ Tech Stack

- **Frontend**: React 18, Vite, React Router, Axios, Modern CSS
- **Backend**: Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, Apache PDFBox
- **Database**: MySQL 8.0 (PostgreSQL compatible)
- **Deployment**: Vercel (Frontend), Cloudflare Tunnel (Live Backend API)

---

## 💻 Run Locally

### 1. Database Setup
Create the MySQL database:
```sql
CREATE DATABASE IF NOT EXISTS internship_platform;
```

### 2. Start Backend (Port 9090)
```bash
cd backend
./mvnw clean spring-boot:run
```

### 3. Start Frontend (Port 5173)
```bash
cd frontend
npm install
npm run dev
```

---

## 🧪 Run Tests

```bash
cd backend
./mvnw test
```

---

## 📁 Project Structure

```
ai-internship-matching-platform/
├── backend/            # Spring Boot REST API
│   ├── src/main/java/  # Controllers, Services, Entities, Security
│   └── src/test/       # JUnit 5 test suites
├── frontend/           # React + Vite application
│   └── src/            # Pages, Components, Context, API client
└── README.md
```
