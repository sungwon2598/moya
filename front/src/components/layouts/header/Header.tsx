import React, { useEffect, useRef, useState } from 'react';
import { Home, Book, ChevronDown, MessageCircle, Menu, X, Bell } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import NavItem from './components/NavItem.tsx';
import UserDropdown from './components/usermenu/UserDropdown.tsx';
import { useAuth } from '../../features/auth/hooks/useAuth.ts';
import GoogleLoginButton from '../../features/auth/components/GoogleLoginButton.tsx';
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from '@/components/shared/ui/dropdown-menu';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/shared/ui/avatar';
import { useAuthStore } from '@/store/auth.ts';
import { GoogleAuthResponse } from '../../features/auth/types/auth.types';

const navigationItems = [
  {
    label: '로드맵',
    path: '/roadmap/create',
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
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const mobileMenuRef = useRef<HTMLDivElement>(null);
  const { isLogin, user } = useAuthStore();
  const navigate = useNavigate();
  const { handleGoogleLogin } = useAuth();
  const [showBorder, setShowBorder] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setShowBorder(window.scrollY > 0);
    };

    window.addEventListener('scroll', handleScroll);

    handleScroll();

    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
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

  const handleLoginSuccess = async (authData: GoogleAuthResponse) => {
    try {
      await handleGoogleLogin(authData);
      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <>
      <header className={`fixed top-0 z-50 w-full bg-white ${showBorder ? 'border-moya-black/10 border-b' : ''}`}>
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

              <div className="ml-8 hidden items-center gap-1 md:flex">
                {navigationItems.map((item) => (
                  <NavItem key={item.path} {...item} />
                ))}
                {isLogin && authNavigationItems.map((item) => <NavItem key={item.path} {...item} />)}
              </div>
            </div>

            <div className="relative flex items-center space-x-4">
              {isLogin ? (
                <>
                  <button
                    className="hover:text-moya-primary flex items-center px-3 py-2 text-gray-600 transition-colors duration-300 hover:rounded-md hover:bg-gray-50"
                    onClick={() => {
                      navigate('/notifications');
                    }}>
                    <Bell className="h-5 w-5" />
                  </button>
                  <button
                    onClick={handleCreateStudy}
                    className="bg-moya-secondary hover:bg-moya-secondary/90 hidden rounded-full px-4 py-2 text-sm font-medium text-white transition-colors duration-200 md:flex">
                    스터디 모집하기
                  </button>
                  <DropdownMenu modal={false}>
                    <DropdownMenuTrigger className="flex items-center space-x-2 outline-none">
                      <Avatar className="h-8 w-8">
                        {user?.data.profileImageUrl ? (
                          <AvatarImage src={user?.data.profileImageUrl} alt={user.data.nickname} />
                        ) : (
                          <AvatarFallback>{user?.nickname?.charAt(0).toUpperCase()}</AvatarFallback>
                        )}
                      </Avatar>
                      {/* <span className="text-sm font-medium">{user?.data.nickname}</span> */}
                      <ChevronDown className="h-4 w-4" />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="mt-3.5 w-56">
                      <UserDropdown user={user} isLogin={isLogin} />
                    </DropdownMenuContent>
                  </DropdownMenu>
                </>
              ) : (
                <GoogleLoginButton
                  theme="filled_blue"
                  size="large"
                  onSuccess={handleLoginSuccess}
                  onError={(error) => console.error('로그인 실패:', error)}
                />
              )}
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
              className="hover:text-moya-primary flex items-center px-4 py-3 text-gray-600 transition-colors duration-200 hover:bg-gray-50"
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
                className="hover:text-moya-primary flex items-center px-4 py-3 text-gray-600 transition-colors duration-200 hover:bg-gray-50"
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
