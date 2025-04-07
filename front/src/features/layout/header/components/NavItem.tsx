import React from 'react';
import { Link } from 'react-router-dom';
import { LucideIcon } from 'lucide-react';

interface NavItemProps {
  label: string;
  path: string;
  icon: LucideIcon;
}

const NavItem: React.FC<NavItemProps> = ({ label, path, icon: Icon }) => {
  return (
    <Link
      to={path}
      className="hover:text-moya-primary flex items-center space-x-2 px-3 py-2 text-gray-600 transition-colors duration-200">
      <Icon className="h-5 w-5" />
      <span className="text-sm font-medium">{label}</span>
    </Link>
  );
};

export default NavItem;
