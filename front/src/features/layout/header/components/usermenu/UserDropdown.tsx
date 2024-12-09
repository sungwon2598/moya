import React from 'react';
import { Link } from 'react-router-dom';
import { Settings, LogOut, UserPlus, LogIn } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { logout } from '@/features/auth/store/authSlice';
import { User } from '@/features/auth/types/auth.types';

interface UserDropdownProps {
    user: User | null;
    isLogin: boolean;
    onClose: () => void;
}

export const UserDropdown: React.FC<UserDropdownProps> = ({ user, isLogin, onClose }) => {
    const dispatch = useDispatch();

    const handleLogout = () => {
        dispatch(logout());
        onClose();
    };

    return (
        <div className="absolute right-0 top-[64px] w-56 bg-white rounded-lg shadow-lg py-1 z-50 border border-gray-200">
            {isLogin && user ? (
                // 로그인 상태 메뉴
                <>
                    {/* Profile Section */}
                    <div className="px-4 py-3 border-b border-gray-100">
                        <p className="text-sm font-semibold text-gray-800">{user.nickName}</p>
                        <p className="text-sm text-gray-600">{user.email}</p>
                    </div>

                    {/* Menu Items */}
                    <div className="py-1">
                        <MenuItem
                            icon={Settings}
                            text="프로필 설정"
                            href="/settings/profile"
                            onClick={onClose}
                        />
                    </div>

                    {/* Logout Section */}
                    <div className="border-t border-gray-100">
                        <button
                            onClick={handleLogout}
                            className="flex items-center w-full px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                        >
                            <LogOut size={16} className="mr-2" />
                            로그아웃
                        </button>
                    </div>
                </>
            ) : (
                // 게스트 상태 메뉴
                <div className="py-1">
                    <MenuItem
                        icon={LogIn}
                        text="로그인"
                        href="/login"
                        onClick={onClose}
                    />
                    <MenuItem
                        icon={UserPlus}
                        text="회원가입"
                        href="/signup"
                        onClick={onClose}
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
}

const MenuItem: React.FC<MenuItemProps> = ({ icon: Icon, text, href, onClick }) => (
    <Link
        to={href}
        className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
        onClick={onClick}
    >
        <Icon size={16} className="mr-2" />
        {text}
    </Link>
);
