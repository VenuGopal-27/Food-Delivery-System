import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import InputGroup from '../../components/InputGroup';
import { PlusCircle, MinusCircle, Trash2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const CartPage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [orderPlacementMessage, setOrderPlacementMessage] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState(user?.address || ''); // Pre-fill with user's address
  const [paymentType, setPaymentType] = useState('CASH_ON_DELIVERY'); // Default payment type

  const fetchCart = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/customers/${user.id}/cart`);
      setCart(response.data);
    } catch (err) {
      console.error('Error fetching cart:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to load cart. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchCart();
    }
  }, [user?.id]);

  const handleUpdateQuantity = async (foodItemId, newQuantity) => {
    setOrderPlacementMessage('');
    try {
      const response = await api.put(`/customers/${user.id}/cart/update/${foodItemId}?quantity=${newQuantity}`);
      setCart(response.data); // Update cart with the latest data from backend
    } catch (err) {
      console.error('Error updating cart item quantity:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setOrderPlacementMessage(`Error updating quantity: ${err.response.data.message}`);
      } else {
        setOrderPlacementMessage('Failed to update quantity. Please try again.');
      }
    }
  };

  const handleRemoveItem = async (foodItemId) => {
    setOrderPlacementMessage('');
    try {
      await api.delete(`/customers/${user.id}/cart/remove/${foodItemId}`);
      fetchCart(); // Re-fetch cart after removal
    } catch (err) {
      console.error('Error removing cart item:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setOrderPlacementMessage(`Error removing item: ${err.response.data.message}`);
      } else {
        setOrderPlacementMessage('Failed to remove item. Please try again.');
      }
    }
  };

  const handleClearCart = async () => {
    setOrderPlacementMessage('');
    if (window.confirm('Are you sure you want to clear your entire cart?')) {
      try {
        await api.delete(`/customers/${user.id}/cart/clear`);
        setCart({ ...cart, cartItems: [], totalCartValue: 0 }); // Optimistically update UI
        setOrderPlacementMessage('Cart cleared successfully!');
        // No need to fetch, state is already updated
      } catch (err) {
        console.error('Error clearing cart:', err);
        if (err.response && err.response.data && err.response.data.message) {
          setOrderPlacementMessage(`Error clearing cart: ${err.response.data.message}`);
        } else {
          setOrderPlacementMessage('Failed to clear cart. Please try again.');
        }
      }
    }
  };

  const handlePlaceOrder = async () => {
    setOrderPlacementMessage('');
    if (!deliveryAddress) {
      setOrderPlacementMessage('Please enter a delivery address.');
      return;
    }
    if (!cart || !cart.cartItems || cart.cartItems.length === 0) {
      setOrderPlacementMessage('Your cart is empty. Please add items before placing an order.');
      return;
    }

    try {
      const response = await api.post(`/customers/${user.id}/orders/place`, {
        customerId: user.id,
        deliveryAddress,
        paymentType,
      });
      setOrderPlacementMessage('Order placed successfully! Redirecting to orders...');
      // Clear cart in UI and localStorage after successful order
      setCart({ ...cart, cartItems: [], totalCartValue: 0 });
      setTimeout(() => navigate('/customer/orders'), 2000); // Redirect to orders page
    } catch (err) {
      console.error('Error placing order:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setOrderPlacementMessage(`Order failed: ${err.response.data.message}`);
      } else {
        setOrderPlacementMessage('Failed to place order. Please try again.');
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
        <p className="ml-4 text-gray-700">Loading cart...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 text-red-600">
        <Navbar />
        <p className="mt-8 text-xl">{error}</p>
        <Button onClick={() => navigate('/customer/home')} className="mt-4 bg-blue-600 hover:bg-blue-700">
          Continue Shopping
        </Button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Navbar />
      <div className="container mx-auto px-4 py-8 flex-grow">
        <h1 className="text-4xl font-extrabold text-gray-900 mb-8 text-center">Your Cart</h1>

        {orderPlacementMessage && (
          <div className={`p-4 rounded-lg text-center mb-6 ${orderPlacementMessage.startsWith('Order placed') ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
            {orderPlacementMessage}
          </div>
        )}

        {!cart || cart.cartItems.length === 0 ? (
          <div className="text-center text-gray-600 text-xl mt-12 p-6 bg-white rounded-lg shadow-md">
            <p className="mb-4">Your cart is currently empty.</p>
            <Button onClick={() => navigate('/customer/home')} className="bg-blue-600 hover:bg-blue-700">
              Start Shopping
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Cart Items List */}
            <div className="lg:col-span-2 bg-white rounded-lg shadow-xl p-6">
              <h2 className="text-2xl font-bold text-gray-800 mb-4">Items in Cart</h2>
              {cart.cartItems.map((item) => (
                <div key={item.cartItemId} className="flex items-center justify-between border-b border-gray-200 py-4 last:border-b-0">
                  <div className="flex-grow">
                    <h3 className="text-lg font-semibold text-gray-800">{item.foodItemName}</h3>
                    <p className="text-gray-600">₹{(item.foodItemPrice * item.quantity).toFixed(2)} (<span className="text-sm">₹{item.foodItemPrice.toFixed(2)}/each</span>)</p>
                  </div>
                  <div className="flex items-center space-x-3">
                    <Button
                      onClick={() => handleUpdateQuantity(item.foodItemId, item.quantity - 1)}
                      className="p-1 bg-red-500 hover:bg-red-600 rounded-full"
                      disabled={item.quantity <= 1}
                    >
                      <MinusCircle size={18} />
                    </Button>
                    <span className="text-lg font-bold text-gray-800">{item.quantity}</span>
                    <Button
                      onClick={() => handleUpdateQuantity(item.foodItemId, item.quantity + 1)}
                      className="p-1 bg-green-500 hover:bg-green-600 rounded-full"
                    >
                      <PlusCircle size={18} />
                    </Button>
                    <Button
                      onClick={() => handleRemoveItem(item.foodItemId)}
                      className="p-1 bg-gray-400 hover:bg-gray-500 rounded-full text-white"
                    >
                      <Trash2 size={18} />
                    </Button>
                  </div>
                </div>
              ))}
              <div className="mt-6 text-right">
                <Button
                  onClick={handleClearCart}
                  className="bg-red-500 hover:bg-red-600 text-white flex items-center gap-2 float-left"
                >
                  <Trash2 size={20} /> Clear Cart
                </Button>
                <p className="text-xl font-bold text-gray-900">Total: ₹{cart.totalCartValue.toFixed(2)}</p>
              </div>
            </div>

            {/* Checkout Section */}
            <div className="lg:col-span-1 bg-white rounded-lg shadow-xl p-6 h-fit sticky top-24">
              <h2 className="text-2xl font-bold text-gray-800 mb-4">Checkout Details</h2>
              <InputGroup
                id="deliveryAddress"
                label="Delivery Address"
                type="text"
                value={deliveryAddress}
                onChange={(e) => setDeliveryAddress(e.target.value)}
                placeholder="Enter delivery address"
                required
              />
              <div className="mb-6">
                <label htmlFor="paymentType" className="block text-gray-700 text-sm font-semibold mb-2">
                  Payment Type
                </label>
                <select
                  id="paymentType"
                  value={paymentType}
                  onChange={(e) => setPaymentType(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                >
                  <option value="CASH_ON_DELIVERY">Cash on Delivery</option>
                  <option value="CARD">Card</option>
                  <option value="UPI">UPI</option>
                </select>
              </div>
              <Button onClick={handlePlaceOrder} className="w-full bg-green-600 hover:bg-green-700">
                Place Order (₹{cart.totalCartValue.toFixed(2)})
              </Button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default CartPage;