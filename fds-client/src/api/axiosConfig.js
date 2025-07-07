import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:9999/api'; // Default if not set in .env

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.url && !config.url.includes('/auth/')) { // Do not attach token for auth endpoints
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token expiration/unauthorized responses
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid, log out the user
      
      const originalRequest = error.config;
      if (originalRequest.url.includes('/auth/')) {
        return Promise.reject(error);
      }

      console.error('Unauthorized access or token expired. Logging out...');
      
      localStorage.removeItem('user');
      localStorage.removeItem('token');
      window.location.reload(); // Force reload to re-evaluate AuthContext
    }
    return Promise.reject(error);
  }
);

export default api;