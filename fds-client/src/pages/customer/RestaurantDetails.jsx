import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import Button from '../../components/Button';
import { PlusCircle, MinusCircle, ShoppingCart } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import VegIcon from '../../assets/Veg_symbol.png';
import NonVegIcon from '../../assets/Non_Veg_symbol.png';

const RestaurantDetails = () => {
  const { restaurantId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [restaurant, setRestaurant] = useState(null);
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [itemQuantities, setItemQuantities] = useState({});
  const [cartMessage, setCartMessage] = useState('');

  useEffect(() => {
    const fetchRestaurantData = async () => {
      setLoading(true);
      setError('');
      try {
        const restaurantRes = await api.get(`/customers/restaurants/${restaurantId}`);
        setRestaurant(restaurantRes.data);

        const menuRes = await api.get(`/customers/${restaurantId}/menu`);
        setMenuItems(menuRes.data);

        const cartRes = await api.get(`/customers/${user.id}/cart`);
        const currentCartItems = cartRes.data.cartItems || [];
        const initialQuantities = {};
        currentCartItems.forEach(item => {
          initialQuantities[item.foodItemId] = item.quantity;
        });
        setItemQuantities(initialQuantities);

      } catch (err) {
        console.error('Error fetching restaurant details or menu:', err);
        setError('Failed to load restaurant details or menu. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    if (user?.id) {
      fetchRestaurantData();
    }
  }, [restaurantId, user?.id]);

  const handleQuantityChange = (foodItemId, change) => {
    setItemQuantities(prevQuantities => {
      const currentQuantity = prevQuantities[foodItemId] || 0;
      const newQuantity = Math.max(0, currentQuantity + change);
      return { ...prevQuantities, [foodItemId]: newQuantity };
    });
  };

  const handleAddToCart = async (foodItemId, quantity) => {
    if (quantity <= 0) {
      setCartMessage('Quantity must be at least 1 to add to cart.');
      return;
    }
    setCartMessage('');
    try {
      const currentQuantity = itemQuantities[foodItemId] || 0;
      let response;
      if (currentQuantity > 100) {
        response = await api.put(`/customers/${user.id}/cart/update/${foodItemId}?quantity=${quantity}`);
      } else {
        response = await api.post(`/customers/${user.id}/cart/add`, {
          customerId: user.id,
          foodItemId,
          quantity,
        });
      }

      setItemQuantities(prev => ({ ...prev, [foodItemId]: quantity }));
      setCartMessage('Cart updated successfully!');
      setTimeout(() => setCartMessage(''), 3000);
    } catch (err) {
      console.error('Error adding/updating cart item:', err);
      if (err.response && err.response.data && err.response.data.message) {
        setCartMessage(`Error: ${err.response.data.message}`);
      } else {
        setCartMessage('Failed to update cart. Please try again.');
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500"></div>
        <p className="ml-4 text-gray-700">Loading restaurant...</p>
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

  if (!restaurant) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100">
        <Navbar />
        <p className="mt-8 text-xl text-gray-600">Restaurant not found.</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 flex flex-col">
      <Navbar />
      <div className="container mx-auto px-4 py-8 flex-grow">
        <div className="bg-white rounded-lg shadow-xl p-8 mb-8">
          <h1 className="text-4xl font-extrabold text-gray-900 mb-2">
            {restaurant.name}
          </h1>
          <p className="text-gray-700 text-lg mb-1">
            <span className="font-semibold">Owner:</span> {restaurant.ownerName}
          </p>
          <p className="text-gray-700 mb-1">
            <span className="font-semibold">Address:</span> {restaurant.address}
          </p>
          <p className="text-gray-600 text-sm">
            <span className="font-semibold">Contact:</span> {restaurant.email} | {restaurant.phone}
          </p>
        </div>

        <h2 className="text-3xl font-bold text-gray-900 mb-6 text-center">Menu</h2>

        {cartMessage && (
          <div className={`p-3 rounded-lg text-center mb-4 ${cartMessage.startsWith('Error') ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`}>
            {cartMessage}
          </div>
        )}

        {menuItems.length === 0 ? (
          <p className="text-center text-gray-600 text-xl mt-12">No menu items available for this restaurant.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {menuItems.map((item) => (
              <div key={item.id} className="bg-white rounded-lg shadow-md p-6 flex flex-col justify-between">
                <div>
                  <img
                    src={item.imageUrl || `https://placehold.co/400x200/ADD8E6/000?text=${encodeURIComponent(item.name)}`}
                    alt={item.name}
                    className="w-full h-40 object-cover rounded-md mb-4"
                    onError={(e) => {
                      e.target.onerror = null; // Prevent infinite loop
                      e.target.src = `https://placehold.co/400x200/ADD8E6/000?text=${encodeURIComponent(item.name)}`;
                    }}
                  />
                  <h3 className="text-xl font-semibold text-gray-800">{item.name}</h3>
                  <p className="text-gray-600 text-sm mb-2">{item.description}</p>
                  <p className="text-lg font-bold text-blue-600 mb-3">₹{item.price.toFixed(2)}</p>
                  <span>
                    {item.category === 'VEG' && (
                      <span className="text-green-700">
                        <img src={VegIcon} alt="Vegetarian" className="h-4 w-4 inline-block mr-3" />
                        Veg
                      </span>
                    )}
                    {item.category === 'NON_VEG' && (
                      <span className="text-red-700">
                        <img src={NonVegIcon} alt="Vegetarian" className="h-4 w-4 inline-block mr-3" />
                        Non-Veg
                      </span>
                    )}
                  </span>
                </div>
                <div className="mt-4 flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Button
                      onClick={() => handleQuantityChange(item.id, -1)}
                      className="p-2 bg-blue-100 hover:bg-red-600 rounded-full"
                      disabled={(itemQuantities[item.id] || 0) <= 0}
                    >
                      <MinusCircle size={18} />
                    </Button>
                    <span className="text-xl font-bold text-gray-800 w-8 text-center">
                      {itemQuantities[item.id] || 0}
                    </span>
                    <Button
                      onClick={() => handleQuantityChange(item.id, 1)}
                      className="p-2 bg-blue-100 hover:bg-green-600 rounded-full"
                    >
                      <PlusCircle size={18} />
                    </Button>
                  </div>
                  <Button
                    onClick={() => handleAddToCart(item.id, itemQuantities[item.id] || 0)}
                    className="flex items-center gap-1 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                    disabled={(itemQuantities[item.id] || 0) === 0}
                  >
                    <ShoppingCart size={18} /> Add
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}
        <div className="mt-8 text-center">
          <Button
            onClick={() => navigate('/customer/cart')}
            className="px-8 py-3 text-lg bg-indigo-600 hover:bg-indigo-700 transition duration-300"
          >
            Go to Cart
          </Button>
        </div>
      </div>
    </div>
  );
};

export default RestaurantDetails;