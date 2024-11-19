import React from 'react';
import { Menu, X } from 'lucide-react';
import { DropdownItem } from '@types/header';

interface MobileMenuProps {
    isOpen: boolean;
    onClose: () => void;
    menuItems: DropdownItem[];
}

export const MobileMenu: React.FC<MobileMenuProps> = ({ isOpen, onClose, menuItems }) => {
    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 bg-black bg-opacity-50 md:hidden">
            <div className="fixed inset-y-0 left-0 w-64 bg-white shadow-xl">
                <div className="flex items-center justify-between p-4 border-b">
                    <span className="text-xl font-bold text-moya-primary">MOYA</span>
                    <button onClick={onClose} className="p-2">
                        <X className="w-6 h-6" />
                    </button>
                </div>
                <nav className="p-4">
                    {menuItems.map((item, index) => (
                        <div key={index} className="py-2">
                            {item.href ? (
                                <Link
                                    to={item.href}
                                    className="flex items-center text-gray-700 hover:text-moya-primary"
                                >
                                    {item.icon && <item.icon className="w-5 h-5 mr-3" />}
                                    {item.label}
                                </Link>
                            ) : (
                                <button
                                    onClick={item.onClick}
                                    className="flex items-center w-full text-gray-700 hover:text-moya-primary"
                                >
                                    {item.icon && <item.icon className="w-5 h-5 mr-3" />}
                                    {item.label}
                                </button>
                            )}
                        </div>
                    ))}
                </nav>
            </div>
        </div>
    );
};