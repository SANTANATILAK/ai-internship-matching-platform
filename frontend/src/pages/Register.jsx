import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { UserPlus, AlertCircle } from 'lucide-react';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    phone: '',
    branch: 'Computer Science and Engineering',
    college: '',
    graduationYear: 2026,
    location: '',
    preferredLocation: 'Bengaluru',
    preferredWorkType: 'HYBRID',
    rolePreference: 'Software Engineering Intern',
  });

  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'graduationYear' ? parseInt(value) || '' : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    try {
      const { confirmPassword, ...registerPayload } = formData;
      await register(registerPayload);
      navigate('/resume');
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'Registration failed';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '650px', margin: '2rem auto', width: '100%' }}>
      <div className="card">
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h2 style={{ fontSize: '1.75rem', fontWeight: 800, marginBottom: '0.5rem' }}>
            Create Student Account
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Set up your profile to match with verified opportunities and analyze your resume
          </p>
        </div>

        {error && (
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '0.85rem 1rem', borderRadius: 'var(--radius-md)',
            backgroundColor: 'rgba(239, 68, 68, 0.12)', border: '1px solid rgba(239, 68, 68, 0.3)',
            color: '#f87171', fontSize: '0.875rem', marginBottom: '1.25rem'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <AlertCircle size={18} style={{ flexShrink: 0 }} />
              <span>{error}</span>
            </div>
            {error.toLowerCase().includes('already') && (
              <Link to="/login" style={{ color: '#38bdf8', fontWeight: 600, textDecoration: 'underline', marginLeft: '0.75rem', whiteSpace: 'nowrap' }}>
                Sign In &rarr;
              </Link>
            )}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Full Name *</label>
              <input
                type="text"
                name="name"
                className="form-input"
                placeholder="e.g. Tilak Kumar"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email Address *</label>
              <input
                type="email"
                name="email"
                className="form-input"
                placeholder="tilak@example.com"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Password *</label>
              <input
                type="password"
                name="password"
                className="form-input"
                placeholder="Min 6 characters"
                value={formData.password}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Confirm Password *</label>
              <input
                type="password"
                name="confirmPassword"
                className="form-input"
                placeholder="Re-enter password"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Phone Number</label>
            <input
              type="text"
              name="phone"
              className="form-input"
              placeholder="+91-9876543210"
              value={formData.phone}
              onChange={handleChange}
            />
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">College / University *</label>
              <input
                type="text"
                name="college"
                className="form-input"
                placeholder="e.g. National Institute of Technology"
                value={formData.college}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Branch / Department *</label>
              <input
                type="text"
                name="branch"
                className="form-input"
                placeholder="e.g. Computer Science, AI, ECE"
                value={formData.branch}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="grid-3">
            <div className="form-group">
              <label className="form-label">Graduation Year *</label>
              <select
                name="graduationYear"
                className="form-select"
                value={formData.graduationYear}
                onChange={handleChange}
                required
              >
                <option value={2025}>2025</option>
                <option value={2026}>2026</option>
                <option value={2027}>2027</option>
                <option value={2028}>2028</option>
                <option value={2029}>2029</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Preferred Work Type</label>
              <select
                name="preferredWorkType"
                className="form-select"
                value={formData.preferredWorkType}
                onChange={handleChange}
              >
                <option value="REMOTE">Remote</option>
                <option value="HYBRID">Hybrid</option>
                <option value="ONSITE">Onsite</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Target Role</label>
              <input
                type="text"
                name="rolePreference"
                className="form-input"
                placeholder="e.g. AI / SWE Intern"
                value={formData.rolePreference}
                onChange={handleChange}
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', padding: '0.875rem', marginTop: '1rem' }}
            disabled={loading}
          >
            <UserPlus size={18} />
            <span>{loading ? 'Creating Account...' : 'Complete Registration'}</span>
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600, textDecoration: 'none' }}>
            Sign In
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
