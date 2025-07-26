import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Settings, LogOut, User, Map, Heart, LayoutGrid, PlusSquare } from 'lucide-react';
import { User as UserType } from '@/types/auth.types';
import { DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator } from '@/components/shared/ui/dropdown-menu';
import { useAuthStore } from '@/store/auth';

interface UserDropdownProps {
  user: UserType | null;
  isLogin: boolean;
}

const UserDropdown: React.FC<UserDropdownProps> = ({ user }) => {
  const navigate = useNavigate();
  const { logoutUser } = useAuthStore();
  console.log(user?.data?.roles);

  const isAdmin = user?.data?.roles?.includes('ADMIN');

  const handleLogout = async () => {
    try {
      await logoutUser();
      navigate('/');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <>
      <DropdownMenuLabel>
        <div className="flex items-center space-x-3">
          {user?.data?.profileImageUrl ? (
            <img src={user?.data.profileImageUrl} alt="프로필" className="w-10 h-10 rounded-full" />
          ) : (
            <div className="flex items-center justify-center w-10 h-10 bg-gray-200 rounded-full">
              <User className="w-6 h-6 text-gray-600" />
            </div>
          )}
          <div>
            <p className="text-sm font-semibold">{user?.data?.nickname}</p>
            <p className="text-sm text-gray-500">{user?.data?.email}</p>
          </div>
        </div>
      </DropdownMenuLabel>
      <DropdownMenuSeparator />

      <DropdownMenuItem asChild>
        <Link to="/my-info" className="flex items-center">
          <User className="w-4 h-4 mr-2" />
          마이페이지
        </Link>
      </DropdownMenuItem>

      <DropdownMenuItem asChild>
        <Link to="/my-info/roadmap" className="flex items-center">
          <Map className="w-4 h-4 mr-2" />내 로드맵
        </Link>
      </DropdownMenuItem>

      <DropdownMenuItem asChild>
        <Link to="/my-favorite-roadmap" className="flex items-center">
          <Heart className="w-4 h-4 mr-2" />내 관심 로드맵
        </Link>
      </DropdownMenuItem>

      <DropdownMenuItem asChild>
        <Link to="/settings/profile" className="flex items-center">
          <Settings className="w-4 h-4 mr-2" />
          프로필 설정
        </Link>
      </DropdownMenuItem>

      {isAdmin && (
        <>
          <DropdownMenuSeparator />
          <DropdownMenuItem asChild>
            <Link to="/go/categorys" className="flex items-center">
              <LayoutGrid className="w-4 h-4 mr-2" />
              카테고리 관리
            </Link>
          </DropdownMenuItem>
          <DropdownMenuItem asChild>
            <Link to="/go/create-sample" className="flex items-center">
              <PlusSquare className="w-4 h-4 mr-2" />
              샘플 제작
            </Link>
          </DropdownMenuItem>
        </>
      )}

      <DropdownMenuSeparator />
      <DropdownMenuItem onClick={handleLogout} className="text-red-600">
        <LogOut className="w-4 h-4 mr-2" />
        로그아웃
      </DropdownMenuItem>
    </>
  );
};

export default UserDropdown;
