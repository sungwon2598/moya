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

  console.log(user?.data);

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
          {user?.profileImageUrl ? (
            <img src={user?.profileImageUrl} alt="프로필" className="h-10 w-10 rounded-full" />
          ) : (
            <div className="flex h-10 w-10 items-center justify-center rounded-full bg-gray-200">
              <User className="h-6 w-6 text-gray-600" />
            </div>
          )}
          <div>
            <p className="text-sm font-semibold">{user?.nickname}</p>
            <p className="text-sm text-gray-500">{user?.data?.email}</p>
          </div>
        </div>
      </DropdownMenuLabel>
      <DropdownMenuSeparator />

      <DropdownMenuItem asChild>
        <Link to="/my-info" className="flex items-center">
          <User className="mr-2 h-4 w-4" />
          마이페이지
        </Link>
      </DropdownMenuItem>

      <DropdownMenuItem asChild>
        <Link to="/my-info/roadmap" className="flex items-center">
          <Map className="mr-2 h-4 w-4" />내 로드맵
        </Link>
      </DropdownMenuItem>

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
