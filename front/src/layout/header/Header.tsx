
import React from 'react';
import { Menu, Home, Book, User, ChevronDown } from 'lucide-react';
import { Link } from 'react-router-dom';
import NavItem from './NavItem';

const Header: React.FC = () => {
    return (
        <header className="bg-white border-b border-gray-200 fixed w-full top-0 z-50">
            <div className="container mx-auto">
                <nav className="flex items-center justify-between h-16 px-4">
                    <div className="flex items-center space-x-8">
                        <Link to="/" className="flex items-center space-x-3 cursor-pointer">
                            <Menu className="w-6 h-6 text-green-600" />
                            <span className="text-xl font-bold text-green-600">MOYA</span>
                        </Link>
                        <div className="hidden md:flex items-center space-x-6">
                            <NavItem type="A" label="로드맵" icon={Home} />
                            <NavItem type="B" label="스터디" icon={Book} />
                        </div>
                    </div>
                    <div className="flex items-center space-x-4">
                        <button className="flex items-center space-x-1 text-gray-500 hover:text-gray-600">
                            <User className="w-5 h-5" />
                            <span className="text-sm font-medium">게스트</span>
                            <ChevronDown className="w-4 h-4" />
                        </button>
                        <button className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-700 transition-colors duration-200">
                            로그인
                        </button>
                    </div>
                </nav>
            </div>
        </header>
    );
};

export default Header;