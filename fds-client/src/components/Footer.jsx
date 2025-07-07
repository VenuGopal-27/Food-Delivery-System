import React from 'react';
import { Link } from 'react-router-dom';
import { Facebook, Twitter, Instagram, Linkedin } from 'lucide-react';

const Footer = () => {
    return (
        <footer className="bg-gradient-to-r from-blue-700 to-indigo-800 text-white py-6 mt-auto shadow-xl border-t-4 border-blue-900">
            <div className="container mx-auto px-4 text-center">
                <div className="flex justify-center space-x-6 mb-3"> 
                    <a href="https://facebook.com" target="_blank" rel="noopener noreferrer" className="text-gray-300 hover:text-white transition-colors duration-200">
                        <Facebook size={22} /> 
                    </a>
                    <a href="https://twitter.com" target="_blank" rel="noopener noreferrer" className="text-gray-300 hover:text-white transition-colors duration-200">
                        <Twitter size={22} />
                    </a>
                    <a href="https://instagram.com" target="_blank" rel="noopener noreferrer" className="text-gray-300 hover:text-white transition-colors duration-200">
                        <Instagram size={22} />
                    </a>
                    <a href="https://linkedin.com" target="_blank" rel="noopener noreferrer" className="text-gray-300 hover:text-white transition-colors duration-200">
                        <Linkedin size={22} />
                    </a>
                </div>
                <p className="text-xs text-gray-400">
                    &copy; {new Date().getFullYear()} FDS App. All rights reserved.
                </p>
            </div>
        </footer>
    );
};

export default Footer;
