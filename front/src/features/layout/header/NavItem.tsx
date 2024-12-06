import React from 'react';
import { Link } from 'react-router-dom';
import { Types } from '../../../app/types/types.tsx';

 const NavItem: React.FC<Types> = ({ label, path, type, icon: Icon }) => {
    const getStyle = (): string => {
        switch (type) {
            case 'A':
            case 'B':
            case 'C':
                return 'text-moya-primary hover:text-moya-secondary';
            case 'D':
            default:
                return 'text-gray-500 hover:text-moya-primary';
        }
    };

    return (
        <Link
            to={path}
            className={`flex items-center px-4 py-2 ${getStyle()} cursor-pointer transition-colors duration-200`}
        >
            {Icon && <Icon className="w-5 h-5 mr-2" />}
            <span className="text-sm font-semibold">{label}</span>
        </Link>
    );
};

 export default NavItem;
