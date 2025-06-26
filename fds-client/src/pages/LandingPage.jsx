import React from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { ArrowRightCircle } from 'lucide-react';

const LandingPage = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex flex-col">
      <Navbar />
      <div className="flex-grow flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl w-full space-y-10 text-center">
          <h1 className="text-5xl font-extrabold text-gray-900 leading-tight">
            Welcome to <span className="text-blue-600">Food Delivery System</span>
          </h1>
          <p className="mt-4 text-xl text-gray-600">
            Seamlessly connect customers, restaurants, and delivery agents.
          </p>

          <div className="mt-10 grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Customer Section */}
            <div className="bg-white p-8 rounded-xl shadow-xl hover:shadow-2xl transition duration-300 transform hover:-translate-y-2">
              <h2 className="text-3xl font-bold text-blue-600 mb-4">Customer</h2>
              <p className="text-gray-700 mb-6">
                Order your favorite food from various restaurants with ease.
              </p>
              <div className="space-y-3">
                <Link
                  to="/customer/login"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-full shadow-sm text-white bg-blue-500 hover:bg-blue-600 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Login as Customer
                </Link>
                <Link
                  to="/customer/register"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-blue-500 text-base font-medium rounded-full text-blue-600 bg-white hover:bg-blue-50 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Register as Customer
                </Link>
              </div>
            </div>

            {/* Restaurant Section */}
            <div className="bg-white p-8 rounded-xl shadow-xl hover:shadow-2xl transition duration-300 transform hover:-translate-y-2">
              <h2 className="text-3xl font-bold text-green-600 mb-4">Restaurant</h2>
              <p className="text-gray-700 mb-6">
                Manage your menu, track orders, and grow your business.
              </p>
              <div className="space-y-3">
                <Link
                  to="/restaurant/login"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-full shadow-sm text-white bg-green-500 hover:bg-green-600 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Login as Restaurant
                </Link>
                <Link
                  to="/restaurant/register"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-green-500 text-base font-medium rounded-full text-green-600 bg-white hover:bg-green-50 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Register as Restaurant
                </Link>
              </div>
            </div>

            {/* Delivery Agent Section */}
            <div className="bg-white p-8 rounded-xl shadow-xl hover:shadow-2xl transition duration-300 transform hover:-translate-y-2">
              <h2 className="text-3xl font-bold text-purple-600 mb-4">Delivery Agent</h2>
              <p className="text-gray-700 mb-6">
                Accept assignments, deliver food, and earn on your schedule.
              </p>
              <div className="space-y-3">
                <Link
                  to="/deliveryagent/login"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-full shadow-sm text-white bg-purple-500 hover:bg-purple-600 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Login as Agent
                </Link>
                <Link
                  to="/deliveryagent/register"
                  className="w-full inline-flex items-center justify-center px-6 py-3 border border-purple-500 text-base font-medium rounded-full text-purple-600 bg-white hover:bg-purple-50 transition duration-300"
                >
                  <ArrowRightCircle className="mr-2 h-5 w-5" /> Register as Agent
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LandingPage;