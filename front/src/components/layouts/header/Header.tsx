import React, { useEffect, useRef, useState } from 'react';
import { Home, Book, User, ChevronDown, MessageCircle, Menu, X } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import NavItem from './components/NavItem.tsx';
import { useSelector } from 'react-redux';
import { RootState } from '../../../store/store.ts';
import UserDropdown from './components/usermenu/UserDropdown.tsx';

const navigationItems = [
  {
    label: '로드맵',
    path: '/roadmap/preview',
    type: 'A' as const,
    icon: Home,
  },
  {
    label: '스터디',
    path: '/study',
    type: 'A' as const,
    icon: Book,
  },
];

const authNavigationItems = [
  {
    label: '채팅',
    path: '/chat',
    type: 'A' as const,
    icon: MessageCircle,
  },
];

export const Header: React.FC = () => {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const mobileMenuRef = useRef<HTMLDivElement>(null);
  const { isLogin, user } = useSelector((state: RootState) => state.auth);
  const navigate = useNavigate();

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
    navigate('/study/create');
  };

  return (
    <>
      <header className="border-moya-primary/10 fixed top-0 z-50 w-full border-b bg-white">
        <div className="container mx-auto">
          <nav className="flex h-16 items-center justify-between px-4">
            <div className="flex items-center">
              <button
                className="hover:text-moya-primary -ml-2 p-2 text-gray-600 md:hidden"
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                aria-label="메뉴 열기">
                <Menu className="h-6 w-6" />
              </button>

              <Link to="/" className="ml-2 flex cursor-pointer items-center space-x-3 md:ml-0">
                <span className="text-moya-primary text-xl font-bold">MOYA</span>
              </Link>

              <div className="ml-8 hidden items-center md:flex">
                {navigationItems.map((item) => (
                  <NavItem key={item.path} {...item} />
                ))}
                {isLogin && authNavigationItems.map((item) => <NavItem key={item.path} {...item} />)}
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <button
                onClick={handleCreateStudy}
                className="hidden rounded-full bg-emerald-500 px-4 py-2 text-sm font-medium text-white transition-colors duration-200 hover:bg-emerald-600 md:flex">
                스터디 모집하기
              </button>

              <div ref={dropdownRef}>
                <button
                  className="hover:text-moya-primary flex items-center space-x-1 text-gray-600 transition-colors duration-200"
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  aria-expanded={isDropdownOpen}
                  aria-haspopup="true">
                  {isLogin && user?.profileImageUrl ? (
                    <img src={user.profileImageUrl} alt="프로필" className="h-5 w-5 rounded-full" />
                  ) : (
                    <User className="h-5 w-5" />
                  )}
                  <span className="text-sm font-medium">{isLogin ? user?.nickname : '게스트'}</span>
                  <ChevronDown className="h-4 w-4" />
                </button>
                {isDropdownOpen && (
                  <UserDropdown user={user} isLogin={isLogin} onClose={() => setIsDropdownOpen(false)} />
                )}
              </div>
            </div>
          </nav>
        </div>
      </header>

      <div
        className={`fixed inset-0 z-50 bg-black bg-opacity-50 transition-opacity duration-200 ${
          isMobileMenuOpen ? 'opacity-100' : 'pointer-events-none opacity-0'
        }`}
        aria-hidden="true"
      />
      <div
        ref={mobileMenuRef}
        className={`fixed left-0 top-0 z-50 h-full w-64 transform bg-white transition-transform duration-200 ease-in-out ${
          isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}>
        <div className="flex items-center justify-between border-b border-gray-200 p-4">
          <span className="text-moya-primary text-xl font-bold">MOYA</span>
          <button
            onClick={() => setIsMobileMenuOpen(false)}
            className="hover:text-moya-primary p-2 text-gray-600"
            aria-label="메뉴 닫기">
            <X className="h-6 w-6" />
          </button>
        </div>

        <div className="py-4">
          <button
            onClick={() => {
              handleCreateStudy();
              handleMobileMenuItemClick();
            }}
            className="flex w-full items-center px-4 py-3 text-emerald-500 hover:bg-gray-50">
            <Book className="mr-3 h-5 w-5" />
            <span>팀원 모집하기</span>
          </button>

          {navigationItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className="hover:text-moya-primary flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50"
              onClick={handleMobileMenuItemClick}>
              <item.icon className="mr-3 h-5 w-5" />
              <span>{item.label}</span>
            </Link>
          ))}
          {isLogin &&
            authNavigationItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className="hover:text-moya-primary flex items-center px-4 py-3 text-gray-600 hover:bg-gray-50"
                onClick={handleMobileMenuItemClick}>
                <item.icon className="mr-3 h-5 w-5" />
                <span>{item.label}</span>
              </Link>
            ))}
        </div>
      </div>
    </>
  );
};

export default Header;
