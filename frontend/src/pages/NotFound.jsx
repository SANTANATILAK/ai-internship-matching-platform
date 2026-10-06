import React from 'react';
import { Link } from 'react-router-dom';
import { AlertCircle, ArrowLeft } from 'lucide-react';

const NotFound = () => {
  return (
    <div style={{ maxWidth: '500px', margin: '4rem auto', textAlign: 'center' }}>
      <div className="card">
        <div style={{
          width: '64px', height: '64px', borderRadius: '50%',
          backgroundColor: 'var(--danger-bg)', color: 'var(--danger)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1.5rem'
        }}>
          <AlertCircle size={32} />
        </div>
        <h1 style={{ fontSize: '2.5rem', fontWeight: 800, marginBottom: '0.5rem' }}>404</h1>
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.75rem' }}>Page Not Found</h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1.75rem' }}>
          The requested page could not be located or has been moved to a new route.
        </p>
        <Link to="/" className="btn btn-primary" style={{ display: 'inline-flex', padding: '0.75rem 1.5rem' }}>
          <ArrowLeft size={16} /> Return to Homepage
        </Link>
      </div>
    </div>
  );
};

export default NotFound;
