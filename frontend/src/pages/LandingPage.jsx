import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Briefcase, 
  Sparkles, 
  ShieldCheck, 
  Clock, 
  TrendingUp, 
  CheckCircle, 
  ArrowRight,
  FileSearch,
  Award,
  Zap
} from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import OpportunityCard from '../components/OpportunityCard';

const LandingPage = () => {
  const [recentOpps, setRecentOpps] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRecent = async () => {
      try {
        const res = await api.get(API_ENDPOINTS.OPPORTUNITIES_RECENT);
        if (res.data.success) {
          setRecentOpps(res.data.data.slice(0, 3));
        }
      } catch (err) {
        console.error('Failed to load recent jobs:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchRecent();
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '4rem' }}>
      {/* Hero Section */}
      <section style={{
        textAlign: 'center',
        padding: '4rem 1rem 2rem',
        maxWidth: '900px',
        margin: '0 auto',
      }}>
        <div style={{
          display: 'inline-flex',
          alignItems: 'center',
          gap: '0.5rem',
          padding: '0.375rem 1rem',
          borderRadius: '9999px',
          background: 'rgba(99, 102, 241, 0.15)',
          color: '#818cf8',
          fontSize: '0.85rem',
          fontWeight: 700,
          marginBottom: '1.5rem',
          border: '1px solid rgba(99, 102, 241, 0.3)'
        }}>
          <Sparkles size={16} /> AI-Powered Career Intelligence Platform
        </div>

        <h1 style={{
          fontSize: 'clamp(2.5rem, 5vw, 3.75rem)',
          fontWeight: 800,
          lineHeight: 1.15,
          letterSpacing: '-0.03em',
          marginBottom: '1.5rem',
          color: '#f8fafc'
        }}>
          Land Your Dream Internship with <span style={{
            background: 'linear-gradient(135deg, #6366f1, #a855f7)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent'
          }}>Authentic AI Matching</span>
        </h1>

        <p style={{
          fontSize: '1.15rem',
          color: 'var(--text-secondary)',
          lineHeight: 1.6,
          marginBottom: '2.5rem',
          maxWidth: '720px',
          margin: '0 auto 2.5rem'
        }}>
          Upload your resume for instant ATS analysis, extract deep technical skills, and match with verified opportunities from premier technology companies. Zero consultancies. Zero fake jobs.
        </p>

        <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
          <Link to="/register" className="btn btn-primary" style={{ padding: '0.875rem 2rem', fontSize: '1rem' }}>
            Get Started Free <ArrowRight size={18} />
          </Link>
          <Link to="/opportunities" className="btn btn-secondary" style={{ padding: '0.875rem 2rem', fontSize: '1rem' }}>
            Browse Opportunities
          </Link>
        </div>
      </section>

      {/* Feature Highlights Grid */}
      <section style={{ maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
        <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
          <h2 style={{ fontSize: '1.85rem', fontWeight: 700, marginBottom: '0.5rem' }}>
            Engineered for Modern Engineering Students
          </h2>
          <p style={{ color: 'var(--text-muted)' }}>
            From PDF text parsing to official enterprise application tracking
          </p>
        </div>

        <div className="grid-3">
          <div className="card">
            <div style={{
              width: '48px', height: '48px', borderRadius: '12px',
              backgroundColor: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary)',
              display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem'
            }}>
              <FileSearch size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Deep ATS Resume Parser
            </h3>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Uses Apache PDFBox and NLP heuristics to extract skills, education, graduation batches, and projects, providing a 0-100 ATS readiness score with actionable advice.
            </p>
          </div>

          <div className="card">
            <div style={{
              width: '48px', height: '48px', borderRadius: '12px',
              backgroundColor: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)',
              display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem'
            }}>
              <ShieldCheck size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Anti-Scam Verification
            </h3>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Filters out consultancies, paid internships, and fraudulent recruiters. Only approved domains and verified enterprise ATS application portals are shown.
            </p>
          </div>

          <div className="card">
            <div style={{
              width: '48px', height: '48px', borderRadius: '12px',
              backgroundColor: 'rgba(139, 92, 246, 0.15)', color: 'var(--secondary)',
              display: 'flex', alignItems: 'center', justifyContent: 'center', marginBottom: '1.25rem'
            }}>
              <Clock size={24} />
            </div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Hourly Sync Engine
            </h3>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              Automated scheduler polls configured official sources every hour (Asia/Kolkata timezone), automatically detecting new postings, updating criteria, and marking expired listings.
            </p>
          </div>
        </div>
      </section>

      {/* Live Featured Opportunities */}
      {recentOpps.length > 0 && (
        <section style={{ maxWidth: '1200px', margin: '0 auto', width: '100%' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h2 style={{ fontSize: '1.5rem', fontWeight: 700 }}>Recently Verified Openings</h2>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Updated via automated scheduler</p>
            </div>
            <Link to="/opportunities" className="btn btn-outline btn-sm">
              View All Openings <ArrowRight size={14} />
            </Link>
          </div>

          <div className="grid-3">
            {recentOpps.map((opp) => (
              <OpportunityCard key={opp.id} opportunity={opp} />
            ))}
          </div>
        </section>
      )}
    </div>
  );
};

export default LandingPage;
