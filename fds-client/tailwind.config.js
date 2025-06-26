/** @type {import('tailwindcss').Config} */
const defaultTheme = require('tailwindcss/defaultTheme');
module.exports = {
  
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        'display': ['"Playfair Display"', 'serif'], // Example: for headings
        'body': ['Inter', 'sans-serif'],
        sans: ['Inter', ...defaultTheme.fontFamily.sans],
      },
    },
  },
  
  plugins: [],
};