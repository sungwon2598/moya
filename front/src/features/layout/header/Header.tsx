import React from 'react';
import { Menu, Home, Book, User, ChevronDown, MessageCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import NavItem from './NavItem.tsx';

// 네비게이션 아이템 설정
const navigationItems = [
    {
        label: '로드맵',
        path: '/roadmap/preview',
        type: 'A' as const,
        icon: Home
    },
    {
        label: '스터디',
        path: '/study',
        type: 'A' as const,
        icon: Book
    }
];

// 인증이 필요한 네비게이션 아이템
const authNavigationItems = [
    {
        label: '채팅',
        path: '/chat',
        type: 'A' as const,
        icon: MessageCircle
    }
];

const Header: React.FC = () => {
    const  isLoggedIn = true;

    return (
        <header className="bg-white border-b border-moya-primary/10 fixed w-full top-0 z-50">
            <div className="container mx-auto">
                <nav className="flex items-center justify-between h-16 px-4">
                    <div className="flex items-center space-x-8">
                        <Link to="/" className="flex items-center space-x-3 cursor-pointer">
                            <Menu className="w-6 h-6 text-moya-primary" />
                            <span className="text-xl font-bold text-moya-primary">MOYA</span>
                        </Link>
                        <div className="hidden md:flex items-center">
                            {/* 기본 네비게이션 아이템 */}
                            {navigationItems.map((item) => (
                                <NavItem
                                    key={item.path}
                                    {...item}
                                />
                            ))}
                            {/* 인증이 필요한 네비게이션 아이템 */}
                            {isLoggedIn && authNavigationItems.map((item) => (
                                <NavItem
                                    key={item.path}
                                    {...item}
                                />
                            ))}
                        </div>
                    </div>
                    <div className="flex items-center space-x-4">
                        <button className="flex items-center space-x-1 text-gray-600 hover:text-moya-primary transition-colors duration-200">
                            <User className="w-5 h-5" />
                            <span className="text-sm font-medium">게스트</span>
                            <ChevronDown className="w-4 h-4" />
                        </button>
                        <button className="bg-moya-primary hover:bg-moya-secondary text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors duration-200 shadow-sm hover:shadow-md">
                            로그인
                        </button>
                    </div>
                </nav>
            </div>
        </header>
    );
};

export default Header;
