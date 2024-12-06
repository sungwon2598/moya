import React from 'react';
import { Link } from 'react-router-dom';
import { NavDropdownProps } from '@/types/header';

export const NavDropdown: React.FC<NavDropdownProps> = ({ type, items, isOpen, onClose }) => {
    if (!isOpen) return null;

    const dropdownPositionClasses = {
        roadmap: 'absolute left-0',
        study: 'absolute left-0',
        user: 'absolute right-0'
    };

    return (
        <div
            className={`${dropdownPositionClasses[type]} top-[48px] min-w-[200px] bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50`}
            onMouseLeave={onClose}
        >
            {items.map((item, index) => (
                <div key={index} className="px-1">
                    {item.href ? (
                        <Link
                            to={item.href}
                            className={`flex items-center px-4 py-2 hover:bg-gray-50 rounded-md ${
                                item.type === 'danger' ? 'text-red-600' : 'text-gray-700'
                            }`}
                        >
                            {item.icon && <item.icon className="w-4 h-4 mr-2"/>}
                            {item.label}
                        </Link>
                    ) : (
                        <button
                            onClick={item.onClick}
                            className={`w-full text-left flex items-center px-4 py-2 hover:bg-gray-50 rounded-md ${
                                item.type === 'danger' ? 'text-red-600' : 'text-gray-700'
                            }`}
                        >
                            {item.icon && <item.icon className="w-4 h-4 mr-2"/>}
                            {item.label}
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
};
