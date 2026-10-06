import React, { useState, useEffect } from 'react';
import { Briefcase, Plus, CheckCircle, XCircle, Trash2, ExternalLink } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const AdminOpportunities = () => {
  const [opportunities, setOpportunities] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [formData, setFormData] = useState({
    companyId: '',
    title: '',
    description: '',
    location: 'Bengaluru / Hybrid',
    workType: 'HYBRID',
    type: 'INTERNSHIP',
    stipend: 'INR 1,00,000 / month',
    requiredSkills: 'Java, Spring Boot, MySQL, Git',
    graduationYears: '2026, 2027',
    degreeRequirements: 'B.Tech / B.E. / MCA',
    branchRequirements: 'CSE, IT, ECE',
    experienceRequirement: 'Fresher',
    applyUrl: '',
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [oppRes, compRes] = await Promise.all([
        api.get(API_ENDPOINTS.OPPORTUNITIES),
        api.get(API_ENDPOINTS.COMPANIES),
      ]);
      if (oppRes.data.success) {
        setOpportunities(oppRes.data.data);
      }
      if (compRes.data.success) {
        setCompanies(compRes.data.data);
        if (compRes.data.data.length > 0 && !formData.companyId) {
          setFormData((prev) => ({ ...prev, companyId: compRes.data.data[0].id }));
        }
      }
    } catch (err) {
      console.error('Error fetching admin opportunities:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleVerify = async (id) => {
    try {
      await api.put(API_ENDPOINTS.ADMIN_OPPORTUNITY_VERIFY(id));
      fetchData();
    } catch (err) {
      console.error('Error verifying opportunity:', err);
    }
  };

  const handleReject = async (id) => {
    try {
      await api.put(API_ENDPOINTS.ADMIN_OPPORTUNITY_REJECT(id));
      fetchData();
    } catch (err) {
      console.error('Error rejecting opportunity:', err);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      const res = await api.post(API_ENDPOINTS.ADMIN_OPPORTUNITIES, formData);
      if (res.data.success) {
        setShowAddModal(false);
        fetchData();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Error creating opportunity');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
            Opportunities Governance
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Review, curate, and post authentic opportunities across verified enterprise sources
          </p>
        </div>

        <button onClick={() => setShowAddModal(true)} className="btn btn-primary btn-sm">
          <Plus size={16} /> Post Opportunity
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.85rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-color)', backgroundColor: '#131b2c', color: 'var(--text-muted)' }}>
                <th style={{ padding: '1rem 1.25rem' }}>Title & Role</th>
                <th style={{ padding: '1rem 1.25rem' }}>Company</th>
                <th style={{ padding: '1rem 1.25rem' }}>Type</th>
                <th style={{ padding: '1rem 1.25rem' }}>Eligibility</th>
                <th style={{ padding: '1rem 1.25rem' }}>Verification</th>
                <th style={{ padding: '1rem 1.25rem' }}>Status</th>
                <th style={{ padding: '1rem 1.25rem', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {opportunities.map((opp) => (
                <tr key={opp.id} style={{ borderBottom: '1px solid #1a2234' }}>
                  <td style={{ padding: '1rem 1.25rem', fontWeight: 700, color: 'var(--text-primary)' }}>
                    {opp.title}
                  </td>
                  <td style={{ padding: '1rem 1.25rem', color: 'var(--text-secondary)' }}>
                    {opp.companyName}
                  </td>
                  <td style={{ padding: '1rem 1.25rem' }}>
                    <span className="skill-chip">{opp.type}</span>
                  </td>
                  <td style={{ padding: '1rem 1.25rem', color: 'var(--text-muted)' }}>
                    {opp.graduationYears || 'All batches'}
                  </td>
                  <td style={{ padding: '1rem 1.25rem' }}>
                    <span className={`badge ${opp.verificationStatus === 'VERIFIED' ? 'badge-verified' : 'badge-review'}`}>
                      {opp.verificationStatus}
                    </span>
                  </td>
                  <td style={{ padding: '1rem 1.25rem' }}>
                    <span style={{ fontWeight: 600, color: opp.status === 'ACTIVE' ? 'var(--success)' : 'var(--danger)' }}>
                      {opp.status}
                    </span>
                  </td>
                  <td style={{ padding: '1rem 1.25rem', textAlign: 'right' }}>
                    <div style={{ display: 'inline-flex', gap: '0.5rem' }}>
                      {opp.verificationStatus !== 'VERIFIED' && (
                        <button onClick={() => handleVerify(opp.id)} className="btn btn-sm" style={{ backgroundColor: 'var(--success-bg)', color: 'var(--success)' }}>
                          Verify
                        </button>
                      )}
                      <button onClick={() => handleReject(opp.id)} className="btn btn-sm btn-danger">
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

      {/* Post Opportunity Modal */}
      {showAddModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh',
          background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100
        }}>
          <div className="card" style={{ maxWidth: '600px', width: '90%', maxHeight: '90vh', overflowY: 'auto', padding: '2rem' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '1.25rem' }}>
              Post Verified Opportunity
            </h3>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label className="form-label">Company *</label>
                <select
                  className="form-select"
                  required
                  value={formData.companyId}
                  onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                >
                  {companies.map((c) => (
                    <option key={c.id} value={c.id}>{c.name} ({c.officialDomain})</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Role Title *</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  placeholder="e.g. AI / ML Research Intern"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                />
              </div>

              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Job Type</label>
                  <select
                    className="form-select"
                    value={formData.type}
                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  >
                    <option value="INTERNSHIP">Internship</option>
                    <option value="PPO">PPO</option>
                    <option value="FULL_TIME">Full Time</option>
                  </select>
                </div>

                <div className="form-group">
                  <label className="form-label">Work Arrangement</label>
                  <select
                    className="form-select"
                    value={formData.workType}
                    onChange={(e) => setFormData({ ...formData, workType: e.target.value })}
                  >
                    <option value="REMOTE">Remote</option>
                    <option value="HYBRID">Hybrid</option>
                    <option value="ONSITE">Onsite</option>
                  </select>
                </div>
              </div>

              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Location</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.location}
                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Stipend / Salary</label>
                  <input
                    type="text"
                    className="form-input"
                    value={formData.stipend}
                    onChange={(e) => setFormData({ ...formData, stipend: e.target.value })}
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Required Skills (Comma separated)</label>
                <input
                  type="text"
                  className="form-input"
                  value={formData.requiredSkills}
                  onChange={(e) => setFormData({ ...formData, requiredSkills: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Eligible Graduation Years</label>
                <input
                  type="text"
                  className="form-input"
                  placeholder="e.g. 2026, 2027, 2028"
                  value={formData.graduationYears}
                  onChange={(e) => setFormData({ ...formData, graduationYears: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Official Apply URL *</label>
                <input
                  type="url"
                  className="form-input"
                  required
                  placeholder="https://company.com/careers/job"
                  value={formData.applyUrl}
                  onChange={(e) => setFormData({ ...formData, applyUrl: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" onClick={() => setShowAddModal(false)} className="btn btn-secondary">
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Publish Opportunity
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminOpportunities;
