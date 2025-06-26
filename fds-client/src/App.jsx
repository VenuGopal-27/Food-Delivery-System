import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';

import LandingPage from './pages/LandingPage';
import CustomerLogin from './pages/customer/CustomerLogin';
import CustomerRegister from './pages/customer/CustomerRegister';
import CustomerHome from './pages/customer/CustomerHome';
import RestaurantLogin from './pages/restaurant/RestaurantLogin';
import RestaurantRegister from './pages/restaurant/RestaurantRegister';
import RestaurantHome from './pages/restaurant/RestaurantHome';
import DeliveryAgentLogin from './pages/deliveryagent/DeliveryAgentLogin';
import DeliveryAgentRegister from './pages/deliveryagent/DeliveryAgentRegister';
import DeliveryAgentHome from './pages/deliveryagent/DeliveryAgentHome';
import RestaurantDetails from './pages/customer/RestaurantDetails';
import CartPage from './pages/customer/CartPage';
import CustomerOrders from './pages/customer/CustomerOrders';
import RestaurantMenuManagement from './pages/restaurant/RestaurantMenuManagement';
import RestaurantOrderManagement from './pages/restaurant/RestaurantOrderManagement';
import DeliveryAgentOrders from './pages/deliveryagent/DeliveryAgentOrders';
import NotFound from './pages/NotFound'; // A simple 404 page
import Footer from './components/Footer';

// ProtectedRoute component to guard routes based on authentication and roles
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, isLoggedIn, loading } = useAuth();

  if (loading) {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100"> 
            <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
            <p className="ml-4 text-gray-700">Loading user data...</p> 
        </div>
    );
  }

  if (!isLoggedIn) {
    return <Navigate to="/customer/login" replace />; // Redirect to a generic login, or specific one
  }

  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/unauthorized" replace />; // Or a dedicated unauthorized page
  }

  return children;
};

// Unauthorized page component
const Unauthorized = () => {
    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 to-red-100"> {/* Removed dark:from-red-900 dark:to-red-800 transition-colors duration-300 */}
            <div className="bg-white p-8 rounded-lg shadow-xl text-center"> {/* Removed dark:bg-gray-800 dark:text-gray-100 */}
                <h1 className="text-6xl font-bold text-red-600 mb-4">403</h1> {/* Removed dark:text-red-400 */}
                <p className="text-2xl font-semibold text-gray-800 mb-2">Access Denied</p> {/* Removed dark:text-gray-200 */}
                <p className="text-lg text-gray-600 mb-6">You do not have permission to view this page.</p> {/* Removed dark:text-gray-300 */}
                <button
                    onClick={() => window.history.back()}
                    className="px-6 py-3 bg-red-500 text-white font-semibold rounded-full shadow-md hover:bg-red-600 transition duration-300 ease-in-out transform hover:scale-105"
                >
                    Go Back
                </button>
            </div>
        </div>
    );
};

const App = () => {
  return (
    <Router>
        <AuthProvider>
          <div className="flex flex-col min-h-screen">
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/unauthorized" element={<Unauthorized />} />

              {/* Customer Routes */}
              <Route path="/customer/login" element={<CustomerLogin />} />
              <Route path="/customer/register" element={<CustomerRegister />} />
              <Route
                path="/customer/home"
                element={
                  <ProtectedRoute allowedRoles={['Customer']}>
                    <CustomerHome />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customer/restaurants/:restaurantId"
                element={
                  <ProtectedRoute allowedRoles={['Customer']}>
                    <RestaurantDetails />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customer/cart"
                element={
                  <ProtectedRoute allowedRoles={['Customer']}>
                    <CartPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/customer/orders"
                element={
                  <ProtectedRoute allowedRoles={['Customer']}>
                    <CustomerOrders />
                  </ProtectedRoute>
                }
              />

              {/* Restaurant Routes */}
              <Route path="/restaurant/login" element={<RestaurantLogin />} />
              <Route path="/restaurant/register" element={<RestaurantRegister />} />
              <Route
                path="/restaurant/home"
                element={
                  <ProtectedRoute allowedRoles={['Restaurant']}>
                    <RestaurantHome />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/restaurant/menu"
                element={
                  <ProtectedRoute allowedRoles={['Restaurant']}>
                    <RestaurantMenuManagement />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/restaurant/orders"
                element={
                  <ProtectedRoute allowedRoles={['Restaurant']}>
                    <RestaurantOrderManagement />
                  </ProtectedRoute>
                }
              />

              {/* Delivery Agent Routes */}
              <Route path="/deliveryagent/login" element={<DeliveryAgentLogin />} />
              <Route path="/deliveryagent/register" element={<DeliveryAgentRegister />} />
              <Route
                path="/deliveryagent/home"
                element={
                  <ProtectedRoute allowedRoles={['DeliveryAgent']}>
                    <DeliveryAgentHome />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/deliveryagent/orders"
                element={
                  <ProtectedRoute allowedRoles={['DeliveryAgent']}>
                    <DeliveryAgentOrders />
                  </ProtectedRoute>
                }
              />

              <Route path="*" element={<NotFound />} />
            </Routes>
            <Footer />
          </div>
        </AuthProvider>
    </Router>
  );
};

export default App;
