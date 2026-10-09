import React, { useState, useEffect } from 'react';
import { Search, Filter, Briefcase, MapPin, Building, RotateCcw } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';
import OpportunityCard from '../components/OpportunityCard';

const Opportunities = () => {
  const [opportunities, setOpportunities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [workType, setWorkType] = useState('');
  const [type, setType] = useState('');
  const [location, setLocation] = useState('');

  const fetchOpportunities = async () => {
    setLoading(true);
    try {
      let endpoint = API_ENDPOINTS.OPPORTUNITIES;
      const params = new URLSearchParams();

      if (searchQuery.trim()) {
        endpoint = API_ENDPOINTS.OPPORTUNITIES_SEARCH;
        params.append('query', searchQuery.trim());
      } else if (workType || type || location) {
        endpoint = API_ENDPOINTS.OPPORTUNITIES_FILTER;
        if (workType) params.append('workType', workType);
        if (type) params.append('type', type);
        if (location) params.append('location', location);
      }

      const url = params.toString() ? `${endpoint}?${params.toString()}` : endpoint;
      const res = await api.get(url);
      const list = res.data?.data || (Array.isArray(res.data) ? res.data : (res.data?.content || []));
      setOpportunities(Array.isArray(list) ? list : []);
    } catch (err) {
      console.error('Error fetching opportunities:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOpportunities();
  }, [workType, type]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    fetchOpportunities();
  };

  const resetFilters = () => {
    setSearchQuery('');
    setWorkType('');
    setType('');
    setLocation('');
    fetchOpportunities();
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
          Explore Verified Opportunities
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
          Only authentic enterprise career openings and verified partner programs
        </p>
      </div>

      {/* Search and Filters Bar */}
      <div className="card" style={{ padding: '1.25rem' }}>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <div style={{ flex: 1, minWidth: '240px', position: 'relative' }}>
            <input
              type="text"
              className="form-input"
              placeholder="Search by role, company, or skills (e.g. Python, Java, React)..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div style={{ width: '160px' }}>
            <select
              className="form-select"
              value={workType}
              onChange={(e) => setWorkType(e.target.value)}
            >
              <option value="">Work Arrangement</option>
              <option value="REMOTE">Remote</option>
              <option value="HYBRID">Hybrid</option>
              <option value="ONSITE">Onsite</option>
            </select>
          </div>

          <div style={{ width: '150px' }}>
            <select
              className="form-select"
              value={type}
              onChange={(e) => setType(e.target.value)}
            >
              <option value="">Job Type</option>
              <option value="INTERNSHIP">Internship</option>
              <option value="PPO">PPO</option>
              <option value="FULL_TIME">Full Time</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary" style={{ padding: '0.625rem 1.25rem' }}>
            <Search size={18} /> Search
          </button>

          {(searchQuery || workType || type || location) && (
            <button type="button" onClick={resetFilters} className="btn btn-secondary" style={{ padding: '0.625rem 1rem' }}>
              <RotateCcw size={16} /> Reset
            </button>
          )}
        </form>
      </div>

      {/* Opportunities Grid */}
      {loading ? (
        <div className="grid-3">
          {[1, 2, 3, 4, 5, 6].map((n) => (
            <div key={n} className="card skeleton" style={{ height: '260px' }} />
          ))}
        </div>
      ) : opportunities.length > 0 ? (
        <div className="grid-3">
          {opportunities.map((opp) => (
            <OpportunityCard key={opp.id} opportunity={opp} />
          ))}
        </div>
      ) : (
        <div className="card empty-state">
          <Briefcase className="empty-state-icon" />
          <h3>No opportunities found matching your criteria</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            Try broadening your search terms or clearing selected filters.
          </p>
        </div>
      )}
    </div>
  );
};

export default Opportunities;
