import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import Modal from '../../components/Modal';
import InputGroup from '../../components/InputGroup';
import { RefreshCw, CheckCircle, XCircle, Clock, Truck } from 'lucide-react';
import { useSearchParams } from 'react-router-dom';

const OrderStatus = {
  PENDING: 'PENDING',
  PREPARING: 'PREPARING',
  PREPARED: 'PREPARED',
  PICKED_UP: 'PICKED_UP',
  OUT_FOR_DELIVERY: 'OUT_FOR_DELIVERY',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
};

const RestaurantOrderManagement = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [assignModalOpen, setAssignModalOpen] = useState(false);
  const [selectedOrderId, setSelectedOrderId] = useState(null);
  const [availableAgents, setAvailableAgents] = useState([]);
  const [selectedAgentId, setSelectedAgentId] = useState('');
  const [formMessage, setFormMessage] = useState('');
  const [formError, setFormError] = useState('');
  const [searchParams, setSearchParams] = useSearchParams();
  const initialFilterStatus = searchParams.get('status') === 'previous' ? 'PREVIOUS' : 'ACTIVE';
  const [filterStatus, setFilterStatus] = useState(initialFilterStatus);

  const fetchOrders = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/restaurants/${user.id}/orders`);
      const allOrders = response.data;

      let filteredOrders;
      if (filterStatus === 'ACTIVE') {
        filteredOrders = allOrders.filter(order =>
          order.orderStatus === OrderStatus.PENDING ||
          order.orderStatus === OrderStatus.PREPARING ||
          order.orderStatus === OrderStatus.PREPARED ||
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
      console.error('Error fetching orders:', err);
      setError('Failed to load orders. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableAgents = async () => {
    setFormError('');
    setFormMessage('');
    try {
      
      const response = await api.get('/restaurants/all'); 
      const filteredAgents = response.data.filter(agent => agent.availabilityStatus === 'AVAILABLE');
      setAvailableAgents(filteredAgents);
      if (filteredAgents.length > 0) {
        setSelectedAgentId(filteredAgents[0].id); 
      } else {
        setSelectedAgentId('');
      }
    } catch (err) {
      console.error('Error fetching available agents:', err);
      setFormError('Failed to load available delivery agents.');
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchOrders();
    }
  }, [user?.id, filterStatus, searchParams]); // Re-fetch when filter changes

  const handleUpdateStatus = async (orderId, newStatus) => {
    setFormMessage('');
    setFormError('');
    try {
      await api.put(`/restaurants/${user.id}/orders/${orderId}/status`, { newStatus });
      setFormMessage('Order status updated successfully!');
      fetchOrders(); // Refresh orders after update
    } catch (err) {
      console.error('Error updating order status:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setFormError(`Status update failed: ${err.response.data.message}`);
      } else {
        setFormError('Failed to update order status. Please try again.');
      }
    }
  };

  const handleOpenAssignModal = (orderId) => {
    setSelectedOrderId(orderId);
    fetchAvailableAgents();
    setAssignModalOpen(true);
  };

  const handleAssignAgent = async () => {
    setFormMessage('');
    setFormError('');
    if (!selectedAgentId) {
      setFormError('Please select a delivery agent.');
      return;
    }
    try {
      await api.post(`/restaurants/${user.id}/orders/${selectedOrderId}/assign/${selectedAgentId}`);
      setFormMessage('Order assigned successfully!');
      fetchOrders(); // Refresh orders
      handleCloseAssignModal();
    } catch (err) {
      console.error('Error assigning agent:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setFormError(`Assignment failed: ${err.response.data.message}`);
      } else {
        setFormError('Failed to assign agent. Please try again.');
      }
    }
  };

  const handleCloseAssignModal = () => {
    setAssignModalOpen(false);
    setSelectedOrderId(null);
    setSelectedAgentId('');
    setFormError('');
    setFormMessage('');
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
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-green-500"></div>
        <p className="ml-4 text-gray-700">Loading orders...</p>
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
          Manage Restaurant Orders
        </h1>

        <div className="flex justify-center gap-4 mb-6">
          <Button
            onClick={() => setFilterStatus('ACTIVE')}
            className={`px-6 py-2 rounded-full ${filterStatus === 'ACTIVE' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'}`}
          >
            Active Orders
          </Button>
          <Button
            onClick={() => setFilterStatus('PREVIOUS')}
            className={`px-6 py-2 rounded-full ${filterStatus === 'PREVIOUS' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-800 hover:bg-gray-300'}`}
          >
            Previous Orders
          </Button>
        </div>

        {formMessage && (
          <div className="p-3 bg-green-100 text-green-700 rounded-lg text-center mb-4">
            {formMessage}
          </div>
        )}
        {formError && (
          <div className="p-3 bg-red-100 text-red-700 rounded-lg text-center mb-4">
            {formError}
          </div>
        )}

        {orders.length === 0 ? (
          <p className="text-center text-gray-600 text-xl mt-12 p-6 bg-white rounded-lg shadow-md">
            No {filterStatus === 'ACTIVE' ? 'active' : 'previous'} orders found.
          </p>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.id} className="bg-white rounded-lg shadow-md p-6">
                <div className="flex justify-between items-center mb-4 border-b pb-4 border-gray-200">
                  <div>
                    <h2 className="text-xl font-bold text-gray-800">Order ID: #{order.id}</h2>
                    <p className="text-gray-600 text-sm">Ordered on: {new Date(order.orderDate).toLocaleString()}</p>
                    <p className="text-gray-700"><span className="font-semibold">Customer:</span> {order.customerUsername} (ID: {order.customerId})</p>
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
                    {order.deliveryAgentId && (
                      <p className="text-gray-700"><span className="font-semibold">Delivery Agent:</span> {order.deliveryAgentName} (ID: {order.deliveryAgentId})</p>
                    )}
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
                  {order.orderStatus === OrderStatus.PENDING && (
                    <>
                      <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.PREPARING)} className="bg-blue-500 hover:bg-blue-600 text-white flex items-center gap-1">
                        <Clock size={18} /> Accept Order
                      </Button>
                      <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.CANCELLED)} className="bg-red-500 hover:bg-red-600 text-white flex items-center gap-1">
                        <XCircle size={18} /> Cancel Order
                      </Button>
                    </>
                  )}
                  {order.orderStatus === OrderStatus.PREPARING && (
                    <Button onClick={() => handleUpdateStatus(order.id, OrderStatus.PREPARED)} className="bg-green-500 hover:bg-green-600 text-white flex items-center gap-1">
                      <CheckCircle size={18} /> Mark Prepared
                    </Button>
                  )}
                  {order.orderStatus === OrderStatus.PREPARED && !order.deliveryAgentId && (
                    <Button onClick={() => handleOpenAssignModal(order.id)} className="bg-purple-500 hover:bg-purple-600 text-white flex items-center gap-1">
                      <Truck size={18} /> Assign Delivery Agent
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        <Modal isOpen={assignModalOpen} onClose={handleCloseAssignModal} title="Assign Delivery Agent">
          {formError && <p className="text-red-600 text-sm mb-4">{formError}</p>}
          {formMessage && <p className="text-green-600 text-sm mb-4">{formMessage}</p>}
          {availableAgents.length === 0 ? (
            <p className="text-gray-700 text-center">No available delivery agents at the moment.</p>
          ) : (
            <div className="mb-4">
              <label htmlFor="agentSelect" className="block text-gray-700 text-sm font-semibold mb-2">
                Select Available Agent
              </label>
              <select
                id="agentSelect"
                value={selectedAgentId}
                onChange={(e) => setSelectedAgentId(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
              >
                {availableAgents.map(agent => (
                  <option key={agent.id} value={agent.id}>
                    {agent.name} (ID: {agent.id}) - {agent.email}
                  </option>
                ))}
              </select>
            </div>
          )}
          <div className="flex justify-end gap-3 mt-6">
            <Button type="button" onClick={handleCloseAssignModal} className="bg-gray-300 hover:bg-gray-400 text-gray-800">
              Cancel
            </Button>
            <Button
              onClick={handleAssignAgent}
              className="bg-blue-600 hover:bg-blue-700 text-white"
              disabled={!selectedAgentId || availableAgents.length === 0}
            >
              Assign Agent
            </Button>
          </div>
        </Modal>
      </div>
    </div>
  );
};

export default RestaurantOrderManagement;