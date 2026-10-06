import React, { useState, useEffect } from 'react';
import { Bell, CheckCheck, ExternalLink, Calendar } from 'lucide-react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = await api.get(API_ENDPOINTS.NOTIFICATIONS);
      if (res.data.success) {
        setNotifications(res.data.data);
      }
    } catch (err) {
      console.error('Error fetching notifications:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, []);

  const handleMarkAsRead = async (id) => {
    try {
      await api.put(API_ENDPOINTS.NOTIFICATION_READ(id));
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, isRead: true } : n))
      );
    } catch (err) {
      console.error('Error marking notification as read:', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await api.put(API_ENDPOINTS.NOTIFICATIONS_READ_ALL);
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch (err) {
      console.error('Error marking all notifications as read:', err);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: '1.85rem', fontWeight: 800, color: '#f8fafc', marginBottom: '0.25rem' }}>
            Alerts & Notifications
          </h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            Updates on matched opportunities, application status changes, and new openings
          </p>
        </div>

        {notifications.some((n) => !n.isRead) && (
          <button onClick={handleMarkAllAsRead} className="btn btn-secondary btn-sm">
            <CheckCheck size={16} /> Mark All as Read
          </button>
        )}
      </div>

      {loading ? (
        <div className="card skeleton" style={{ height: '240px' }} />
      ) : notifications.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.875rem' }}>
          {notifications.map((notif) => (
            <div
              key={notif.id}
              className="card"
              style={{
                padding: '1.25rem',
                backgroundColor: notif.isRead ? 'var(--bg-card)' : 'rgba(99, 102, 241, 0.08)',
                borderColor: notif.isRead ? 'var(--border-color)' : 'rgba(99, 102, 241, 0.35)',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: '1rem'
              }}
            >
              <div>
                <h4 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--text-primary)', marginBottom: '0.25rem' }}>
                  {notif.title}
                </h4>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginBottom: '0.5rem' }}>
                  {notif.message}
                </p>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                  <Calendar size={13} /> {notif.createdAt ? new Date(notif.createdAt).toLocaleDateString() : 'Just now'}
                </div>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                {!notif.isRead && (
                  <button
                    onClick={() => handleMarkAsRead(notif.id)}
                    className="btn btn-secondary btn-sm"
                    style={{ padding: '0.35rem 0.65rem', fontSize: '0.75rem' }}
                  >
                    Mark Read
                  </button>
                )}
                {notif.link && (
                  <a href={notif.link} className="btn btn-outline btn-sm" style={{ padding: '0.35rem 0.65rem' }}>
                    <ExternalLink size={14} />
                  </a>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card empty-state">
          <Bell className="empty-state-icon" />
          <h3>No notifications at this time</h3>
          <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
            We'll notify you when an opportunity with an 85%+ match is discovered!
          </p>
        </div>
      )}
    </div>
  );
};

export default Notifications;
