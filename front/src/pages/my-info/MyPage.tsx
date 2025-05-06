import { Card, Avatar } from '@/components/shared/ui';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { useEffect } from 'react';
import { Ticket, Settings } from 'lucide-react';
import { Link } from 'react-router-dom';

const MyPage = () => {
  const { user } = useAuth();

  useEffect(() => {
    console.log(user);
  }, [user]);

  return (
    <div className="flex items-start justify-center min-h-screen px-4 py-12">
      <Card.Card className="w-full max-w-xl p-6 bg-white border-0 shadow-sm rounded-2xl">
        <div className="flex items-center gap-4 mb-6">
          <Avatar.Avatar className="w-16 h-16">
            {user?.data.profileImageUrl ? (
              <Avatar.AvatarImage src={user?.data.profileImageUrl} alt={user.data.nickname} />
            ) : (
              <Avatar.AvatarFallback className="text-lg">
                {user?.data.nickname?.charAt(0).toUpperCase()}
              </Avatar.AvatarFallback>
            )}
          </Avatar.Avatar>
          <div className="flex-1">
            <p className="text-2xl font-semibold">{user?.data.nickname}</p>
            <p className="text-sm text-gray-500">{user?.data.email}</p>
          </div>
        </div>

        <div className="pt-4 border-t">
          <div className="flex items-center gap-2">
            <Ticket className="w-5 h-5 text-gray-600" />
            <p className="text-base font-medium">재화:</p>
            <p className="text-base text-gray-700">0</p>
          </div>
        </div>

        <Link to="/settings/profile" className="flex items-center gap-2">
          <Settings className="w-5 h-5 text-gray-600" />
          <p className="text-base font-medium">프로필 설정</p>
        </Link>
      </Card.Card>
    </div>
  );
};

export { MyPage };
