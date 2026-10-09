import React, { useState, useEffect } from 'react';
import { Bookmark, AlertCircle } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import OpportunityCard from '../components/OpportunityCard';

const SavedOpportunities = () => {
  const [savedItems, setSavedItems] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchSaved = async () => {
    setLoading(true);
    try {
      const res = await api.get(API_ENDPOINTS.SAVED_OPPORTUNITIES);
      const items = res.data?.data || (Array.isArray(res.data) ? res.data : []);
      setSavedItems(Array.isArray(items) ? items : []);
    } catch (err) {
      console.error('Error fetching saved opportunities:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSaved();
  }, []);

  const handleSaveToggle = (oppId, isSaved) => {
    if (!isSaved) {
      setSavedItems((prev) => prev.filter((item) => (item.opportunity?.id || item.id || item.opportunityId) !== oppId));
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Saved Opportunities
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Your bookmarked openings for quick application and deadline tracking
        </p>
      </div>

      {loading ? (
        <div className="grid-3">
          {[1, 2, 3].map((n) => (
            <div key={n} className="card skeleton" style={{ height: '260px' }} />
          ))}
        </div>
      ) : savedItems.length > 0 ? (
        <div className="grid-3">
          {savedItems.map((item) => (
            <OpportunityCard
              key={item.id || item.opportunityId}
              opportunity={item.opportunity || item}
              isSavedInitially={true}
              onSaveToggle={handleSaveToggle}
            />
          ))}
        </div>
      ) : (
        <div className="card empty-state">
          <Bookmark className="empty-state-icon" />
          <h3>No saved opportunities yet</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            Browse through verified listings or AI matches and click the bookmark icon to save.
          </p>
        </div>
      )}
    </div>
  );
};

export default SavedOpportunities;
