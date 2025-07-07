import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import { CircleCheck, CircleX } from 'lucide-react';
import { Link } from 'react-router-dom';

const DeliveryAgentHome = () => {
  const { user } = useAuth();
  const [agentData, setAgentData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusMessage, setStatusMessage] = useState(''); // For availability updates

  const fetchAgentData = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/deliveryagents/${user.id}`);
      setAgentData(response.data);
    } catch (err) {
      console.error('Error fetching agent data:', err);
      setError('Failed to load agent data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchAgentData();
    }
  }, [user?.id]);

  const handleUpdateAvailability = async (newStatus) => {
    setStatusMessage('');
    try {
      const response = await api.put(`/deliveryagents/${user.id}/availability?status=${newStatus}`);
      setAgentData(response.data); // Update agent data with new status
      setStatusMessage(`Availability set to ${newStatus.replace(/_/g, ' ')} successfully!`);
      setTimeout(() => setStatusMessage(''), 3000); // Clear message
    } catch (err) {
      console.error('Error updating availability:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setStatusMessage(`Error: ${err.response.data.message}`);
      } else {
        setStatusMessage('Failed to update availability. Please try again.');
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return 'bg-green-100 text-green-800';
      case 'IN_DELIVERY':
        return 'bg-orange-100 text-orange-800';
      case 'OFFLINE':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-purple-500"></div>
        <p className="ml-4 text-gray-700">Loading agent dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 text-red-600">
        <Navbar />
        <p className="mt-8 text-xl">{error}</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Navbar />
      <div className="container mx-auto px-4 py-8 flex-grow">
        <h1 className="text-4xl font-extrabold text-gray-900 mb-8 text-center">
          Delivery Agent Dashboard
        </h1>

        {agentData && (
          <div className="bg-white rounded-lg shadow-xl p-8 mb-8 text-center">
            <h2 className="text-3xl font-bold text-gray-800 mb-2">{agentData.name}</h2>
            <p className="text-gray-600 mb-1">Email: {agentData.email}</p>
            <p className="text-gray-600 mb-1">Phone: {agentData.phone}</p>
            <p className="text-gray-600 mb-1">Address: {agentData.address}</p>
            <p className="text-gray-600 text-sm mt-2 flex items-center justify-center">
              Current Status:{' '}
              <span className={`ml-2 px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(agentData.availabilityStatus)}`}>
                {agentData.availabilityStatus.replace(/_/g, ' ')}
              </span>
            </p>
          </div>
        )}

        <div className="bg-white p-6 rounded-lg shadow-md mb-8 text-center">
          <h2 className="text-2xl font-bold text-gray-800 mb-4">Update Availability</h2>
          {statusMessage && (
            <p className={`text-sm mb-4 ${statusMessage.startsWith('Error') ? 'text-red-600' : 'text-green-600'}`}>
              {statusMessage}
            </p>
          )}
          <div className="flex flex-wrap justify-center gap-4">
            <Button
              onClick={() => handleUpdateAvailability('AVAILABLE')}
              className={`bg-green-500 hover:bg-green-600 text-white flex items-center gap-2 ${agentData?.availabilityStatus === 'AVAILABLE' ? 'opacity-70 cursor-not-allowed' : ''}`}
              disabled={agentData?.availabilityStatus === 'AVAILABLE'}
            >
              <CircleCheck size={20} /> Set Available
            </Button>
            <Button
              onClick={() => handleUpdateAvailability('OFFLINE')}
              className={`bg-red-500 hover:bg-red-600 text-white flex items-center gap-2 ${agentData?.availabilityStatus === 'OFFLINE' ? 'opacity-70 cursor-not-allowed' : ''}`}
              disabled={agentData?.availabilityStatus === 'OFFLINE'}
            >
              <CircleX size={20} /> Set Offline
            </Button>
            {/* IN_DELIVERY status is primarily set by order assignment from restaurant */}
          </div>
        </div>

        <div className="text-center">
          <Link
            to="/deliveryagent/orders"
            className="inline-flex items-center justify-center px-8 py-4 border border-transparent text-lg font-medium rounded-full shadow-lg text-white bg-purple-600 hover:bg-purple-700 transition duration-300 transform hover:scale-105"
          >
            View My Deliveries
          </Link>
        </div>
      </div>
    </div>
  );
};

export default DeliveryAgentHome;