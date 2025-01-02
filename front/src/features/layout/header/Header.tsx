import React, {useContext, useEffect, useRef, useState} from 'react';
import { Home, Book, User, ChevronDown, MessageCircle, Menu, X } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import NavItem from './components/NavItem';
import { useSelector } from 'react-redux';
import { RootState } from '@/core/store/store';
import { UserDropdown } from "./components/usermenu/UserDropdown.tsx";
// import LoginAlertModal from "../modal/LoginAlertModal.tsx";
// import {ModalContext} from "../../../core/providers/context/ModalContext.tsx";

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
    const navigate = useNavigate();
    // const modalContext = useContext(ModalContext);


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

    const handleCreateStudy = () => {
        // if (!isLogin && modalContext) {
        //     modalContext.showModal(
        //         <LoginAlertModal onClose={modalContext.hideModal} />,
        //         {
        //             showCloseButton: false,
        //         }
        //     );
        //     return;
        // }
        navigate('/study/create');
    };


    return (
        <>
            <header className="bg-white border-b border-moya-primary/10 fixed w-full top-0 z-50">
                <div className="container mx-auto">
                    <nav className="flex items-center justify-between h-16 px-4">
                        <div className="flex items-center">
                            <button
                                className="md:hidden p-2 -ml-2 text-gray-600 hover:text-moya-primary"
                                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                                aria-label="메뉴 열기"
                            >
                                <Menu className="w-6 h-6" />
                            </button>

                            <Link to="/" className="flex items-center space-x-3 cursor-pointer ml-2 md:ml-0">
                                <span className="text-xl font-bold text-moya-primary">MOYA</span>
                            </Link>

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

                        <div className="flex items-center space-x-4">
                            {/* 스터디 만들기 버튼 추가 */}
                            <button
                                onClick={handleCreateStudy}
                                className="hidden md:flex px-4 py-2 bg-emerald-500 text-white rounded-full text-sm font-medium hover:bg-emerald-600 transition-colors duration-200"
                            >
                                스터디 모집하기
                            </button>

                            <div ref={dropdownRef}>
                                <button
                                    className="flex items-center space-x-1 text-gray-600 hover:text-moya-primary transition-colors duration-200"
                                    onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                    aria-expanded={isDropdownOpen}
                                    aria-haspopup="true"
                                >
                                    <User className="w-5 h-5" />
                                    <span className="text-sm font-medium">
                                        {isLogin ? user?.nickname : '게스트'}
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
                        </div>
                    </nav>
                </div>
            </header>

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

                <div className="py-4">
                    {/* 모바일 메뉴에 스터디 만들기 버튼 추가 */}
                    <button
                        onClick={() => {
                            handleCreateStudy();
                            handleMobileMenuItemClick();
                        }}
                        className="flex items-center w-full px-4 py-3 text-emerald-500 hover:bg-gray-50"
                    >
                        <Book className="w-5 h-5 mr-3" />
                        <span>팀원 모집하기</span>
                    </button>

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
