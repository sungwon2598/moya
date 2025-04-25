import React, { useRef, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '@/store/store';
import { User as UserType } from '@/components/features/auth/types/auth.types';
import { Settings, LogOut, User, Map, Heart, LayoutGrid, PlusSquare, Menu, X } from 'lucide-react';
import { logoutUser } from '@/components/features/auth/store/authSlice';
import GoogleLoginButton from '@/components/features/auth/components/GoogleLoginButton';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { cn } from '@/shared/utils/cn';

// UserAvatar 컴포넌트
interface UserAvatarProps {
  user: UserType | null;
  size?: number;
  className?: string;
}

const UserAvatar: React.FC<UserAvatarProps> = ({ user, size = 32, className }) => {
  const handleImageError = (e: React.SyntheticEvent<HTMLImageElement>) => {
    e.currentTarget.style.display = 'none';
    e.currentTarget.nextElementSibling?.classList.remove('hidden');
  };

  const containerClassName = cn(
    'flex items-center justify-center rounded-full overflow-hidden',
    `w-[${size}px] h-[${size}px]`,
    className
  );

  return (
    <div className={containerClassName}>
      {user?.profileImageUrl ? (
        <>
          <img
            src={user.profileImageUrl}
            alt={`${user.nickname}'s profile`}
            className="object-cover w-full h-full"
            onError={handleImageError}
          />
          <div className="flex items-center justify-center hidden w-full h-full bg-gray-200">
            <User size={size * 0.75} className="text-gray-600" />
          </div>
        </>
      ) : (
        <div className="flex items-center justify-center w-full h-full bg-gray-200">
          <User size={size * 0.75} className="text-gray-600" />
        </div>
      )}
    </div>
  );
};

// UserDropdown 컴포넌트
interface UserDropdownProps {
  user: UserType | null;
  isLogin: boolean;
  onClose: () => void;
}

const UserDropdown: React.FC<UserDropdownProps> = ({ user, onClose }) => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();
  const { isAuthenticated, handleGoogleLogin } = useAuth();

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  const handleLogout = async () => {
    try {
      const result = await dispatch(logoutUser()).unwrap();
      // @ts-expect-error
      if (result.result) {
        onClose();
      }
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  const handleLoginSuccess = async (authData: any) => {
    try {
      await handleGoogleLogin(authData);
      navigate('/');
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div className="absolute right-0 top-[64px] z-50 w-56 rounded-lg border border-gray-200 bg-white py-1 shadow-lg">
      {isAuthenticated && user ? (
        <>
          <div className="flex items-center px-4 py-3 space-x-3 border-b border-gray-100">
            {user?.profileImageUrl ? (
              <img src={user.profileImageUrl} alt="프로필" className="w-10 h-10 rounded-full" />
            ) : (
              <div className="flex items-center justify-center w-10 h-10 bg-gray-200 rounded-full">
                <User className="w-6 h-6 text-gray-600" />
              </div>
            )}
            <div>
              <p className="text-sm font-semibold text-gray-800">{user.nickname}</p>
              <p className="text-sm text-gray-600">{user.email}</p>
            </div>
          </div>

          <div className="py-1">
            <MenuItem icon={Map} text="내 로드맵" href="/my-roadmap" onClick={onClose} />
            <MenuItem icon={Heart} text="내 관심 로드맵" href="/my-favorite-roadmap" onClick={onClose} />
            <MenuItem icon={Settings} text="프로필 설정" href="/settings/profile" onClick={onClose} />

            {isAdmin && (
              <>
                <div className="my-1 border-t border-gray-100"></div>
                <MenuItem icon={LayoutGrid} text="카테고리 관리" href="/go/categorys" onClick={onClose} />
                <MenuItem icon={PlusSquare} text="샘플 제작" href="/go/create-sample" onClick={onClose} />
              </>
            )}
          </div>

          <div className="border-t border-gray-100">
            <button
              onClick={handleLogout}
              className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50">
              <LogOut size={16} className="mr-2" />
              로그아웃
            </button>
          </div>
        </>
      ) : (
        <div className="py-1">
          <GoogleLoginButton
            theme="filled_blue"
            size="large"
            onSuccess={handleLoginSuccess}
            onError={(error) => console.error('로그인 실패:', error)}
          />
        </div>
      )}
    </div>
  );
};

interface MenuItemProps {
  icon: React.ElementType;
  text: string;
  href: string;
  onClick: () => void;
  size?: number;
}

const MenuItem: React.FC<MenuItemProps> = ({ icon: Icon, text, href, onClick }) => (
  <Link to={href} className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50" onClick={onClick}>
    <Icon size={16} className="mr-2" />
    {text}
  </Link>
);

// UserMenu 컴포넌트
const UserMenu: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const { user, isLogin } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className="relative" ref={dropdownRef}>
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center focus:outline-none"
        aria-expanded={isOpen}
        aria-haspopup="true">
        {isLogin && user ? (
          <UserAvatar user={user} />
        ) : (
          <div className="flex items-center justify-center w-8 h-8 bg-gray-200 rounded-full">
            <User className="w-4 h-4 text-gray-600" />
          </div>
        )}
      </button>

      {isOpen && <UserDropdown user={user} onClose={() => setIsOpen(false)} isLogin={isLogin} />}
    </div>
  );
};

// 메인 Header 컴포넌트
export const Header: React.FC = () => {
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="fixed top-0 z-50 w-full border-b bg-moya/95 backdrop-blur">
      <div className="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* 로고 */}
          <Link to="/" className="flex items-center space-x-2">
            <span className="text-xl font-bold text-moya-foreground">MOYA</span>
          </Link>

          {/* 데스크톱 메뉴 */}
          <div className="hidden md:flex md:items-center md:space-x-4">
            <button
              onClick={() => navigate('/roadmap')}
              className="px-4 py-2 text-sm font-medium rounded-md text-moya-foreground hover:bg-moya/10">
              로드맵
            </button>
            <button
              onClick={() => navigate('/study')}
              className="px-4 py-2 text-sm font-medium rounded-md text-moya-foreground hover:bg-moya/10">
              스터디
            </button>
            <button
              onClick={() => navigate('/study/write')}
              className="px-4 py-2 text-sm font-medium rounded-md text-moya-foreground hover:bg-moya/10">
              스터디 모집하기
            </button>
            <UserMenu />
          </div>

          {/* 모바일 메뉴 버튼 */}
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="p-2 rounded-md text-moya-foreground hover:bg-moya/10 md:hidden">
            {isMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>
      </div>

      {/* 모바일 메뉴 */}
      {isMenuOpen && (
        <div className="absolute z-50 w-full border-b bg-moya/95 top-16 backdrop-blur md:hidden">
          <div className="px-4 mx-auto max-w-7xl sm:px-6 lg:px-8">
            <div className="flex flex-col py-4 space-y-2">
              <button
                onClick={() => {
                  navigate('/roadmap');
                  setIsMenuOpen(false);
                }}
                className="w-full px-4 py-2 text-sm font-medium text-left rounded-md text-moya-foreground hover:bg-moya/10">
                로드맵
              </button>
              <button
                onClick={() => {
                  navigate('/study');
                  setIsMenuOpen(false);
                }}
                className="w-full px-4 py-2 text-sm font-medium text-left rounded-md text-moya-foreground hover:bg-moya/10">
                스터디
              </button>
              <button
                onClick={() => {
                  navigate('/study/write');
                  setIsMenuOpen(false);
                }}
                className="w-full px-4 py-2 text-sm font-medium text-left rounded-md text-moya-foreground hover:bg-moya/10">
                스터디 모집하기
              </button>
              <div className="px-4 py-2">
                <UserMenu />
              </div>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};
