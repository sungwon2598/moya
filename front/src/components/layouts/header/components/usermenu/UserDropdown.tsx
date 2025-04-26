import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Settings, LogOut, User, Map, Heart, LayoutGrid, PlusSquare } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { logoutUser } from '@/components/features/auth/store/authSlice.ts';
import { User as UserType } from '@/components/features/auth/types/auth.types.ts';
import { AppDispatch } from '@/store/store';
import GoogleLoginButton from '../../../../features/auth/components/GoogleLoginButton.tsx';
import { useAuth } from '../../../../features/auth/hooks/useAuth.ts';

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
      // @ts-ignore
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
            <MenuItem icon={Map} text="내 로드맵" href="/my-info/roadmap" onClick={onClose} />
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

export default UserDropdown;
