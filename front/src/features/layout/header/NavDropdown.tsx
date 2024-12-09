import { NavDropdownProps } from '@core/types/header';
import { Link } from 'react-router-dom';

const dropdownPositionClasses: Record<NavDropdownProps['type'], string> = {
    roadmap: 'left-0',
    study: 'left-[120px]',
    user: 'right-0'
};

export const NavDropdown: React.FC<NavDropdownProps> = ({ type, items, isOpen, onClose }) => {
    if (!isOpen) return null;

    return (
        <div
            className={`${dropdownPositionClasses[type]} top-[48px] min-w-[200px] bg-white rounded-lg shadow-lg border border-gray-200 py-1 z-50`}
            onMouseLeave={onClose}
        >
            {items.map((item, i) => (
                <div key={i} className="px-1">
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
