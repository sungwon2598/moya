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
    <div className="flex min-h-screen items-start justify-center px-4 py-12">
      <Card.Card className="w-full max-w-xl rounded-2xl border-0 bg-white p-6 text-neutral-950 shadow-sm">
        <div className="mb-6 flex items-center gap-4">
          <Avatar.Avatar className="h-16 w-16">
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

        <div className="border-t pt-4">
          <div className="flex items-center gap-2">
            <Ticket className="h-5 w-5 text-gray-600" />
            <p className="text-base font-medium">MOYA 포인트:</p>
            <p className="text-base text-gray-700">0</p>
          </div>
        </div>

        <Link to="/settings/profile" className="flex items-center gap-2">
          <Settings className="h-5 w-5 text-gray-600" />
          <p className="text-base font-medium">프로필 설정</p>
        </Link>
      </Card.Card>
    </div>
  );
};

export { MyPage };
