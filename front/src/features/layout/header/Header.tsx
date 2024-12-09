import React, {useEffect, useRef, useState} from 'react';
import { Menu, Home, Book, User, ChevronDown, MessageCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import NavItem from './components/NavItem';
import { useSelector } from 'react-redux';
import { RootState } from '@/core/store/store';
import {UserDropdown} from "./components/usermenu/UserDropdown.tsx";

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
export const Header: React.FC = () => {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);
    const { isLogin, user } = useSelector((state: RootState) => state.auth);

    // 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsDropdownOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    return (
        <header className="bg-white border-b border-moya-primary/10 fixed w-full top-0 z-50">
            <div className="container mx-auto">
                <nav className="flex items-center justify-between h-16 px-4">
                    <div className="flex items-center space-x-8">
                        <Link to="/" className="flex items-center space-x-3 cursor-pointer">
                            {/*<Menu className="w-6 h-6 text-moya-primary" />*/}
                            <span className="text-xl font-bold text-moya-primary">MOYA</span>
                        </Link>
                        <div className="hidden md:flex items-center">
                            {navigationItems.map((item) => (
                                <NavItem
                                    key={item.path}
                                    {...item}
                                />
                            ))}
                            {isLogin && authNavigationItems.map((item) => (
                                <NavItem
                                    key={item.path}
                                    {...item}
                                />
                            ))}
                        </div>
                    </div>
                    <div className="flex items-center space-x-4" ref={dropdownRef}>
                        <button
                            className="flex items-center space-x-1 text-gray-600 hover:text-moya-primary transition-colors duration-200"
                            onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                            aria-expanded={isDropdownOpen}
                            aria-haspopup="true"
                        >
                            <User className="w-5 h-5" />
                            <span className="text-sm font-medium">
                                {isLogin ? user?.nickName : '게스트'}
                            </span>
                            <ChevronDown className="w-4 h-4" />
                        </button>
                        {isDropdownOpen && (
                            <UserDropdown
                                user={user}
                                isLogin={isLogin}
                                onClose={() => setIsDropdownOpen(false)}
                            />
                        )}
                    </div>
                </nav>
            </div>
        </header>
    );
};

export default Header;
