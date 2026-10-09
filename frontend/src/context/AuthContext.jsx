import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/axios';
import { API_ENDPOINTS } from '../api/endpoints';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      const savedToken = localStorage.getItem('token');
      const savedUser = localStorage.getItem('user');

      if (savedToken && savedUser) {
        try {
          setUser(JSON.parse(savedUser));
          // Verify with backend
          const res = await api.get(API_ENDPOINTS.ME);
          if (res.data.success) {
            setUser(res.data.data);
            localStorage.setItem('user', JSON.stringify(res.data.data));
          }
        } catch (err) {
          console.warn('Session verification failed, resetting auth');
          logout();
        }
      }
      setLoading(false);
    };

    initializeAuth();
  }, []);

  const login = async (email, password) => {
    const res = await api.post(API_ENDPOINTS.LOGIN, { email, password });
    const authData = res.data?.data || res.data;
    if (authData && authData.token) {
      setToken(authData.token);
      setUser(authData);
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData));
      return authData;
    }
    throw new Error(res.data?.message || 'Login failed');
  };

  const register = async (registerData) => {
    const res = await api.post(API_ENDPOINTS.REGISTER, registerData);
    const authData = res.data?.data || res.data;
    if (authData && authData.token) {
      setToken(authData.token);
      setUser(authData);
      localStorage.setItem('token', authData.token);
      localStorage.setItem('user', JSON.stringify(authData));
      return authData;
    }
    throw new Error(res.data?.message || 'Registration failed');
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, setUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
