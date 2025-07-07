import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, UserCircle } from 'lucide-react';

const Navbar = () => {
    const { user, isLoggedIn, logout } = useAuth();
    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef(null);

    const handleLogout = () => {
        logout();
        setIsDropdownOpen(false);
        navigate('/');
    };

    const getUserHomePath = (role) => {
        switch (role) {
            case 'Customer':
                return '/customer/home';
            case 'Restaurant':
                return '/restaurant/home';
            case 'DeliveryAgent':
                return '/deliveryagent/home';
            default:
                return '/';
        }
    };

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
                setIsDropdownOpen(false);
            }
        };

        if (isDropdownOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        } else {
            document.removeEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isDropdownOpen]);

    return (
        <nav className="bg-gradient-to-r from-blue-600 to-blue-800 p-4 shadow-lg relative z-10">
            <div className="container mx-auto flex justify-between items-center">
                <Link to={isLoggedIn ? getUserHomePath(user?.role) : '/'} className="text-white text-2xl font-bold flex items-center gap-2 hover:text-blue-100 transition duration-300">
                    <span className="text-yellow-300 text-3xl">🍽️</span> FDS App
                </Link>

                <div className="flex items-center gap-x-6"> 
                    {isLoggedIn && (
                        <div className="hidden md:flex items-center space-x-4">
                            {user?.role === 'Customer' && (
                                <>
                                    <Link to="/customer/home" className="nav-link">
                                        Restaurants
                                    </Link>
                                    <Link to="/customer/cart" className="nav-link">
                                        Cart
                                    </Link>
                                    <Link to="/customer/orders" className="nav-link">
                                        My Orders
                                    </Link>
                                </>
                            )}
                            {user?.role === 'Restaurant' && (
                                <>
                                    <Link to="/restaurant/home" className="nav-link">
                                        Dashboard
                                    </Link>
                                    <Link to="/restaurant/menu" className="nav-link">
                                        Manage Menu
                                    </Link>
                                    <Link to="/restaurant/orders" className="nav-link">
                                        Manage Orders
                                    </Link>
                                </>
                            )}
                            {user?.role === 'DeliveryAgent' && (
                                <>
                                    <Link to="/deliveryagent/home" className="nav-link">
                                        Dashboard
                                    </Link>
                                    <Link to="/deliveryagent/orders" className="nav-link">
                                        My Deliveries
                                    </Link>
                                </>
                            )}
                        </div>
                    )}

                    {!isLoggedIn ? (
                        <div className="flex items-center space-x-4">
                            <Link to="/" className="nav-link">
                                Home
                            </Link>
                            <Link to="/customer/login" className="nav-link">
                                Customer Login
                            </Link>
                            <Link to="/restaurant/login" className="nav-link">
                                Restaurant Login
                            </Link>
                            <Link to="/deliveryagent/login" className="nav-link">
                                Agent Login
                            </Link>
                        </div>
                    ) : (
                        <div className="relative" ref={dropdownRef}>
                            <button
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                className="flex items-center text-white p-2 rounded-full hover:bg-white hover:bg-opacity-20 focus:outline-none focus:ring-2 focus:ring-white transition-all duration-200"
                                aria-label="User menu"
                                aria-expanded={isDropdownOpen ? "true" : "false"}
                            >
                                <UserCircle size={28} className="text-white" />
                                {user?.username && <span className="ml-2 text-lg font-medium hidden md:inline">{user.username.split(' ')[0]}</span>}
                            </button>

                            {isDropdownOpen && (
                                <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1 ring-1 ring-black ring-opacity-5 focus:outline-none transform origin-top-right transition-all duration-200 ease-out scale-100 opacity-100">
                                    <div className="px-4 py-2 text-sm text-gray-700 border-b border-gray-100">
                                        <p className="font-semibold">{user?.username}</p>
                                        <p className="text-xs text-gray-500">{user?.role}</p>
                                    </div>
                                    <button
                                        onClick={handleLogout}
                                        className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-red-700 transition-colors"
                                    >
                                        <LogOut size={18} className="inline-block mr-2" /> Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;