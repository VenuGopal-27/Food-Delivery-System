import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../api/axiosConfig';
import InputGroup from '../../components/InputGroup';
import Button from '../../components/Button';
import Navbar from '../../components/Navbar';

const RestaurantRegister = () => {
  const [formData, setFormData] = useState({
    name: '',
    ownerName: '',
    password: '',
    email: '',
    phone: '',
    address: '',
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');
    setLoading(true);

    try {
      const response = await api.post('/auth/restaurant/register', formData);
      setMessage(response.data.message || 'Registration successful! Please login.');
      setFormData({
        name: '',
        ownerName: '',
        password: '',
        email: '',
        phone: '',
        address: '',
      });
      setTimeout(() => navigate('/restaurant/login'), 2000);
    } catch (err) {
      console.error('Registration error:', err);
      if (err.response && err.response.data) {
        if (typeof err.response.data.message === 'object') {
          const validationErrors = Object.values(err.response.data.message).join(' ');
          setError(`Validation failed: ${validationErrors}`);
        } else {
          setError(err.response.data.message || 'Registration failed. Please try again.');
        }
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-green-100 flex flex-col">
      <Navbar />
      <div className="flex-grow flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow-xl">
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Restaurant Registration
          </h2>
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <InputGroup
              id="name"
              label="Restaurant Name"
              type="text"
              value={formData.name}
              onChange={handleChange}
              placeholder="Enter restaurant name"
              required
            />
            <InputGroup
              id="ownerName"
              label="Owner Name"
              type="text"
              value={formData.ownerName}
              onChange={handleChange}
              placeholder="Enter owner's name"
              required
            />
            <InputGroup
              id="password"
              label="Password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Create a password"
              required
            />
            <InputGroup
              id="email"
              label="Email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Enter restaurant email"
              required
            />
            <InputGroup
              id="phone"
              label="Phone Number"
              type="tel"
              value={formData.phone}
              onChange={handleChange}
              placeholder="Enter restaurant phone number"
              required
              maxLength="10"
            />
            <InputGroup
              id="address"
              label="Address"
              type="text"
              value={formData.address}
              onChange={handleChange}
              placeholder="Enter restaurant address"
              required
            />
            {message && <p className="text-green-600 text-sm text-center">{message}</p>}
            {error && <p className="text-red-600 text-sm text-center">{error}</p>}
            <Button
              type="submit"
              className="w-full bg-green-600 hover:bg-green-700"
              disabled={loading}
            >
              {loading ? 'Registering...' : 'Register'}
            </Button>
          </form>
          <div className="text-center text-sm text-gray-600">
            Already have an account?{' '}
            <Link to="/restaurant/login" className="font-medium text-green-600 hover:text-green-500">
              Login here
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RestaurantRegister;