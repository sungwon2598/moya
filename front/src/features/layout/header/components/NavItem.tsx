import React from 'react';
import { Link } from 'react-router-dom';
import { LucideIcon } from 'lucide-react';

interface NavItemProps {
    label: string;
    path: string;
    icon: LucideIcon;
    type: 'A' | 'B';
}

const NavItem: React.FC<NavItemProps> = ({ label, path, icon: Icon, type }) => {
    return (
        <Link
            to={path}
            className="flex items-center space-x-2 px-3 py-2 text-gray-600 hover:text-moya-primary transition-colors duration-200"
        >
            <Icon className="w-5 h-5" />
            <span className="text-sm font-medium">{label}</span>
        </Link>
    );
};

export default NavItem;
