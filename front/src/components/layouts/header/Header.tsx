import React, { useEffect, useRef, useState } from 'react';
import { Home, Book, ChevronDown, Menu, X } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import NavItem from './components/NavItem.tsx';
import UserDropdown from './components/usermenu/UserDropdown.tsx';
import { DropdownMenu, DropdownMenuContent, DropdownMenuTrigger } from '@/components/shared/ui/dropdown-menu';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/shared/ui/avatar';
import { useAuthStore } from '@/store/auth.ts';
import { toast } from 'sonner';

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

export const Header: React.FC = () => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const mobileMenuRef = useRef<HTMLDivElement>(null);

  const { isLogin, user, loading, checkLoginStatus } = useAuthStore();
  const navigate = useNavigate();
  const [showBorder, setShowBorder] = useState(false);
  const [isHeaderVisible, setIsHeaderVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);

  const [isLoggingIn, setIsLoggingIn] = useState(false);

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

  useEffect(() => {
    checkLoginStatus();
  }, [checkLoginStatus]);

  const handleGoogleLogin = () => {
    if (isLoggingIn) return;

    setIsLoggingIn(true);

    try {
      const isDev = import.meta.env.DEV;
      console.log(isDev);
      const baseUrl = isDev ? 'http://localhost:8080' : 'https://api.moyastudy.com';
      const oauthUrl = `${baseUrl}/oauth2/authorization/google`;

      console.log(oauthUrl);

      window.location.href = oauthUrl;

      setIsLoggingIn(true);
    } catch (error) {
      console.error('로그인 요청 중 오류:', error);
      toast.error('로그인 요청 중 오류가 발생했습니다.');
      setIsLoggingIn(false);
    }
  };

  return (
    <>
      <header
        className={`bg-background fixed top-0 z-50 w-full transition-transform duration-300 ease-in-out ${
          isHeaderVisible ? 'translate-y-0' : '-translate-y-full'
        } ${showBorder ? 'border-moya-black/10 border-b' : ''}`}>
        <div className="container mx-auto">
          <nav className="flex h-16 items-center justify-between px-4">
            <div className="flex items-center">
              <button
                className="hover:text-moya-primary -ml-2 p-2 opacity-60 md:hidden"
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
              </div>
            </div>

            <div className="relative flex items-center space-x-4">
              {loading ? (
                // 로딩 상태
                <div className="h-8 w-8 animate-spin rounded-full border-2 border-gray-300 border-t-blue-600" />
              ) : isLogin && user ? (
                <>
                  <button
                    onClick={handleCreateStudy}
                    className="bg-moya-secondary hover:bg-moya-secondary/90 hidden rounded-full px-4 py-2 text-sm font-medium text-white transition-colors duration-200 md:flex">
                    스터디 모집하기
                  </button>
                  <DropdownMenu modal={false}>
                    <DropdownMenuTrigger className="flex items-center space-x-2 outline-none">
                      <Avatar className="h-8 w-8">
                        {user.profileImageUrl ? (
                          <AvatarImage src={user.profileImageUrl} alt={user.nickname} />
                        ) : (
                          <AvatarFallback>{user.nickname}</AvatarFallback>
                        )}
                      </Avatar>
                      <ChevronDown className="h-4 w-4" />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="mt-3.5 w-56">
                      <UserDropdown user={user} isLogin={isLogin} />
                    </DropdownMenuContent>
                  </DropdownMenu>
                </>
              ) : (
                <button
                  onClick={handleGoogleLogin}
                  disabled={isLoggingIn}
                  className="flex items-center gap-3 rounded-lg border bg-blue-500 px-2 py-1 text-sm font-medium text-white transition-all duration-200 hover:bg-blue-600 hover:shadow-md disabled:cursor-not-allowed disabled:opacity-50">
                  <div className="rounded bg-white p-1">
                    {isLoggingIn ? (
                      <div className="h-5 w-5 animate-spin rounded-full border-2 border-gray-300 border-t-blue-600" />
                    ) : (
                      <svg width="20" height="20" viewBox="0 0 24 24">
                        <path
                          fill="#4285F4"
                          d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                        />
                        <path
                          fill="#34A853"
                          d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                        />
                        <path
                          fill="#FBBC05"
                          d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                        />
                        <path
                          fill="#EA4335"
                          d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                        />
                      </svg>
                    )}
                  </div>
                  {isLoggingIn ? '로그인 중...' : 'Google 로그인'}
                </button>
              )}
            </div>
          </nav>
        </div>
      </header>

      {/* 모바일 메뉴 오버레이 */}
      <div
        className={`fixed inset-0 z-50 bg-black bg-opacity-50 transition-opacity duration-200 ${
          isMobileMenuOpen ? 'opacity-100' : 'pointer-events-none opacity-0'
        }`}
        aria-hidden="true"
      />

      {/* 모바일 메뉴 */}
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
        </div>
      </div>
    </>
  );
};

export default Header;
