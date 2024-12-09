import React from 'react';
import { User } from '@/features/auth/types/auth.types.ts';
import { UserIcon } from 'lucide-react';

interface UserAvatarProps {
    user: User | null;
    size?: number;
}

export const UserAvatar: React.FC<UserAvatarProps> = ({ user, size = 32 }) => {
    if (!user?.profileImage) {
        return (
            <div className="flex items-center justify-center w-8 h-8 bg-gray-200 rounded-full">
                <UserIcon size={size * 0.75} className="text-gray-600" />
            </div>
        );
    }

    return (
        <img
            src={user.profileImage}
            alt={`${user.nickName}'s profile`}
            className="w-8 h-8 rounded-full object-cover"
        />
    );
};
