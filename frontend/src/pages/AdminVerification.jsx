import React, { useState, useEffect } from 'react';
import { ShieldAlert, CheckCircle, XCircle, AlertTriangle, ExternalLink } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const AdminVerification = () => {
  const [reviewList, setReviewList] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchReviewQueue = async () => {
    setLoading(true);
    try {
      const res = await api.get(API_ENDPOINTS.OPPORTUNITIES);
      if (res.data.success) {
        // Filter those requiring review or flagged
        const flagged = res.data.data.filter(
          (o) => o.verificationStatus === 'REVIEW' || o.verificationStatus === 'REJECTED'
        );
        setReviewList(flagged);
      }
    } catch (err) {
      console.error('Error fetching verification queue:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReviewQueue();
  }, []);

  const handleApprove = async (id) => {
    try {
      await api.put(API_ENDPOINTS.ADMIN_OPPORTUNITY_VERIFY(id));
      fetchReviewQueue();
    } catch (err) {
      console.error('Error approving:', err);
    }
  };

  const handleReject = async (id) => {
    try {
      await api.put(API_ENDPOINTS.ADMIN_OPPORTUNITY_REJECT(id));
      fetchReviewQueue();
    } catch (err) {
      console.error('Error rejecting:', err);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Anti-Fraud & Verification Review Queue
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Inspect flagged opportunities, suspicious URL patterns, or unverified employer submissions
        </p>
      </div>

      {loading ? (
        <div className="card skeleton" style={{ height: '250px' }} />
      ) : reviewList.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {reviewList.map((opp) => (
            <div key={opp.id} className="card" style={{ borderColor: 'rgba(245, 158, 11, 0.4)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
                    <span className="badge badge-review">
                      <AlertTriangle size={12} /> {opp.verificationStatus}
                    </span>
                    <span style={{ fontWeight: 700, color: 'var(--text-primary)' }}>
                      {opp.companyName}
                    </span>
                  </div>

                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700, color: '#f1f5f9' }}>
                    {opp.title}
                  </h3>

                  <div style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                    Apply Link: <a href={opp.applyUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>{opp.applyUrl}</a>
                  </div>
                </div>

                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <button onClick={() => handleApprove(opp.id)} className="btn btn-sm" style={{ backgroundColor: 'var(--success-bg)', color: 'var(--success)' }}>
                    <CheckCircle size={15} /> Approve as Legitimate
                  </button>
                  <button onClick={() => handleReject(opp.id)} className="btn btn-sm btn-danger">
                    <XCircle size={15} /> Permanently Reject
                  </button>
                </div>
              </div>

              <div style={{
                marginTop: '1rem',
                padding: '0.75rem 1rem',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: '#1c2233',
                fontSize: '0.85rem',
                color: '#cbd5e1'
              }}>
                <strong>Verification Diagnostics:</strong> URL host evaluated against trusted ATS whitelist. Reviewing for unverified payment demands or agency indicators.
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card empty-state">
          <ShieldAlert className="empty-state-icon" style={{ color: 'var(--success)' }} />
          <h3>Verification Queue is Empty</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            All active opportunities have passed the automated fraud heuristics and enterprise domain verification.
          </p>
        </div>
      )}
    </div>
  );
};

export default AdminVerification;
