import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogIn, AlertCircle, Key, Shield } from 'lucide-react';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const authData = await login(email, password);
      if (authData.role === 'ADMIN') {
        navigate('/admin');
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  const fillAdminDemo = () => {
    setEmail('admin@internmatch.com');
    setPassword('Admin@123');
  };

  return (
    <div style={{ maxWidth: '440px', margin: '3rem auto', width: '100%' }}>
      <div className="card">
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '0.5rem' }}>
            Welcome Back
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Sign in to access your ATS score, matches, and applications
          </p>
        </div>

        {error && (
          <div style={{
            display: 'flex', alignItems: 'center', gap: '0.5rem',
            padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--danger-bg)', color: 'var(--danger)',
            fontSize: '0.85rem', marginBottom: '1.25rem'
          }}>
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Email Address</label>
            <input
              type="email"
              className="form-input"
              placeholder="e.g. tilak@example.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              className="form-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', padding: '0.75rem', marginTop: '0.5rem' }}
            disabled={loading}
          >
            <LogIn size={18} />
            <span>{loading ? 'Authenticating...' : 'Sign In'}</span>
          </button>
        </form>

        {/* Quick Demo Credentials Box */}
        <div style={{
          marginTop: '1.5rem',
          padding: '0.875rem',
          borderRadius: 'var(--radius-md)',
          backgroundColor: '#162032',
          border: '1px solid #24324f',
          fontSize: '0.8rem',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
            <span style={{ fontWeight: 600, color: 'var(--text-secondary)' }}>Demo Admin Account:</span>
            <button 
              type="button" 
              onClick={fillAdminDemo}
              className="btn btn-outline btn-sm"
              style={{ padding: '2px 8px', fontSize: '0.75rem' }}
            >
              Autofill
            </button>
          </div>
          <div style={{ color: 'var(--text-muted)' }}>
            <code>admin@internmatch.com</code> / <code>Admin@123</code>
          </div>
        </div>

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
            Register as Student
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
