import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { 
  Building2, 
  MapPin, 
  Clock, 
  Banknote, 
  CheckCircle, 
  ExternalLink, 
  Bookmark, 
  ArrowLeft,
  Sparkles,
  Send,
  Layers,
  GraduationCap
} from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import { useAuth } from '../context/AuthContext';

const OpportunityDetails = () => {
  const { id } = useParams();
  const { user } = useAuth();
  const [opportunity, setOpportunity] = useState(null);
  const [matchData, setMatchData] = useState(null);
  const [saved, setSaved] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [notes, setNotes] = useState('');

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      try {
        const res = await api.get(API_ENDPOINTS.OPPORTUNITY_DETAIL(id));
        if (res.data.success) {
          setOpportunity(res.data.data);
        }

        if (user) {
          // Check if match detail available
          try {
            const mRes = await api.get(API_ENDPOINTS.MATCH_DETAIL(id));
            if (mRes.data.success) {
              setMatchData(mRes.data.data);
            }
          } catch (ignored) {}

          // Check if saved
          try {
            const sRes = await api.get(API_ENDPOINTS.CHECK_SAVED(id));
            if (sRes.data.success) {
              setSaved(sRes.data.data.saved);
            }
          } catch (ignored) {}
        }
      } catch (err) {
        console.error('Error fetching details:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [id, user]);

  const handleSaveToggle = async () => {
    try {
      if (saved) {
        await api.delete(API_ENDPOINTS.SAVE_OPPORTUNITY(id));
        setSaved(false);
      } else {
        await api.post(API_ENDPOINTS.SAVE_OPPORTUNITY(id));
        setSaved(true);
      }
    } catch (err) {
      console.error('Error toggling bookmark:', err);
    }
  };

  const handleApplyClick = () => {
    if (opportunity?.applyUrl) {
      window.open(opportunity.applyUrl, '_blank', 'noopener,noreferrer');
    }
    setShowApplyModal(true);
  };

  const handleConfirmApply = async () => {
    try {
      await api.post(API_ENDPOINTS.APPLICATIONS, {
        opportunityId: id,
        notes: notes || 'Applied via official portal',
      });
      setShowApplyModal(false);
    } catch (err) {
      console.error('Error recording application:', err);
      setShowApplyModal(false);
    }
  };

  if (loading) {
    return (
      <div className="card skeleton" style={{ height: '400px', maxWidth: '900px', margin: '2rem auto' }} />
    );
  }

  if (!opportunity) {
    return (
      <div className="card empty-state" style={{ maxWidth: '600px', margin: '3rem auto' }}>
        <h3>Opportunity Not Found</h3>
        <Link to="/opportunities" className="btn btn-secondary" style={{ marginTop: '1rem' }}>
          Back to Listings
        </Link>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '920px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '1.75rem' }}>
      <Link to="/opportunities" style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-secondary)', textDecoration: 'none', fontSize: '0.9rem' }}>
        <ArrowLeft size={16} /> Back to opportunities
      </Link>

      {/* Main Header Card */}
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.25rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
              <span style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--text-primary)' }}>
                {opportunity.companyName}
              </span>
              <span className="badge badge-verified">
                <CheckCircle size={12} /> Verified Company
              </span>
            </div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.5rem' }}>
              {opportunity.title}
            </h1>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1rem', fontSize: '0.875rem', color: 'var(--text-secondary)' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <MapPin size={16} color="var(--primary)" /> {opportunity.location || 'India'}
              </span>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Building2 size={16} color="#8b5cf6" /> {opportunity.workType}
              </span>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Clock size={16} color="#10b981" /> {opportunity.type.replace('_', ' ')}
              </span>
              {opportunity.stipend && (
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', color: '#f59e0b', fontWeight: 600 }}>
                  <Banknote size={16} /> {opportunity.stipend}
                </span>
              )}
            </div>
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
            <button onClick={handleSaveToggle} className="btn btn-secondary" style={{ padding: '0.625rem 1rem' }}>
              <Bookmark size={18} fill={saved ? 'var(--primary)' : 'none'} color={saved ? 'var(--primary)' : 'currentColor'} />
              <span>{saved ? 'Saved' : 'Save'}</span>
            </button>
            <button onClick={handleApplyClick} className="btn btn-primary" style={{ padding: '0.625rem 1.5rem' }}>
              <span>Apply on Career Portal</span>
              <ExternalLink size={16} />
            </button>
          </div>
        </div>

        {/* AI Match Overview if available */}
        {matchData && (
          <div style={{
            background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.12), rgba(139, 92, 246, 0.08))',
            border: '1px solid #2e3c5d',
            borderRadius: 'var(--radius-md)',
            padding: '1.25rem',
            marginTop: '1rem'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 800, fontSize: '1rem', color: 'var(--text-primary)' }}>
                <Sparkles size={18} color="#818cf8" /> Candidate Fit Score: {matchData.matchPercentage}%
              </span>
              <span className="badge badge-primary">{matchData.matchLevel}</span>
            </div>

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '0.75rem' }}>
              {matchData.matchedSkills.map((s, i) => (
                <span key={i} className="skill-chip matched">✓ {s}</span>
              ))}
              {matchData.missingSkills.map((s, i) => (
                <span key={i} className="skill-chip missing">Missing: {s}</span>
              ))}
            </div>

            <ul style={{ paddingLeft: '1.2rem', fontSize: '0.825rem', color: 'var(--text-secondary)' }}>
              {matchData.matchingReasons.map((r, i) => (
                <li key={i}>{r}</li>
              ))}
            </ul>
          </div>
        )}
      </div>

      {/* Description & Requirements */}
      <div className="card">
        <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' }}>
          Role Overview & Description
        </h2>
        <p style={{ lineHeight: 1.7, color: '#cbd5e1', fontSize: '0.95rem', whiteSpace: 'pre-line', marginBottom: '1.5rem' }}>
          {opportunity.description || 'Join the engineering team to build scalable software solutions and gain hands-on production experience.'}
        </p>

        <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Layers size={18} color="var(--primary)" /> Required Core Skills
        </h3>
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem', marginBottom: '1.5rem' }}>
          {opportunity.requiredSkills ? (
            opportunity.requiredSkills.split(',').map((sk, idx) => (
              <span key={idx} className="skill-chip" style={{ fontSize: '0.825rem', padding: '0.35rem 0.75rem' }}>
                {sk.trim()}
              </span>
            ))
          ) : (
            <span style={{ color: 'var(--text-muted)' }}>Not explicitly specified</span>
          )}
        </div>

        <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <GraduationCap size={18} color="var(--secondary)" /> Eligibility Criteria
        </h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', fontSize: '0.875rem' }}>
          <div>
            <span style={{ color: 'var(--text-muted)' }}>Eligible Batches: </span>
            <strong>{opportunity.graduationYears || 'Open to all engineering batches'}</strong>
          </div>
          <div>
            <span style={{ color: 'var(--text-muted)' }}>Degree Required: </span>
            <strong>{opportunity.degreeRequirements || 'B.Tech / B.E. / MCA'}</strong>
          </div>
          <div>
            <span style={{ color: 'var(--text-muted)' }}>Branches: </span>
            <strong>{opportunity.branchRequirements || 'CSE / IT / ECE / AI'}</strong>
          </div>
        </div>
      </div>

      {/* Tracker Modal */}
      {showApplyModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh',
          background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100
        }}>
          <div className="card" style={{ maxWidth: '450px', width: '90%', padding: '2rem' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.75rem' }}>
              Track Application
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
              Did you submit your application on the official career portal? Save it to track interview stages and progress.
            </p>
            <div className="form-group">
              <label className="form-label">Notes (Optional)</label>
              <textarea 
                className="form-textarea" 
                rows="2"
                placeholder="e.g. Uploaded resume v2, applied for SWE 2026 batch"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
            </div>
            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
              <button onClick={() => setShowApplyModal(false)} className="btn btn-secondary">
                Cancel
              </button>
              <button onClick={handleConfirmApply} className="btn btn-primary">
                <Send size={16} /> Save to Tracker
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OpportunityDetails;
