import React, { useEffect, useRef, useState } from 'react';
import { Home, Book, User, ChevronDown, MessageCircle, Menu, X } from 'lucide-react';
import { Link } from 'react-router-dom';
import NavItem from './components/NavItem';
import { useSelector } from 'react-redux';
import { RootState } from '@/core/store/store';
import { UserDropdown } from "./components/usermenu/UserDropdown.tsx";

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
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);
    const mobileMenuRef = useRef<HTMLDivElement>(null);
    const { isLogin, user } = useSelector((state: RootState) => state.auth);

    // 외부 클릭 감지
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsDropdownOpen(false);
            }
            if (mobileMenuRef.current && !mobileMenuRef.current.contains(event.target as Node)) {
                setIsMobileMenuOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // 모바일 메뉴가 열릴 때 body 스크롤 막기
    useEffect(() => {
        if (isMobileMenuOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }
    }, [isMobileMenuOpen]);

    const handleMobileMenuItemClick = () => {
        setIsMobileMenuOpen(false);
    };

    return (
        <>
            <header className="bg-white border-b border-moya-primary/10 fixed w-full top-0 z-50">
                <div className="container mx-auto">
                    <nav className="flex items-center justify-between h-16 px-4">
                        <div className="flex items-center">
                            {/* 햄버거 메뉴 버튼 */}
                            <button
                                className="md:hidden p-2 -ml-2 text-gray-600 hover:text-moya-primary"
                                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                                aria-label="메뉴 열기"
                            >
                                <Menu className="w-6 h-6" />
                            </button>

                            {/* 로고 */}
                            <Link to="/" className="flex items-center space-x-3 cursor-pointer ml-2 md:ml-0">
                                <span className="text-xl font-bold text-moya-primary">MOYA</span>
                            </Link>

                            {/* 데스크톱 네비게이션 */}
                            <div className="hidden md:flex items-center ml-8">
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

                        {/* 사용자 메뉴 */}
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

            {/* 모바일 사이드 메뉴 */}
            <div
                className={`fixed inset-0 bg-black bg-opacity-50 z-50 transition-opacity duration-200 ${
                    isMobileMenuOpen ? 'opacity-100' : 'opacity-0 pointer-events-none'
                }`}
                aria-hidden="true"
            />
            <div
                ref={mobileMenuRef}
                className={`fixed top-0 left-0 h-full w-64 bg-white z-50 transform transition-transform duration-200 ease-in-out ${
                    isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
                }`}
            >
                {/* 모바일 메뉴 헤더 */}
                <div className="flex items-center justify-between p-4 border-b border-gray-200">
                    <span className="text-xl font-bold text-moya-primary">MOYA</span>
                    <button
                        onClick={() => setIsMobileMenuOpen(false)}
                        className="p-2 text-gray-600 hover:text-moya-primary"
                        aria-label="메뉴 닫기"
                    >
                        <X className="w-6 h-6" />
                    </button>
                </div>

                {/* 모바일 메뉴 아이템 */}
                <div className="py-4">
                    {navigationItems.map((item) => (
                        <Link
                            key={item.path}
                            to={item.path}
                            className="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 hover:text-moya-primary"
                            onClick={handleMobileMenuItemClick}
                        >
                            <item.icon className="w-5 h-5 mr-3" />
                            <span>{item.label}</span>
                        </Link>
                    ))}
                    {isLogin && authNavigationItems.map((item) => (
                        <Link
                            key={item.path}
                            to={item.path}
                            className="flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50 hover:text-moya-primary"
                            onClick={handleMobileMenuItemClick}
                        >
                            <item.icon className="w-5 h-5 mr-3" />
                            <span>{item.label}</span>
                        </Link>
                    ))}
                </div>
            </div>
        </>
    );
};

export default Header;
