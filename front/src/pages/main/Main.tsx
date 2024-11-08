import React from 'react'
import Header from '../..//layout/header/Header'
import MainContent from './MainContent.tsx'

const Main: React.FC = () => {
    return (
        <div className="min-h-screen bg-gray-50">
            <Header />
            <MainContent />
        </div>
    )
}

export default Main