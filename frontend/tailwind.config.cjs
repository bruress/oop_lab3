/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brandViolet: '#6f2cff',
        brandBlue: '#1d4ed8',
        brandYellow: '#ffd60a'
      },
      boxShadow: {
        card: '0 12px 30px rgba(29, 78, 216, 0.15)'
      }
    }
  },
  plugins: []
};
