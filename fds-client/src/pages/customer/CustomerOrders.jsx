import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import { RefreshCw } from 'lucide-react';
import Button from '../../components/Button';

const CustomerOrders = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchOrders = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/customers/${user.id}/orders`);
      setOrders(response.data);
    } catch (err) {
      console.error('Error fetching orders:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to load orders. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchOrders();
    }
  }, [user?.id]);

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'PREPARING':
        return 'bg-blue-100 text-blue-800';
      case 'PREPARED':
        return 'bg-indigo-100 text-indigo-800';
      case 'PICKED_UP':
        return 'bg-purple-100 text-purple-800';
      case 'OUT_FOR_DELIVERY':
        return 'bg-orange-100 text-orange-800';
      case 'DELIVERED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
        <p className="ml-4 text-gray-700">Loading your orders...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 text-red-600">
        <Navbar />
        <p className="mt-8 text-xl">{error}</p>
        <Button onClick={fetchOrders} className="mt-4 bg-blue-600 hover:bg-blue-700">
          <RefreshCw className="mr-2" size={18} /> Retry
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Navbar />
      <div className="container mx-auto px-4 py-8 flex-grow">
        <h1 className="text-4xl font-extrabold text-gray-900 mb-8 text-center">Your Orders</h1>

        {orders.length === 0 ? (
          <div className="text-center text-gray-600 text-xl mt-12 p-6 bg-white rounded-lg shadow-md">
            <p>You haven't placed any orders yet.</p>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-md p-6">
                <div className="flex justify-between items-center mb-4 border-b pb-4 border-gray-200">
                  <div>
                    <h2 className="text-xl font-bold text-gray-800">Order ID: #{order.id}</h2>
                    <p className="text-gray-600 text-sm">Ordered on: {new Date(order.orderDate).toLocaleString()}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(order.orderStatus)}`}>
                    {order.orderStatus.replace(/_/g, ' ')}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <p className="text-gray-700"><span className="font-semibold">Restaurant:</span> {order.restaurantName}</p>
                    <p className="text-gray-700"><span className="font-semibold">Delivery Address:</span> {order.deliveryAddress}</p>
                    <p className="text-gray-700"><span className="font-semibold">Payment Type:</span> {order.paymentType.replace(/_/g, ' ')}</p>
                  </div>
                  <div>
                    {order.deliveryAgentId && (
                      <p className="text-gray-700"><span className="font-semibold">Delivery Agent:</span> {order.deliveryAgentName} (ID: {order.deliveryAgentId})</p>
                    )}
                    <p className="text-xl font-bold text-gray-900">Total Amount: ₹{order.totalAmount.toFixed(2)}</p>
                  </div>
                </div>

                <h3 className="text-lg font-semibold text-gray-800 mb-2">Items:</h3>
                <ul className="list-disc list-inside space-y-1 text-gray-700 pl-4">
                  {order.orderItems.map((item, idx) => (
                    <li key={idx}>
                      {item.foodItemName} x {item.quantity} (₹{item.foodItemPrice.toFixed(2)} each)
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        )}
        <div className="mt-8 text-center">
          <Button onClick={fetchOrders} className="bg-blue-600 hover:bg-blue-700 flex items-center justify-center mx-auto">
            <RefreshCw className="mr-2" size={20} /> Refresh Orders
          </Button>
        </div>
      </div>
    </div>
  );
};

export default CustomerOrders;