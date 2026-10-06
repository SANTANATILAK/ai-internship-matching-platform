import React, { useState, useEffect } from 'react';
import { Sparkles, Filter, AlertCircle, RefreshCw } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import OpportunityCard from '../components/OpportunityCard';

const Matches = () => {
  const [matches, setMatches] = useState([]);
  const [filteredMatches, setFilteredMatches] = useState([]);
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchMatches = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await api.get(API_ENDPOINTS.MATCHES_TOP);
      if (res.data.success) {
        setMatches(res.data.data);
        setFilteredMatches(res.data.data);
      }
    } catch (err) {
      setError('Could not retrieve matches. Ensure your profile is filled or resume is uploaded.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMatches();
  }, []);

  const handleFilter = (filterKey) => {
    setActiveFilter(filterKey);
    if (filterKey === 'ALL') {
      setFilteredMatches(matches);
    } else if (filterKey === 'EXCELLENT') {
      setFilteredMatches(matches.filter((m) => m.matchPercentage >= 90));
    } else if (filterKey === 'STRONG') {
      setFilteredMatches(matches.filter((m) => m.matchPercentage >= 80 && m.matchPercentage < 90));
    } else if (filterKey === 'GOOD') {
      setFilteredMatches(matches.filter((m) => m.matchPercentage >= 60 && m.matchPercentage < 80));
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
            AI-Powered Opportunity Matches
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Opportunities scored by normalized skill overlap, graduation batch eligibility, and role alignment
          </p>
        </div>

        <button onClick={fetchMatches} className="btn btn-secondary btn-sm" disabled={loading}>
          <RefreshCw size={15} className={loading ? 'spin' : ''} />
          <span>Refresh Matches</span>
        </button>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap' }}>
        <button
          onClick={() => handleFilter('ALL')}
          className={`btn btn-sm ${activeFilter === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
        >
          All Matches ({matches.length})
        </button>
        <button
          onClick={() => handleFilter('EXCELLENT')}
          className={`btn btn-sm ${activeFilter === 'EXCELLENT' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Excellent (90%+)
        </button>
        <button
          onClick={() => handleFilter('STRONG')}
          className={`btn btn-sm ${activeFilter === 'STRONG' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Strong (80-89%)
        </button>
        <button
          onClick={() => handleFilter('GOOD')}
          className={`btn btn-sm ${activeFilter === 'GOOD' ? 'btn-primary' : 'btn-secondary'}`}
        >
          Good (60-79%)
        </button>
      </div>

      {error && (
        <div style={{
          padding: '1rem', borderRadius: 'var(--radius-md)',
          backgroundColor: 'var(--danger-bg)', color: 'var(--danger)', display: 'flex', alignItems: 'center', gap: '0.5rem'
        }}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {loading ? (
        <div className="grid-3">
          {[1, 2, 3].map((n) => (
            <div key={n} className="card skeleton" style={{ height: '280px' }} />
          ))}
        </div>
      ) : filteredMatches.length > 0 ? (
        <div className="grid-3">
          {filteredMatches.map((m) => (
            <OpportunityCard key={m.opportunityId} matchData={m} />
          ))}
        </div>
      ) : (
        <div className="card empty-state">
          <Sparkles className="empty-state-icon" />
          <h3>No matching opportunities in this filter</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            Try updating your resume skills or selecting "All Matches".
          </p>
        </div>
      )}
    </div>
  );
};

export default Matches;
