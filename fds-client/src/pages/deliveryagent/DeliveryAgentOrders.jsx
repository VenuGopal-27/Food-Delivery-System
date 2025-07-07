import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import { RefreshCw, Truck, CheckCheck, PackageCheck } from 'lucide-react';

const OrderStatus = {
  PENDING: 'PENDING',
  PREPARING: 'PREPARING',
  PREPARED: 'PREPARED',
  PICKED_UP: 'PICKED_UP',
  OUT_FOR_DELIVERY: 'OUT_FOR_DELIVERY',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
};

const DeliveryAgentOrders = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusMessage, setStatusMessage] = useState('');
  const [filterStatus, setFilterStatus] = useState('ACTIVE'); // 'ACTIVE' or 'PREVIOUS'

  const fetchOrders = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/deliveryagents/${user.id}/assignments`);
      const allOrders = response.data;

      let filteredOrders;
      if (filterStatus === 'ACTIVE') {
        filteredOrders = allOrders.filter(order =>
          order.orderStatus === OrderStatus.PREPARED || // Ready for pickup
          order.orderStatus === OrderStatus.PICKED_UP ||
          order.orderStatus === OrderStatus.OUT_FOR_DELIVERY
        );
      } else { // 'PREVIOUS'
        filteredOrders = allOrders.filter(order =>
          order.orderStatus === OrderStatus.DELIVERED ||
          order.orderStatus === OrderStatus.CANCELLED
        );
      }
      setOrders(filteredOrders);
    } catch (err) {
      console.error('Error fetching delivery agent orders:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to load your deliveries. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchOrders();
    }
  }, [user?.id, filterStatus]);

  const handleUpdateStatus = async (orderId, newStatus) => {
    setStatusMessage('');
    try {
      await api.put(`/deliveryagents/${user.id}/orders/${orderId}/status`, { newStatus });
      setStatusMessage('Order status updated successfully!');
      fetchOrders(); // Refresh orders after update
    } catch (err) {
      console.error('Error updating delivery status:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setStatusMessage(`Status update failed: ${err.response.data.message}`);
      } else {
        setStatusMessage('Failed to update delivery status. Please try again.');
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case OrderStatus.PENDING:
        return 'bg-yellow-100 text-yellow-800';
      case OrderStatus.PREPARING:
        return 'bg-blue-100 text-blue-800';
      case OrderStatus.PREPARED:
        return 'bg-indigo-100 text-indigo-800';
      case OrderStatus.PICKED_UP:
        return 'bg-purple-100 text-purple-800';
      case OrderStatus.OUT_FOR_DELIVERY:
        return 'bg-orange-100 text-orange-800';
      case OrderStatus.DELIVERED:
        return 'bg-green-100 text-green-800';
      case OrderStatus.CANCELLED:
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-purple-500"></div>
        <p className="ml-4 text-gray-700">Loading deliveries...</p>
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
          My Deliveries
        </h1>

        <div className="flex justify-center gap-4 mb-6">
          <Button
            onClick={() => setFilterStatus('ACTIVE')}
            className={`px-6 py-2 rounded-full ${filterStatus === 'ACTIVE' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'}`}
          >
            Active Deliveries
          </Button>
          <Button
            onClick={() => setFilterStatus('PREVIOUS')}
            className={`px-6 py-2 rounded-full ${filterStatus === 'PREVIOUS' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'}`}
          >
            Previous Deliveries
          </Button>
        </div>

        {statusMessage && (
          <div className={`p-3 rounded-lg text-center mb-4 ${statusMessage.startsWith('Status update failed') ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
            {statusMessage}
          </div>
        )}

        {orders.length === 0 ? (
          <p className="text-center text-gray-600 text-xl mt-12 p-6 bg-white rounded-lg shadow-md">
            No {filterStatus === 'ACTIVE' ? 'active' : 'previous'} deliveries found.
          </p>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-md p-6">
                <div className="flex justify-between items-center mb-4 border-b pb-4 border-gray-200">
                  <div>
                    <h2 className="text-xl font-bold text-gray-800">Order ID: #{order.id}</h2>
                    <p className="text-gray-600 text-sm">Assigned on: {new Date(order.orderDate).toLocaleString()}</p>
                    <p className="text-gray-700"><span className="font-semibold">Customer:</span> {order.customerUsername}</p>
                    <p className="text-gray-700"><span className="font-semibold">Restaurant:</span> {order.restaurantName}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getStatusColor(order.orderStatus)}`}>
                    {order.orderStatus.replace(/_/g, ' ')}
                  </span>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                  <div>
                    <p className="text-gray-700"><span className="font-semibold">Delivery Address:</span> {order.deliveryAddress}</p>
                    <p className="text-gray-700"><span className="font-semibold">Payment Type:</span> {order.paymentType.replace(/_/g, ' ')}</p>
                  </div>
                  <div>
                    <p className="text-xl font-bold text-gray-900">Total Amount: ₹{order.totalAmount.toFixed(2)}</p>
                  </div>
                </div>

                <h3 className="text-lg font-semibold text-gray-800 mb-2">Items:</h3>
                <ul className="list-disc list-inside space-y-1 text-gray-700 pl-4 mb-4">
                  {order.orderItems.map((item, idx) => (
                    <li key={idx}>
                      {item.foodItemName} x {item.quantity} (₹{item.foodItemPrice.toFixed(2)} each)
                    </li>
                  ))}
                </ul>

                <div className="flex flex-wrap gap-3 justify-end">
                  {order.orderStatus === OrderStatus.PREPARED && (
                    <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.PICKED_UP)} className="bg-purple-500 hover:bg-purple-600 text-white flex items-center gap-1">
                      <Truck size={18} /> Picked Up
                    </Button>
                  )}
                  {order.orderStatus === OrderStatus.PICKED_UP && (
                    <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.OUT_FOR_DELIVERY)} className="bg-orange-500 hover:bg-orange-600 text-white flex items-center gap-1">
                      <Truck size={18} /> Out for Delivery
                    </Button>
                  )}
                  {order.orderStatus === OrderStatus.OUT_FOR_DELIVERY && (
                    <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.DELIVERED)} className="bg-green-500 hover:bg-green-600 text-white flex items-center gap-1">
                      <PackageCheck size={18} /> Delivered
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default DeliveryAgentOrders;