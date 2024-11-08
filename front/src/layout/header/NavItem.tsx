import React from 'react';
import { Link } from 'react-router-dom';
import { Types } from '../../types/types.tsx';

const NavItem: React.FC<Types> = ({ label, type, icon: Icon }) => {
    const getStyle = (): string => {
        switch (type) {
            case 'A':
            case 'B':
            case 'C':
                return 'text-red-500 hover:text-red-600';
            case 'D':
            default:
                return 'text-gray-500 hover:text-gray-600';
        }
    };

    return (
        <Link to={`/${label.toLowerCase()}`} className={`flex items-center px-4 py-2 ${getStyle()} cursor-pointer transition-colors duration-200`}>
            {Icon && <Icon className="w-5 h-5 mr-2" />}
            <span className="text-sm font-semibold">{label}</span>
        </Link>
    );
};

export default NavItem;