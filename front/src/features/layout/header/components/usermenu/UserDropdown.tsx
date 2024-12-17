import React from 'react';
import {Link, useNavigate} from 'react-router-dom';
import { Settings, LogOut } from 'lucide-react';
import { useDispatch } from 'react-redux';
import { logoutUser } from '@/features/auth/store/authSlice';
import { User } from '@/features/auth/types/auth.types';
import { AppDispatch } from '@/core/store/store';
import GoogleLoginButton from "../../../../auth/components/GoogleLoginButton.tsx";
import { useAuth} from '../../../../auth/hooks/useAuth.ts'

interface UserDropdownProps {
    user: User | null;
    isLogin: boolean;
    onClose: () => void;
}

export const UserDropdown: React.FC<UserDropdownProps> = ({ user, onClose }) => {
    const dispatch = useDispatch<AppDispatch>(); // AppDispatch 타입 지정
    const navigate = useNavigate(); // useNavigate hook

    const { isAuthenticated, handleGoogleLogin } = useAuth();


    const handleLogout = async () => {
        try {
            const result = await dispatch(logoutUser()).unwrap();
            if (result.success) {
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
        <div className="absolute right-0 top-[64px] w-56 bg-white rounded-lg shadow-lg py-1 z-50 border border-gray-200">
            {isAuthenticated && user ? (
                <>
                    <div className="px-4 py-3 border-b border-gray-100">
                        <p className="text-sm font-semibold text-gray-800">{user.nickname}</p>
                        <p className="text-sm text-gray-600">{user.email}</p>
                    </div>

                    <div className="py-1">
                        <MenuItem
                            icon={Settings}
                            text="프로필 설정"
                            href="/settings/profile"
                            onClick={onClose}
                        />
                    </div>

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
