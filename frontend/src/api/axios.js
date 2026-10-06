import axios from 'axios';

// Ensure baseURL never fails even if VITE_API_URL is omitted or has a trailing slash
const rawUrl = import.meta.env.VITE_API_URL || 'https://internmatch-backend-jyk3.onrender.com';
const baseURL = rawUrl ? (rawUrl.endsWith('/') ? rawUrl.slice(0, -1) : rawUrl) : '';

const api = axios.create({
  baseURL: baseURL,
  timeout: 30000, // 30s timeout for cloud cold starts
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle Unauthorized
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token if expired or unauthorized
      if (!window.location.pathname.includes('/login') && !window.location.pathname.includes('/register')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
