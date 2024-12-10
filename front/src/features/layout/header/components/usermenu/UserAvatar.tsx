import React from 'react';
import { User } from '@/features/auth/types/auth.types.ts';
import { UserIcon } from 'lucide-react';
import { cn } from '@/shared/utils/cn.ts';

interface UserAvatarProps {
    user: User | null;
    size?: number;
    className?: string;
}

export const UserAvatar: React.FC<UserAvatarProps> = ({
                                                          user,
                                                          size = 32,
                                                          className
                                                      }) => {
    // 이미지 로드 실패 시 fallback으로 전환하는 핸들러
    const handleImageError = (e: React.SyntheticEvent<HTMLImageElement>) => {
        e.currentTarget.style.display = 'none';
        e.currentTarget.nextElementSibling?.classList.remove('hidden');
    };

    const containerClassName = cn(
        "flex items-center justify-center rounded-full overflow-hidden",
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
                        className="w-full h-full object-cover"
                        onError={handleImageError}
                    />
                    {/* Fallback hidden by default */}
                    <div className="hidden w-full h-full bg-gray-200 flex items-center justify-center">
                        <UserIcon size={size * 0.75} className="text-gray-600" />
                    </div>
                </>
            ) : (
                <div className="w-full h-full bg-gray-200 flex items-center justify-center">
                    <UserIcon size={size * 0.75} className="text-gray-600" />
                </div>
            )}
        </div>
    );
};
