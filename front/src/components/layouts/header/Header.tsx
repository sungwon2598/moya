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

// const authNavigationItems = [
//   {
//     label: '채팅',
//     path: '/chat',
//     type: 'A' as const,
//     icon: MessageCircle,
//   },
// ];

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
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get('accessToken');
    const refreshToken = urlParams.get('refreshToken');
    // const email = urlParams.get('email');
    // const nickname = urlParams.get('nickname');
    // const profileImage = urlParams.get('profileImage');

    console.log(urlParams);
    if (accessToken && refreshToken) {
      try {
        console.log('로그인 성공');
        console.log('Access Token:', accessToken);
        // console.log('사용자 이메일:', email);

        localStorage.setItem('accessToken', accessToken);
        if (refreshToken) {
          localStorage.setItem('refreshToken', refreshToken);
        }

        window.history.replaceState({}, document.title, '/');
      } catch (error) {
        console.error('토큰 처리 중 오류:', error);

        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');

        window.history.replaceState({}, document.title, '/');
      }
    }

    const error = urlParams.get('error');
    if (error) {
      console.error('OAuth 로그인 실패:', error);
      alert('로그인에 실패했습니다.');

      window.history.replaceState({}, document.title, '/');
    }
  }, []);

  const testLogin = () => {
    const isDev = import.meta.env.DEV;
    console.log('개발 환경', isDev);

    if (isDev) {
      window.location.href = '/oauth2/authorization/google';
    } else {
      window.location.href = 'https://api.moyastudy.com/oauth2/authorization/google';
    }
  };
  // const testLogin = () => {
  //   const isDev = import.meta.env.DEV;
  //   const apiUrl = import.meta.env.VITE_API_URL;

  //   console.log('개발 환경', isDev);
  //   console.log('API URL', apiUrl);

  //   const oauthUrl = `${apiUrl}/oauth2/authorization/google`;
  //   console.log('URL:', oauthUrl);

  //   window.location.href = oauthUrl;
  // };

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
                {/* {isLogin && authNavigationItems.map((item) => <NavItem key={item.path} {...item} />)} */}
              </div>
            </div>

            <div className="relative flex items-center space-x-4">
              {isLogin ? (
                <>
                  {/* <button
                    className={`hover:text-moya-primary ${isActive ? 'text-moya-primary bg-gray-50' : ''} flex items-center rounded-md px-3 py-2 text-gray-600 transition-colors duration-300 hover:rounded-md hover:bg-gray-50 ${
                      isActive ? 'text-moya-primary rounded-md bg-gray-50' : ''
                    }`}
                    onClick={() => {
                      navigate('/notifications');
                    }}>
                    <Bell className="w-5 h-5" />
                  </button> */}
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
                  <button
                    onClick={testLogin}
                    className="flex items-center gap-3 px-4 py-2 text-sm font-medium text-gray-700 transition-all duration-200 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 hover:shadow-md">
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
                    Google 로그인
                  </button>
                  {/* <GoogleLoginButton
                    theme="filled_blue"
                    size="large"
                    onSuccess={handleLoginSuccess}
                    onError={(error) => console.error('로그인 실패:', error)}
                  /> */}
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
          {/* {isLogin &&
            authNavigationItems.map((item) => (
              <Link
                key={item.path}
                to={item.path}
                className="flex items-center px-4 py-3 text-gray-600 transition-colors duration-200 hover:text-moya-primary hover:bg-gray-50"
                onClick={handleMobileMenuItemClick}>
                <item.icon className="w-5 h-5 mr-3" />
                <span>{item.label}</span>
              </Link>
            ))} */}
        </div>
      </div>
    </>
  );
};

export default Header;
