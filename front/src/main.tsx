// src/main.tsx
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'  // App 컴포넌트 import
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <App />  // RouterProvider 대신 App 컴포넌트 사용
    </React.StrictMode>,
)