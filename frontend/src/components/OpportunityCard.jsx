import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  Building2, 
  MapPin, 
  Calendar, 
  ExternalLink, 
  Bookmark, 
  CheckCircle, 
  Sparkles, 
  Clock, 
  Banknote,
  Send
} from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const OpportunityCard = ({ 
  opportunity, 
  matchData = null, 
  onSaveToggle = null,
  isSavedInitially = false 
}) => {
  const [saved, setSaved] = useState(isSavedInitially);
  const [saving, setSaving] = useState(false);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [applied, setApplied] = useState(false);
  const [notes, setNotes] = useState('');

  const oppId = opportunity?.id || (matchData && (matchData.opportunityId || matchData.internshipId));
  const companyName = opportunity?.companyName || (typeof opportunity?.company === 'string' ? opportunity.company : opportunity?.company?.name) || (matchData && (matchData.company || matchData.companyName)) || 'Tech Enterprise';
  const domain = opportunity?.companyDomain || (opportunity?.company && opportunity.company.officialDomain) || (matchData && matchData.companyDomain) || '';
  const title = opportunity?.title || (matchData && matchData.title) || 'Software Engineer Intern';
  const location = opportunity?.location || (matchData && matchData.location) || 'India';
  const workType = opportunity?.workType || opportunity?.workMode || (matchData && (matchData.workType || matchData.workMode)) || 'HYBRID';
  const type = opportunity?.type || opportunity?.jobType || (matchData && (matchData.type || matchData.jobType)) || 'INTERNSHIP';
  const stipend = opportunity?.stipend || opportunity?.salary || (matchData && (matchData.stipend || matchData.salary));
  const applyUrl = opportunity?.applyUrl || (matchData && matchData.applyUrl);

  const matchScore = matchData ? matchData.matchPercentage : null;
  const matchLevel = matchData ? matchData.matchLevel : null;
  const matchedSkills = matchData ? matchData.matchedSkills : [];
  const missingSkills = matchData ? matchData.missingSkills : [];

  const handleSave = async (e) => {
    e.preventDefault();
    if (saving) return;
    setSaving(true);
    try {
      if (saved) {
        await api.delete(API_ENDPOINTS.SAVE_OPPORTUNITY(oppId));
        setSaved(false);
        if (onSaveToggle) onSaveToggle(oppId, false);
      } else {
        await api.post(API_ENDPOINTS.SAVE_OPPORTUNITY(oppId));
        setSaved(true);
        if (onSaveToggle) onSaveToggle(oppId, true);
      }
    } catch (err) {
      console.error('Error saving opportunity:', err);
    } finally {
      setSaving(false);
    }
  };

  const handleApplyClick = () => {
    // Open official application portal in new tab
    if (applyUrl) {
      window.open(applyUrl, '_blank', 'noopener,noreferrer');
    }
    // Prompt tracker modal
    setShowApplyModal(true);
  };

  const handleConfirmApplication = async () => {
    try {
      await api.post(API_ENDPOINTS.APPLICATIONS, {
        opportunityId: oppId,
        notes: notes || 'Applied via official career portal',
      });
      setApplied(true);
      setShowApplyModal(false);
    } catch (err) {
      console.error('Error recording application:', err);
      setShowApplyModal(false);
    }
  };

  return (
    <div className="card" style={{ position: 'relative', display: 'flex', flexDirection: 'column', height: '100%' }}>
      {/* Header: Company & Badges */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.875rem' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontWeight: 700, fontSize: '1.05rem', color: 'var(--text-primary)' }}>
              {companyName}
            </span>
            <span className="badge badge-verified" title="Verified official company domain">
              <CheckCircle size={12} /> Verified
            </span>
          </div>
          {domain && (
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              {domain}
            </span>
          )}
        </div>

        <button 
          onClick={handleSave} 
          disabled={saving}
          style={{
            background: saved ? 'var(--primary-light)' : 'transparent',
            border: 'none',
            color: saved ? 'var(--primary)' : 'var(--text-muted)',
            cursor: 'pointer',
            padding: '6px',
            borderRadius: '6px'
          }}
          title={saved ? 'Remove Bookmark' : 'Save Opportunity'}
        >
          <Bookmark size={20} fill={saved ? 'var(--primary)' : 'none'} />
        </button>
      </div>

      {/* Role Title */}
      <h3 style={{ fontSize: '1.15rem', fontWeight: 700, marginBottom: '0.75rem', color: '#f1f5f9' }}>
        {title}
      </h3>

      {/* Details Row: Location, WorkType, Stipend */}
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', marginBottom: '1.25rem', fontSize: '0.825rem', color: 'var(--text-secondary)' }}>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <MapPin size={15} color="var(--primary)" /> {location}
        </span>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <Building2 size={15} color="#8b5cf6" /> {workType}
        </span>
        <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
          <Clock size={15} color="#10b981" /> {type.replace('_', ' ')}
        </span>
        {stipend && (
          <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontWeight: 600, color: 'var(--text-primary)' }}>
            <Banknote size={15} color="#f59e0b" /> {stipend}
          </span>
        )}
      </div>

      {/* AI Match Metrics Bar if available */}
      {matchScore !== null && (
        <div style={{
          backgroundColor: '#162032',
          border: '1px solid #24324f',
          borderRadius: 'var(--radius-md)',
          padding: '0.875rem',
          marginBottom: '1.25rem',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
            <span style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-primary)' }}>
              <Sparkles size={16} color="#818cf8" /> AI Match: {matchScore}%
            </span>
            <span style={{
              fontSize: '0.75rem',
              fontWeight: 700,
              padding: '2px 8px',
              borderRadius: '9999px',
              backgroundColor: matchScore >= 80 ? 'rgba(16, 185, 129, 0.2)' : 'rgba(99, 102, 241, 0.2)',
              color: matchScore >= 80 ? '#34d399' : '#818cf8'
            }}>
              {matchLevel}
            </span>
          </div>

          {/* Matched & Missing Skills tags */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem', marginTop: '0.5rem' }}>
            {matchedSkills.length > 0 && (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem', alignItems: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Matched:</span>
                {matchedSkills.slice(0, 4).map((s, idx) => (
                  <span key={idx} className="skill-chip matched">{s}</span>
                ))}
              </div>
            )}
            {missingSkills.length > 0 && (
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem', alignItems: 'center' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Missing:</span>
                {missingSkills.slice(0, 3).map((s, idx) => (
                  <span key={idx} className="skill-chip missing">{s}</span>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Action Footer */}
      <div style={{ marginTop: 'auto', display: 'flex', gap: '0.75rem', paddingTop: '1rem', borderTop: '1px solid #1f293d' }}>
        <button 
          onClick={handleApplyClick} 
          className="btn btn-primary"
          style={{ flex: 1, padding: '0.55rem 1rem' }}
        >
          <span>Apply Now</span>
          <ExternalLink size={16} />
        </button>
        <Link 
          to={`/opportunities/${oppId}`} 
          className="btn btn-secondary"
          style={{ padding: '0.55rem 0.875rem' }}
        >
          Details
        </Link>
      </div>

      {/* Tracker Modal */}
      {showApplyModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          height: '100vh',
          background: 'rgba(0,0,0,0.7)',
          backdropFilter: 'blur(4px)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 100
        }}>
          <div className="card" style={{ maxWidth: '450px', width: '90%', padding: '2rem' }}>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '0.75rem' }}>
              Track Your Application
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-secondary)', marginBottom: '1.25rem' }}>
              We opened the official portal for <strong>{companyName}</strong>. Would you like to record this application in your InternMatch dashboard?
            </p>
            <div className="form-group">
              <label className="form-label">Application Notes (Optional)</label>
              <textarea 
                className="form-textarea" 
                rows="2"
                placeholder="e.g. Applied via Workday, submitted resume v2"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
            </div>
            <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
              <button onClick={() => setShowApplyModal(false)} className="btn btn-secondary">
                Skip for now
              </button>
              <button onClick={handleConfirmApplication} className="btn btn-primary">
                <Send size={16} /> Mark as Applied
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OpportunityCard;
