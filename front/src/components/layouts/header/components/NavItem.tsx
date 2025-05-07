import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LucideIcon } from 'lucide-react';

interface NavItemProps {
  label: string;
  path: string;
  icon: LucideIcon;
}

const NavItem: React.FC<NavItemProps> = ({ label, path }) => {
  const location = useLocation();
  const isActive = location.pathname.startsWith(path);

  return (
    <Link
      to={path}
      className={`hover:text-moya-primary flex items-center space-x-2 px-3 py-2 text-gray-600 transition-colors duration-300 hover:rounded-md hover:bg-gray-50 ${
        isActive ? 'text-moya-primary rounded-md bg-gray-50' : ''
      }`}>
      {/* <Icon className="w-5 h-5" /> */}
      <span className="font-mono text-sm">{label}</span>
    </Link>
  );
};

export default NavItem;
