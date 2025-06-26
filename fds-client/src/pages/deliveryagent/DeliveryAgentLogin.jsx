import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import InputGroup from '../../components/InputGroup';
import Button from '../../components/Button';
import Navbar from '../../components/Navbar';

const DeliveryAgentLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/deliveryagents/login', { email, password });
      const { token, userId, message } = response.data;
      
      // For delivery agent, `username` is often their email or name.
      login({ id: userId, username: email, role: 'DeliveryAgent' }, token);
      navigate('/deliveryagent/home');
    } catch (err) {
      console.error('Login error:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Login failed. Please check your credentials.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-purple-100 flex flex-col">
      <Navbar />
      <div className="flex-grow flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow-xl">
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Delivery Agent Login
          </h2>
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <InputGroup
              id="email"
              label="Email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Enter your email"
              required
            />
            <InputGroup
              id="password"
              label="Password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter your password"
              required
            />
            {error && <p className="text-red-600 text-sm text-center">{error}</p>}
            <Button
              type="submit"
              className="w-full bg-purple-600 hover:bg-purple-700"
              disabled={loading}
            >
              {loading ? 'Logging in...' : 'Login'}
            </Button>
          </form>
          <div className="text-center text-sm text-gray-600">
            Don't have an account?{' '}
            <Link to="/deliveryagent/register" className="font-medium text-purple-600 hover:text-purple-500">
              Register here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeliveryAgentLogin;