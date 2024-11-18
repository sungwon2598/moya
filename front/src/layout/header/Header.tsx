import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Menu, Home, Book, User, ChevronDown, Settings, Bell, LogOut } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';
import { useModal } from '@/hooks/useModal';
import { NavDropdown } from './NavDropdown';
import { MobileMenu } from './MobileMenu';
import NavItem from './NavItem';

const Header: React.FC = () => {
    const { isLoggedIn, logout, user } = useAuth();
    const { showModal } = useModal();
    const [activeDropdown, setActiveDropdown] = useState<'roadmap' | 'study' | 'user' | null>(null);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const roadmapItems = [
        { label: '전체 로드맵', href: '/roadmaps' },
        { label: '로드맵 생성', href: '/roadmaps/frontend' },
        { label: '생성된 로드맵', href: '/roadmaps/backend' },
    ];

    const studyItems = [
        { label: '스터디 찾기', href: '/studies' },
        { label: '스터디 만들기', href: '/studies/create' },
        { label: '내 스터디', href: '/studies/my' },
        { label: '스터디 히스토리', href: '/studies/history' },
    ];

    const userItems = [
        { label: '프로필 설정', href: '/settings/profile', icon: Settings },
        { label: '알림 설정', href: '/settings/notifications', icon: Bell },
        { label: '로그아웃', onClick: logout, icon: LogOut, type: 'danger' },
    ];

    const handleLogin = () => {
        showModal(<div>로그인 모달 내용</div>, {
            title: '로그인',
            size: 'md',
        });
    };

    const toggleMobileMenu = () => {
        setIsMobileMenuOpen(!isMobileMenuOpen);
    };

    return (
        <header className="bg-white border-b border-moya-primary/10 fixed w-full top-0 z-50">
            <div className="container mx-auto">
                <nav className="flex items-center justify-between h-16 px-4">
                    <div className="flex items-center space-x-8">
                        <button onClick={toggleMobileMenu} className="md:hidden">
                            <Menu className="w-6 h-6 text-moya-primary"/>
                        </button>
                        <Link to="/" className="flex items-center space-x-3">
                            <span className="text-xl font-bold text-moya-primary">MOYA</span>
                        </Link>
                        <div className="hidden md:flex items-center space-x-6">
                            {/* 수정된 네비게이션 항목 컨테이너 */}
                            <div className="relative">
                                <div
                                    onMouseEnter={() => setActiveDropdown('roadmap')}
                                    onMouseLeave={() => setActiveDropdown(null)}
                                >
                                    <NavItem icon={Home} label="로드맵"/>
                                    <NavDropdown
                                        type="roadmap"
                                        items={roadmapItems}
                                        isOpen={activeDropdown === 'roadmap'}
                                        onClose={() => setActiveDropdown(null)}
                                    />
                                </div>
                            </div>
                            <div className="relative">
                                <div
                                    onMouseEnter={() => setActiveDropdown('study')}
                                    onMouseLeave={() => setActiveDropdown(null)}
                                >
                                    <NavItem icon={Book} label="스터디"/>
                                    <NavDropdown
                                        type="study"
                                        items={studyItems}
                                        isOpen={activeDropdown === 'study'}
                                        onClose={() => setActiveDropdown(null)}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="flex items-center space-x-4">
                        <div className="relative">
                            <div
                                onMouseEnter={() => setActiveDropdown('user')}
                                onMouseLeave={() => setActiveDropdown(null)}
                            >
                                <button
                                    className="flex items-center space-x-1 text-gray-600 hover:text-moya-primary transition-colors duration-200">
                                    <User className="w-5 h-5"/>
                                    <span className="text-sm font-medium">
                    {isLoggedIn ? user?.nickname : '게스트'}
                  </span>
                                    <ChevronDown className="w-4 h-4"/>
                                </button>
                                <NavDropdown
                                    type="user"
                                    items={userItems}
                                    isOpen={activeDropdown === 'user'}
                                    onClose={() => setActiveDropdown(null)}
                                />
                            </div>
                        </div>
                        {!isLoggedIn && (
                            <button
                                onClick={handleLogin}
                                className="bg-moya-primary hover:bg-moya-secondary text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors duration-200 shadow-sm hover:shadow-md"
                            >
                                로그인
                            </button>
                        )}
                    </div>
                </nav>
            </div>

            <MobileMenu
                isOpen={isMobileMenuOpen}
                onClose={() => setIsMobileMenuOpen(false)}
                menuItems={[...roadmapItems, ...studyItems]}
            />
        </header>
    );
};

export default Header;