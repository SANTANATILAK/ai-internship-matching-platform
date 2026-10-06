import React from 'react';
import { ShieldCheck, Cpu } from 'lucide-react';

const Footer = () => {
  return (
    <footer style={{
      borderTop: '1px solid var(--border-color)',
      padding: '2.5rem 2rem',
      backgroundColor: '#090d16',
      marginTop: 'auto',
      textAlign: 'center',
      color: 'var(--text-secondary)',
      fontSize: '0.875rem'
    }}>
      <div style={{ maxWidth: '1280px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '1rem', alignItems: 'center' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-primary)', fontWeight: '700' }}>
          <Cpu size={20} color="#6366f1" />
          <span>InternMatch - AI-Powered Internship & Career Matching Platform</span>
        </div>
        <p style={{ maxWidth: '650px', color: 'var(--text-muted)' }}>
          Only authentic enterprise career listings and verified partner programs. Built with Spring Boot 3, MySQL, and React with automated hourly verification against scam listings and paid consultancies.
        </p>
        <div style={{ display: 'flex', gap: '1.5rem', marginTop: '0.5rem' }}>
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: 'var(--success)' }}>
            <ShieldCheck size={16} /> 100% Verified Direct Portal Links
          </span>
          <span>•</span>
          <span>Automatic Hourly Sync (Asia/Kolkata)</span>
          <span>•</span>
          <span>ATS Parser & Scoring Engine</span>
        </div>
        <div style={{ borderTop: '1px solid #1a2234', width: '100%', paddingTop: '1.25rem', marginTop: '0.75rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>
          &copy; {new Date().getFullYear()} InternMatch. All rights reserved.
        </div>
      </div>
    </footer>
  );
};

export default Footer;
