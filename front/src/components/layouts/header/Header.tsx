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
import { useLocation } from 'react-router-dom';

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
  const [isHeaderVisible, setIsHeaderVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      setShowBorder(currentScrollY > 0);

      if (currentScrollY === 0) {
        setIsHeaderVisible(true);
      } else if (currentScrollY < lastScrollY) {
        setIsHeaderVisible(true);
      } else if (currentScrollY > lastScrollY && currentScrollY > 100) {
        setIsHeaderVisible(false);
      }

      setLastScrollY(currentScrollY);
    };

    let ticking = false;
    const scrollHandler = () => {
      if (!ticking) {
        requestAnimationFrame(() => {
          handleScroll();
          ticking = false;
        });
        ticking = true;
      }
    };

    window.addEventListener('scroll', scrollHandler, { passive: true });
    handleScroll();

    return () => window.removeEventListener('scroll', scrollHandler);
  }, [lastScrollY]);

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

  const location = useLocation();
  const isActive = location.pathname.startsWith('/notification');

  useEffect(() => {
    if (location.pathname === '/login/oauth2/code/google') {
      navigate('/', { replace: true });
    }
  }, [location.pathname, navigate]);

  const testBtn = () => {
    navigate('/oauth2/authorization/google');
  };

  return (
    <>
      <header
        className={`bg-background fixed top-0 z-50 w-full transition-transform duration-300 ease-in-out ${
          isHeaderVisible ? 'translate-y-0' : '-translate-y-full'
        } ${showBorder ? 'border-moya-black/10 border-b' : ''}`}>
        <div className="container mx-auto">
          <nav className="flex items-center justify-between h-16 px-4">
            <div className="flex items-center">
              <button
                className="p-2 -ml-2 hover:text-moya-primary opacity-60 md:hidden"
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                aria-label="메뉴 열기">
                <Menu className="w-6 h-6" />
              </button>

              <Link to="/" className="flex items-center ml-2 space-x-3 cursor-pointer md:ml-0">
                <span className="text-xl font-bold text-moya-primary">MOYA</span>
              </Link>

              <div className="items-center hidden gap-1 ml-8 md:flex">
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
                    className={`hover:text-moya-primary ${isActive ? 'text-moya-primary bg-gray-50' : ''} flex items-center rounded-md px-3 py-2 text-gray-600 transition-colors duration-300 hover:rounded-md hover:bg-gray-50 ${
                      isActive ? 'text-moya-primary rounded-md bg-gray-50' : ''
                    }`}
                    onClick={() => {
                      navigate('/notifications');
                    }}>
                    <Bell className="w-5 h-5" />
                  </button>
                  <button
                    onClick={handleCreateStudy}
                    className="hidden px-4 py-2 text-sm font-medium text-white transition-colors duration-200 rounded-full bg-moya-secondary hover:bg-moya-secondary/90 md:flex">
                    스터디 모집하기
                  </button>
                  <DropdownMenu modal={false}>
                    <DropdownMenuTrigger className="flex items-center space-x-2 outline-none">
                      <Avatar className="w-8 h-8">
                        {user?.data.profileImageUrl ? (
                          <AvatarImage src={user?.data.profileImageUrl} alt={user.data.nickname} />
                        ) : (
                          <AvatarFallback>{user?.nickname?.charAt(0).toUpperCase()}</AvatarFallback>
                        )}
                      </Avatar>
                      {/* <span className="text-sm font-medium">{user?.data.nickname}</span> */}
                      <ChevronDown className="w-4 h-4" />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="mt-3.5 w-56">
                      <UserDropdown user={user} isLogin={isLogin} />
                    </DropdownMenuContent>
                  </DropdownMenu>
                </>
              ) : (
                <>
                  <button onClick={testBtn}>로그인 테스트 버튼 </button>
                  <GoogleLoginButton
                    theme="filled_blue"
                    size="large"
                    onSuccess={handleLoginSuccess}
                    onError={(error) => console.error('로그인 실패:', error)}
                  />
                </>
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
        <div className="flex items-center justify-between p-4 border-b border-gray-200">
          <span className="text-xl font-bold text-moya-primary">MOYA</span>
          <button
            onClick={() => setIsMobileMenuOpen(false)}
            className="p-2 text-gray-600 hover:text-moya-primary"
            aria-label="메뉴 닫기">
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="py-4">
          <button
            onClick={() => {
              handleCreateStudy();
              handleMobileMenuItemClick();
            }}
            className="flex items-center w-full px-4 py-3 text-emerald-500 hover:bg-gray-50">
            <Book className="w-5 h-5 mr-3" />
            <span>팀원 모집하기</span>
          </button>

          {navigationItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className="flex items-center px-4 py-3 text-gray-600 transition-colors duration-200 hover:text-moya-primary hover:bg-gray-50"
              onClick={handleMobileMenuItemClick}>
              <item.icon className="w-5 h-5 mr-3" />
              <span>{item.label}</span>
            </Link>
          ))}
          {isLogin &&
            authNavigationItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className="flex items-center px-4 py-3 text-gray-600 transition-colors duration-200 hover:text-moya-primary hover:bg-gray-50"
                onClick={handleMobileMenuItemClick}>
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
