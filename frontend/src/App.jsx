import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import { ProtectedRoute, AdminRoute } from './components/ProtectedRoute';

// Pages
import LandingPage from './pages/LandingPage';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import ResumeUpload from './pages/ResumeUpload';
import Matches from './pages/Matches';
import Opportunities from './pages/Opportunities';
import OpportunityDetails from './pages/OpportunityDetails';
import SavedOpportunities from './pages/SavedOpportunities';
import Applications from './pages/Applications';
import Profile from './pages/Profile';
import Notifications from './pages/Notifications';
import AdminDashboard from './pages/AdminDashboard';
import AdminCompanies from './pages/AdminCompanies';
import AdminOpportunities from './pages/AdminOpportunities';
import AdminVerification from './pages/AdminVerification';
import NotFound from './pages/NotFound';

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/opportunities" element={<Opportunities />} />
              <Route path="/opportunities/:id" element={<OpportunityDetails />} />

              {/* Student Protected Routes */}
              <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
              <Route path="/resume" element={<ProtectedRoute><ResumeUpload /></ProtectedRoute>} />
              <Route path="/matches" element={<ProtectedRoute><Matches /></ProtectedRoute>} />
              <Route path="/saved" element={<ProtectedRoute><SavedOpportunities /></ProtectedRoute>} />
              <Route path="/applications" element={<ProtectedRoute><Applications /></ProtectedRoute>} />
              <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
              <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />

              {/* Admin Protected Routes */}
              <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
              <Route path="/admin/companies" element={<AdminRoute><AdminCompanies /></AdminRoute>} />
              <Route path="/admin/opportunities" element={<AdminRoute><AdminOpportunities /></AdminRoute>} />
              <Route path="/admin/verification" element={<AdminRoute><AdminVerification /></AdminRoute>} />

              {/* 404 Route */}
              <Route path="*" element={<NotFound />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
