import React, { useState, useEffect } from 'react';
import { Send, ExternalLink, Calendar, CheckCircle2, Clock, AlertCircle } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const STATUS_COLORS = {
  APPLIED: { bg: 'rgba(99, 102, 241, 0.15)', text: '#818cf8', label: 'Applied' },
  INTERVIEW: { bg: 'rgba(245, 158, 11, 0.15)', text: '#fbbf24', label: 'Interview Scheduled' },
  OFFER: { bg: 'rgba(16, 185, 129, 0.15)', text: '#34d399', label: 'Offer Received' },
  REJECTED: { bg: 'rgba(239, 68, 68, 0.15)', text: '#f87171', label: 'Rejected' },
  WITHDRAWN: { bg: 'rgba(148, 163, 184, 0.15)', text: '#94a3b8', label: 'Withdrawn' },
};

const Applications = () => {
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchApplications = async () => {
    setLoading(true);
    try {
      const res = await api.get(API_ENDPOINTS.USER_APPLICATIONS);
      const apps = res.data?.data || (Array.isArray(res.data) ? res.data : []);
      setApplications(Array.isArray(apps) ? apps : []);
    } catch (err) {
      console.error('Error fetching applications:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchApplications();
  }, []);

  const handleStatusChange = async (appId, newStatus) => {
    try {
      const res = await api.put(API_ENDPOINTS.APPLICATION_STATUS(appId), {
        status: newStatus,
      });
      setApplications((prev) =>
        prev.map((app) => (app.id === appId ? { ...app, status: newStatus } : app))
      );
    } catch (err) {
      console.error('Error updating application status:', err);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Application Tracking Board
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Monitor your active candidacy across company portals and interviews
        </p>
      </div>

      {loading ? (
        <div className="card skeleton" style={{ height: '300px' }} />
      ) : applications.length > 0 ? (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border-color)', backgroundColor: '#131b2c', color: 'var(--text-muted)' }}>
                  <th style={{ padding: '1rem 1.25rem' }}>Company & Role</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Applied On</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Status</th>
                  <th style={{ padding: '1rem 1.25rem' }}>Notes</th>
                  <th style={{ padding: '1rem 1.25rem', textAlign: 'right' }}>Portal Link</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((app) => {
                  const opp = app.opportunity;
                  const statusStyle = STATUS_COLORS[app.status] || STATUS_COLORS.APPLIED;
                  const appliedDate = app.appliedAt ? new Date(app.appliedAt).toLocaleDateString() : 'Recent';

                  return (
                    <tr key={app.id || app.applicationId} style={{ borderBottom: '1px solid #1a2234' }}>
                      <td style={{ padding: '1.25rem' }}>
                        <div style={{ fontWeight: 700, color: 'var(--text-primary)', fontSize: '1rem' }}>
                          {opp?.title || app.title || 'Software Engineering Intern'}
                        </div>
                        <div style={{ fontSize: '0.825rem', color: 'var(--text-muted)' }}>
                          {opp?.company?.name || opp?.company || app.company || 'Enterprise Leader'} • {opp?.location || app.location || 'India'}
                        </div>
                      </td>

                      <td style={{ padding: '1.25rem', color: 'var(--text-secondary)' }}>
                        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                          <Calendar size={14} /> {appliedDate}
                        </span>
                      </td>

                      <td style={{ padding: '1.25rem' }}>
                        <select
                          className="form-select"
                          value={app.status}
                          onChange={(e) => handleStatusChange(app.id || app.applicationId, e.target.value)}
                          style={{
                            backgroundColor: statusStyle.bg,
                            color: statusStyle.text,
                            borderColor: 'transparent',
                            fontWeight: 700,
                            fontSize: '0.8rem',
                            padding: '0.35rem 0.75rem',
                            width: 'auto'
                          }}
                        >
                          <option value="APPLIED">Applied</option>
                          <option value="INTERVIEW">Interview Scheduled</option>
                          <option value="OFFER">Offer Received</option>
                          <option value="REJECTED">Rejected</option>
                          <option value="WITHDRAWN">Withdrawn</option>
                        </select>
                      </td>

                      <td style={{ padding: '1.25rem', color: 'var(--text-secondary)', maxWidth: '240px' }}>
                        <span style={{ fontSize: '0.825rem' }}>
                          {app.notes || '—'}
                        </span>
                      </td>

                      <td style={{ padding: '1.25rem', textAlign: 'right' }}>
                        {(opp?.applyUrl || app.applyUrl) && (
                          <a
                            href={opp?.applyUrl || app.applyUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="btn btn-secondary btn-sm"
                            style={{ padding: '0.35rem 0.75rem' }}
                          >
                            <span>Open</span>
                            <ExternalLink size={14} />
                          </a>
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="card empty-state">
          <Send className="empty-state-icon" />
          <h3>No applications tracked yet</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            When applying to verified opportunities, click "Apply Now" to log them automatically.
          </p>
        </div>
      )}
    </div>
  );
};

export default Applications;
