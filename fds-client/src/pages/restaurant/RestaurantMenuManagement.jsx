import React, { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import api from '../../api/axiosConfig';
import { useAuth } from '../../context/AuthContext';
import Button from '../../components/Button';
import Modal from '../../components/Modal';
import InputGroup from '../../components/InputGroup';
import { Plus, Edit, Trash2 } from 'lucide-react';

const FoodCategory = {
  VEG: 'VEG',
  NON_VEG: 'NON_VEG',
};

const RestaurantMenuManagement = () => {
  const { user } = useAuth();
  const [menuItems, setMenuItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentFoodItem, setCurrentFoodItem] = useState(null); // For editing
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    imageUrl: '',
    category: FoodCategory.VEG, // Default to VEG
  });
  const [formError, setFormError] = useState('');
  const [formMessage, setFormMessage] = useState('');

  const fetchMenu = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await api.get(`/restaurants/${user.id}/menu`);
      setMenuItems(response.data);
    } catch (err) {
      console.error('Error fetching menu:', err);
      setError('Failed to load menu. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.id) {
      fetchMenu();
    }
  }, [user?.id]);

  const handleOpenModal = (item = null) => {
    setCurrentFoodItem(item);
    if (item) {
      setFormData({
        name: item.name,
        description: item.description || '',
        price: item.price,
        imageUrl: item.imageUrl || '',
        category: item.category,
      });
    } else {
      setFormData({
        name: '',
        description: '',
        price: '',
        imageUrl: '',
        category: FoodCategory.VEG,
      });
    }
    setFormError('');
    setFormMessage('');
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setCurrentFoodItem(null);
    setFormError('');
    setFormMessage('');
  };

  const handleChange = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
  };

  const handleFormSubmit = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormMessage('');

    try {
      if (currentFoodItem) {
        // Update food item
        await api.put(`/restaurants/${user.id}/menu/${currentFoodItem.id}`, formData);
        setFormMessage('Food item updated successfully!');
      } else {
        // Add new food item
        await api.post(`/restaurants/${user.id}/menu`, formData);
        setFormMessage('Food item added successfully!');
      }
      fetchMenu(); // Refresh menu
      setTimeout(() => handleCloseModal(), 1500); // Close modal after showing message
    } catch (err) {
      console.error('Form submission error:', err);
      if (err.response && err.response.data) {
        if (typeof err.response.data.message === 'object') {
          const validationErrors = Object.values(err.response.data.message).join(' ');
          setFormError(`Validation failed: ${validationErrors}`);
        } else {
          setFormError(err.response.data.message || 'Operation failed. Please try again.');
        }
      } else {
        setFormError('Operation failed. Please try again.');
      }
    }
  };

  const handleDeleteFoodItem = async (foodItemId) => {
    if (window.confirm('Are you sure you want to delete this food item?')) {
      try {
        await api.delete(`/restaurants/${user.id}/menu/${foodItemId}`);
        setMenuItems(menuItems.filter(item => item.id !== foodItemId));
        setFormMessage('Food item deleted successfully!');
        setTimeout(() => setFormMessage(''), 3000); // Clear message
      } catch (err) {
        console.error('Error deleting food item:', err);
        if (err.response && err.response.data && err.response.data.message) {
          setError(err.response.data.message);
        } else {
          setError('Failed to delete food item. Please try again.');
        }
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <div className="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-green-500"></div>
        <p className="ml-4 text-gray-700">Loading menu...</p>
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
          Manage Restaurant Menu
        </h1>

        <div className="flex justify-end mb-6">
          <Button
            onClick={() => handleOpenModal()}
            className="bg-green-600 hover:bg-green-700 text-white flex items-center gap-2"
          >
            <Plus size={20} /> Add New Food Item
          </Button>
        </div>

        {formMessage && (
          <div className="p-3 bg-green-100 text-green-700 rounded-lg text-center mb-4">
            {formMessage}
          </div>
        )}

        {menuItems.length === 0 ? (
          <p className="text-center text-gray-600 text-xl mt-12 p-6 bg-white rounded-lg shadow-md">
            No menu items added yet. Click "Add New Food Item" to get started!
          </p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {menuItems.map((item) => (
              <div key={item.id} className="bg-white rounded-lg shadow-md p-6 flex flex-col">
                <img
                  src={item.imageUrl || `https://placehold.co/400x200/ADD8E6/000?text=${encodeURIComponent(item.name)}`}
                  alt={item.name}
                  className="w-full h-40 object-cover rounded-md mb-4"
                  onError={(e) => {
                    e.target.onerror = null;
                    e.target.src = `https://placehold.co/400x200/ADD8E6/000?text=${encodeURIComponent(item.name)}`;
                  }}
                />
                <h3 className="text-xl font-semibold text-gray-800">{item.name}</h3>
                <p className="text-gray-600 text-sm mb-2 flex-grow">{item.description}</p>
                <p className="text-lg font-bold text-blue-600 mb-3">₹{item.price.toFixed(2)}</p>
                <span className={`px-2 py-1 rounded-full text-xs font-semibold self-start mb-4 ${item.category === FoodCategory.VEG ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                  {item.category}
                </span>
                <div className="flex justify-end gap-3 mt-auto">
                  <Button
                    onClick={() => handleOpenModal(item)}
                    className="p-2 bg-blue-500 hover:bg-blue-600 text-white rounded-full"
                  >
                    <Edit size={18} />
                  </Button>
                  <Button
                    onClick={() => handleDeleteFoodItem(item.id)}
                    className="p-2 bg-red-500 hover:bg-red-600 text-white rounded-full"
                  >
                    <Trash2 size={18} />
                  </Button>
                </div>
              </div>
            ))}
          </div>
        )}

        <Modal isOpen={isModalOpen} onClose={handleCloseModal} title={currentFoodItem ? 'Edit Food Item' : 'Add New Food Item'}>
          <form onSubmit={handleFormSubmit}>
            <InputGroup
              id="name"
              label="Name"
              value={formData.name}
              onChange={handleChange}
              placeholder="e.g., Margherita Pizza"
              required
            />
            <InputGroup
              id="description"
              label="Description"
              value={formData.description}
              onChange={handleChange}
              placeholder="e.g., Classic cheese and tomato pizza"
            />
            <InputGroup
              id="price"
              label="Price (₹)"
              type="number"
              value={formData.price}
              onChange={handleChange}
              placeholder="e.g., 250.00"
              step="0.01"
              required
            />
            <InputGroup
              id="imageUrl"
              label="Image URL"
              value={formData.imageUrl}
              onChange={handleChange}
              placeholder="e.g., https://example.com/pizza.jpg"
            />
            <div className="mb-4">
              <label htmlFor="category" className="block text-gray-700 text-sm font-semibold mb-2">
                Category
              </label>
              <select
                id="category"
                value={formData.category}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400"
                required
              >
                <option value={FoodCategory.VEG}>Vegetarian</option>
                <option value={FoodCategory.NON_VEG}>Non-Vegetarian</option>
              </select>
            </div>
            {formError && <p className="text-red-600 text-sm mb-4">{formError}</p>}
            {formMessage && <p className="text-green-600 text-sm mb-4">{formMessage}</p>}
            <div className="flex justify-end gap-3 mt-6">
              <Button type="button" onClick={handleCloseModal} className="bg-gray-300 hover:bg-gray-400 text-gray-800">
                Cancel
              </Button>
              <Button type="submit" className="bg-blue-600 hover:bg-blue-700 text-white">
                {currentFoodItem ? 'Update Item' : 'Add Item'}
              </Button>
            </div>
          </form>
        </Modal>
      </div>
    </div>
  );
};

export default RestaurantMenuManagement;