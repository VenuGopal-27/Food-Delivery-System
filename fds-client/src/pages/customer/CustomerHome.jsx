import React, { useState, useEffect, StrictMode } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { Link } from 'react-router-dom';
import { Search } from 'lucide-react';
import InputGroup from '../../components/InputGroup';
import Button from '../../components/Button';

const CustomerHome = () => {

  const [restaurants, setRestaurants] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchRestaurants = async (query = '') => {
    setLoading(true);
    setError('');
    try {
      let response;
      if (query) {
        response = await api.get(`/customers/search/restaurants?query=${query}`);
      } else {
        response = await api.get('/customers/restaurants'); 
      }
      setRestaurants(response.data);
    } catch (err) {
      console.error('Error fetching restaurants:', err);
      setError('Failed to load restaurants. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchRestaurants(searchQuery);
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
        <p className="ml-4 text-gray-700">Loading restaurants...</p>
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
          Discover Restaurants
        </h1>

        <form onSubmit={handleSearch} className="flex flex-col sm:flex-row gap-4 mb-8 p-4 bg-white rounded-xl shadow-lg border border-gray-200 max-w-2xl mx-auto">
          <div className="flex-grow">
            <InputGroup
              id="search"
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by restaurant name..."
              className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 transition duration-200 ease-in-out" // Enhanced input styling
            />
          </div>
          <Button
            type="submit"
            className="flex items-center justify-center px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg shadow-md transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2" // Enhanced button styling
          >
            <Search className="mr-2" size={20} /> Search
          </Button>
        </form>


        {restaurants.length === 0 && (
          <p className="text-center text-gray-600 text-xl mt-12">No restaurants found. Try a different search or check back later!</p>
        )}

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {restaurants.map((restaurant) => (
            <Link
              key={restaurant.id}
              to={`/customer/restaurants/${restaurant.id}`}
              className="bg-white rounded-lg shadow-md hover:shadow-xl transition-shadow duration-300 transform hover:-translate-y-1 overflow-hidden"
            >
              <div className="p-6">
              
                <h2 className="text-2xl font-bold text-gray-800 mb-2">
                  {restaurant.name}
                </h2>
                <p className="text-gray-600 mb-1">
                  <span className="font-semibold">Owner:</span> {restaurant.ownerName}
                </p>
                <p className="text-gray-600 mb-1">
                  <span className="font-semibold">Address:</span> {restaurant.address}
                </p>
                <p className="text-gray-600 text-sm">
                  <span className="font-semibold">Registered:</span> {new Date(restaurant.registeredDate).toLocaleDateString()}
                </p>
              </div>
            </Link>
          ))}
        </div>
      </div>
    </div>
  );
};

export default CustomerHome;

