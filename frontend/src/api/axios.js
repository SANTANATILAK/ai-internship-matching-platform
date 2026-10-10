import axios from 'axios';

// Determine backend URL dynamically:
// 1. Explicit VITE_API_URL environment variable if set
// 2. When running on localhost / local dev machine: http://localhost:9090
// 3. Cloud / production deployment: https://internmatch-backend-jyk3.onrender.com
const getBaseUrl = () => {
  if (import.meta.env.VITE_API_URL) {
    const custom = import.meta.env.VITE_API_URL;
    return custom.endsWith('/') ? custom.slice(0, -1) : custom;
  }
  if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
    return 'http://localhost:9090';
  }
  return 'https://clothing-cells-setting-daily.trycloudflare.com';
};

const baseURL = getBaseUrl();

const api = axios.create({
  baseURL: baseURL,
  timeout: 30000,
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
