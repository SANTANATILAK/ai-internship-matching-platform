import React, { useState, useEffect } from 'react';
import { Building, Plus, CheckCircle, XCircle, ShieldCheck, Globe, ExternalLink } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const AdminCompanies = () => {
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    officialDomain: '',
    careerUrl: '',
    description: '',
    industry: 'Technology',
    location: 'Bengaluru / Hyderabad',
    verified: true,
  });

  const fetchCompanies = async () => {
    setLoading(true);
    try {
      const res = await api.get(API_ENDPOINTS.COMPANIES);
      if (res.data.success) {
        setCompanies(res.data.data);
      }
    } catch (err) {
      console.error('Error fetching companies:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCompanies();
  }, []);

  const handleVerify = async (id) => {
    try {
      const res = await api.put(API_ENDPOINTS.ADMIN_COMPANY_VERIFY(id));
      if (res.data.success) {
        fetchCompanies();
      }
    } catch (err) {
      console.error('Error verifying company:', err);
    }
  };

  const handleReject = async (id) => {
    try {
      const res = await api.put(API_ENDPOINTS.ADMIN_COMPANY_REJECT(id));
      if (res.data.success) {
        fetchCompanies();
      }
    } catch (err) {
      console.error('Error rejecting company:', err);
    }
  };

  const handleCreateCompany = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(API_ENDPOINTS.ADMIN_COMPANIES, formData);
      if (res.data.success) {
        setShowAddModal(false);
        setFormData({
          name: '',
          officialDomain: '',
          careerUrl: '',
          description: '',
          industry: 'Technology',
          location: 'Bengaluru / Hyderabad',
          verified: true,
        });
        fetchCompanies();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Error creating company');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
            Enterprise Directory & Domain Verification
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Manage verified partner companies and official career domain credentials
          </p>
        </div>

        <button onClick={() => setShowAddModal(true)} className="btn btn-primary btn-sm">
          <Plus size={16} /> Add Company
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.875rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', backgroundColor: '#131b2c', color: 'var(--text-muted)' }}>
                <th style={{ padding: '1rem 1.25rem' }}>Company</th>
                <th style={{ padding: '1rem 1.25rem' }}>Official Domain</th>
                <th style={{ padding: '1rem 1.25rem' }}>Career Portal</th>
                <th style={{ padding: '1rem 1.25rem' }}>Trust Score</th>
                <th style={{ padding: '1rem 1.25rem' }}>Status</th>
                <th style={{ padding: '1rem 1.25rem', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {companies.map((comp) => (
                <tr key={comp.id} style={{ borderBottom: '1px solid #1a2234' }}>
                  <td style={{ padding: '1rem 1.25rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                    {comp.name}
                  </td>
                  <td style={{ padding: '1rem 1.25rem', color: 'var(--text-secondary)' }}>
                    <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                      <Globe size={14} color="#818cf8" /> {comp.officialDomain}
                    </span>
                  </td>
                  <td style={{ padding: '1rem 1.25rem' }}>
                    <a href={comp.careerUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)', textDecoration: 'none', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                      <span>Portal</span>
                      <ExternalLink size={13} />
                    </a>
                  </td>
                  <td style={{ padding: '1rem 1.25rem', fontWeight: 700, color: '#34d399' }}>
                    {comp.trustScore || 90}/100
                  </td>
                  <td style={{ padding: '1rem 1.25rem' }}>
                    <span className={`badge ${comp.verified ? 'badge-verified' : 'badge-review'}`}>
                      {comp.verified ? 'VERIFIED' : comp.verificationStatus}
                    </span>
                  </td>
                  <td style={{ padding: '1rem 1.25rem', textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '0.5rem' }}>
                      {!comp.verified && (
                        <button onClick={() => handleVerify(comp.id)} className="btn btn-sm" style={{ backgroundColor: 'var(--success-bg)', color: 'var(--success)' }}>
                          Approve
                        </button>
                      )}
                      <button onClick={() => handleReject(comp.id)} className="btn btn-sm btn-danger">
                        Reject
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Company Modal */}
      {showAddModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh',
          background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100
        }}>
          <div className="card" style={{ maxWidth: '520px', width: '90%', padding: '2rem' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.25rem' }}>
              Add Enterprise Company
            </h3>
            <form onSubmit={handleCreateCompany}>
              <div className="form-group">
                <label className="form-label">Company Name *</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  placeholder="e.g. Adobe"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Official Domain *</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  placeholder="e.g. adobe.com"
                  value={formData.officialDomain}
                  onChange={(e) => setFormData({ ...formData, officialDomain: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Career Portal URL *</label>
                <input
                  type="url"
                  className="form-input"
                  required
                  placeholder="https://adobe.wd5.myworkdayjobs.com/careers"
                  value={formData.careerUrl}
                  onChange={(e) => setFormData({ ...formData, careerUrl: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" onClick={() => setShowAddModal(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Create & Whitelist Company
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminCompanies;
