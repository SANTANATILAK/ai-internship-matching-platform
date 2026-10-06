import React, { useState, useEffect } from 'react';
import { User, CheckCircle, Save, AlertCircle } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

const Profile = () => {
  const { user } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    college: '',
    branch: '',
    graduationYear: 2026,
    location: '',
    preferredLocation: '',
    preferredWorkType: 'HYBRID',
    rolePreference: '',
  });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await api.get(API_ENDPOINTS.PROFILE);
        if (res.data.success) {
          const p = res.data.data;
          setFormData({
            name: p.name || '',
            email: p.email || '',
            phone: p.phone || '',
            college: p.college || '',
            branch: p.branch || '',
            graduationYear: p.graduationYear || 2026,
            location: p.location || '',
            preferredLocation: p.preferredLocation || '',
            preferredWorkType: p.preferredWorkType || 'HYBRID',
            rolePreference: p.rolePreference || '',
          });
        }
      } catch (err) {
        console.error('Error fetching profile:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'graduationYear' ? parseInt(value) || '' : value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMessage('');
    setError('');

    try {
      const res = await api.put(API_ENDPOINTS.PROFILE, formData);
      if (res.data.success) {
        setMessage('Profile updated successfully! Match scores have been recomputed.');
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="card skeleton" style={{ height: '350px', maxWidth: '750px', margin: '2rem auto' }} />;
  }

  return (
    <div style={{ maxWidth: '780px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Student Profile Settings
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Keep your graduation batch and career preferences accurate for optimal matching
        </p>
      </div>

      <div className="card">
        {message && (
          <div style={{
            display: 'flex', alignItems: 'center', gap: '0.5rem',
            padding: '0.75rem 1rem', borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--success-bg)', color: 'var(--success)',
            fontSize: '0.85rem', marginBottom: '1.25rem'
          }}>
            <CheckCircle size={18} />
            <span>{message}</span>
          </div>
        )}

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
          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input
                type="text"
                name="name"
                className="form-input"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Email Address (Read-only)</label>
              <input
                type="email"
                className="form-input"
                value={formData.email}
                disabled
                style={{ opacity: 0.65, cursor: 'not-allowed' }}
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Phone Number</label>
              <input
                type="text"
                name="phone"
                className="form-input"
                value={formData.phone}
                onChange={handleChange}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Current Location</label>
              <input
                type="text"
                name="location"
                className="form-input"
                placeholder="e.g. Hyderabad, Bengaluru"
                value={formData.location}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">College / University</label>
              <input
                type="text"
                name="college"
                className="form-input"
                value={formData.college}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Branch / Department</label>
              <input
                type="text"
                name="branch"
                className="form-input"
                value={formData.branch}
                onChange={handleChange}
                required
              />
            </div>
          </div>

          <div className="grid-3">
            <div className="form-group">
              <label className="form-label">Graduation Year</label>
              <select
                name="graduationYear"
                className="form-select"
                value={formData.graduationYear}
                onChange={handleChange}
              >
                <option value={2025}>2025</option>
                <option value={2026}>2026</option>
                <option value={2027}>2027</option>
                <option value={2028}>2028</option>
                <option value={2029}>2029</option>
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Work Type Preference</label>
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
              <label className="form-label">Preferred Job Location</label>
              <input
                type="text"
                name="preferredLocation"
                className="form-input"
                placeholder="e.g. Bengaluru"
                value={formData.preferredLocation}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Target Role Preference</label>
            <input
              type="text"
              name="rolePreference"
              className="form-input"
              placeholder="e.g. Software Engineer Intern, AI Engineer"
              value={formData.rolePreference}
              onChange={handleChange}
            />
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '1.25rem' }}>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={saving}
              style={{ padding: '0.75rem 1.75rem' }}
            >
              <Save size={18} />
              <span>{saving ? 'Updating...' : 'Save Changes'}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;
