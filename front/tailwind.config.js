/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        moya: {
          primary: '#0752cd',    // 메인 컬러
          secondary: '#2a83fc',  // 세컨더리 컬러
          background: '#f5f5f8', // 서브 컬러
        }
      }
    },
  },
  plugins: [],
}
