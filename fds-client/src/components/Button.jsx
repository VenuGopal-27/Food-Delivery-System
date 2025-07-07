import React from 'react';

const Button = ({ children, onClick, type = 'button', className = '', ...props }) => {
  return (
    <button
      type={type}
      onClick={onClick}
      className={`px-6 py-3 rounded-lg font-semibold shadow-md transition duration-300 ease-in-out transform hover:scale-105
        ${className || 'bg-blue-600 text-white hover:bg-blue-700'}`}
      {...props}
    >
      {children}
    </button>
  );
};

export default Button;