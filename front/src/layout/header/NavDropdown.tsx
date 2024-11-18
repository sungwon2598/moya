import React from 'react';
import { Link } from 'react-router-dom';
import { NavDropdownProps } from '@/types/header';

export const NavDropdown: React.FC<NavDropdownProps> = ({ type, items, isOpen, onClose }) => {
    if (!isOpen) return null;

    const dropdownClasses = {
        roadmap: 'left-0',
        study: 'left-[120px]',
        user: 'right-0',
    };

    return (
        <div
            className={`absolute top-full mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 py-2 ${dropdownClasses[type]}`}
            onMouseLeave={onClose}
        >
            {items.map((item, index) => (
                <div key={index}>
                    {item.href ? (
                        <Link
                            to={item.href}
                            className={`flex items-center px-4 py-2 hover:bg-gray-50 ${
                                item.type === 'danger' ? 'text-red-600' : 'text-gray-700'
                            }`}
                        >
                            {item.icon && <item.icon className="w-4 h-4 mr-2" />}
                            {item.label}
                        </Link>
                    ) : (
                        <button
                            onClick={item.onClick}
                            className={`w-full text-left flex items-center px-4 py-2 hover:bg-gray-50 ${
                                item.type === 'danger' ? 'text-red-600' : 'text-gray-700'
                            }`}
                        >
                            {item.icon && <item.icon className="w-4 h-4 mr-2" />}
                            {item.label}
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
};
