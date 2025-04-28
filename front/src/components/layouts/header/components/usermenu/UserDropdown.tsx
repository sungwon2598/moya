import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Settings, LogOut, User, Map, Heart, LayoutGrid, PlusSquare } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { logoutUser } from '@/components/features/auth/store/authSlice.ts';
import { User as UserType } from '@/components/features/auth/types/auth.types.ts';
import { AppDispatch } from '@/store/store';
import { DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator } from '@/components/shared/ui/dropdown-menu';

interface UserDropdownProps {
  user: UserType | null;
  isLogin: boolean;
  onClose: () => void;
}

const UserDropdown: React.FC<UserDropdownProps> = ({ user }) => {
  const dispatch = useDispatch<AppDispatch>();
  const navigate = useNavigate();

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');

  const handleLogout = async () => {
    try {
      await dispatch(logoutUser()).unwrap();
      navigate('/');
    } catch (error) {
      console.error('Logout failed:', error);
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
        )}
        <div>
          <p className="text-sm font-semibold">{user?.nickname}</p>
          <p className="text-sm text-gray-500">{user?.email}</p>
        </div>
      </DropdownMenuLabel>
      <DropdownMenuSeparator />


          <div className="py-1">
            <MenuItem icon={Map} text="내 로드맵" href="/my-info/roadmap" onClick={onClose} />
            <MenuItem icon={Heart} text="내 관심 로드맵" href="/my-favorite-roadmap" onClick={onClose} />
            <MenuItem icon={Settings} text="프로필 설정" href="/settings/profile" onClick={onClose} />


      <DropdownMenuItem asChild>
        <Link to="/my-favorite-roadmap" className="flex items-center">
          <Heart className="mr-2 h-4 w-4" />내 관심 로드맵
        </Link>
      </DropdownMenuItem>

      <DropdownMenuItem asChild>
        <Link to="/settings/profile" className="flex items-center">
          <Settings className="mr-2 h-4 w-4" />
          프로필 설정
        </Link>
      </DropdownMenuItem>

      {isAdmin && (
        <>
          <DropdownMenuSeparator />
          <DropdownMenuItem asChild>
            <Link to="/go/categorys" className="flex items-center">
              <LayoutGrid className="mr-2 h-4 w-4" />
              카테고리 관리
            </Link>
          </DropdownMenuItem>
          <DropdownMenuItem asChild>
            <Link to="/go/create-sample" className="flex items-center">
              <PlusSquare className="mr-2 h-4 w-4" />
              샘플 제작
            </Link>
          </DropdownMenuItem>

        </>
      )}

      <DropdownMenuSeparator />
      <DropdownMenuItem onClick={handleLogout} className="text-red-600">
        <LogOut className="mr-2 h-4 w-4" />
        로그아웃
      </DropdownMenuItem>
    </>
  );
};

export default UserDropdown;
