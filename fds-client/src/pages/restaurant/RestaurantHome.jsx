import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';
import { Utensils, ClipboardList, Package } from 'lucide-react';

const RestaurantHome = () => {
  const { user } = useAuth();
  const [restaurantData, setRestaurantData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchRestaurantData = async () => {
      setLoading(true);
      setError('');
      try {
        const response = await api.get(`/restaurants/${user.id}`);
        setRestaurantData(response.data);
      } catch (err) {
        console.error('Error fetching restaurant data:', err);
        setError('Failed to load restaurant data. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    if (user?.id) {
      fetchRestaurantData();
    }
  }, [user?.id]);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-green-500"></div>
        <p className="ml-4 text-gray-700">Loading restaurant dashboard...</p>
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
          Restaurant Dashboard
        </h1>

        {restaurantData && (
          <div className="bg-white rounded-lg shadow-xl p-8 mb-8 text-center">
            <h2 className="text-3xl font-bold text-gray-800 mb-2">{restaurantData.name}</h2>
            <p className="text-gray-600 mb-1">Owned by: {restaurantData.ownerName}</p>
            <p className="text-gray-600 mb-1">Address: {restaurantData.address}</p>
            <p className="text-gray-600 text-sm">Contact: {restaurantData.email} | {restaurantData.phone}</p>
            <p className="text-gray-600 text-sm mt-1">Registered on: {new Date(restaurantData.registeredDate).toLocaleDateString()}</p>
          </div>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <Link
            to="/restaurant/menu"
            className="bg-white p-6 rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-1 flex flex-col items-center justify-center text-center"
          >
            <Utensils className="h-16 w-16 text-green-600 mb-4" />
            <h2 className="text-xl font-bold text-gray-800 mb-2">Manage Menu</h2>
            <p className="text-gray-600">Add, edit, and remove food items.</p>
          </Link>

          <Link
            to="/restaurant/orders"
            className="bg-white p-6 rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-1 flex flex-col items-center justify-center text-center"
          >
            <ClipboardList className="h-16 w-16 text-blue-600 mb-4" />
            <h2 className="text-xl font-bold text-gray-800 mb-2">Manage Orders</h2>
            <p className="text-gray-600">View new orders, update status, and assign deliveries.</p>
          </Link>

          {/* This link will also go to /restaurant/orders, as that page can handle filtering for previous orders */}
          <Link
            to="/restaurant/orders?status=previous"
            className="bg-white p-6 rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-1 flex flex-col items-center justify-center text-center"
          >
            <Package className="h-16 w-16 text-purple-600 mb-4" />
            <h2 className="text-xl font-bold text-gray-800 mb-2">View Previous Orders</h2>
            <p className="text-gray-600">Review past fulfilled or cancelled orders.</p>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default RestaurantHome;