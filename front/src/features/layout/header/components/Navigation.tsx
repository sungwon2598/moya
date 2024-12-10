import React from 'react';
import { Link } from 'react-router-dom';

export const Navigation: React.FC = () => {
    return (
        <nav className="hidden md:flex items-center space-x-6">
            <Link to="/studies" className="text-gray-700 hover:text-gray-900">
                스터디 찾기
            </Link>
            <Link to="/create-study" className="text-gray-700 hover:text-gray-900">
                스터디 만들기
            </Link>
            <Link to="/community" className="text-gray-700 hover:text-gray-900">
                커뮤니티
            </Link>
        </nav>
    );
};
