import { Card, Avatar, Tabs } from '@/components/shared/ui';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { useEffect } from 'react';
import { Ticket, Settings, Map, Users, Shield } from 'lucide-react';
import { Link } from 'react-router-dom';

const MyPage = () => {
  const { user } = useAuth();

  useEffect(() => {
    console.log(user);
  }, [user]);

  return (
    <div className="flex min-h-screen items-start justify-center px-4 py-12">
      <div className="w-full max-w-4xl">
        <Tabs.Tabs defaultValue="profile" className="w-full">
          <div className="w-full overflow-x-auto">
            <Tabs.TabsList className="inline-flex w-auto min-w-full">
              <Tabs.TabsTrigger value="profile" className="flex items-center gap-2 whitespace-nowrap">
                <Settings className="h-4 w-4" />
                프로필 설정
              </Tabs.TabsTrigger>
              <Tabs.TabsTrigger value="roadmap" className="flex items-center gap-2 whitespace-nowrap">
                <Map className="h-4 w-4" />내 로드맵
              </Tabs.TabsTrigger>
              <Tabs.TabsTrigger value="study" className="flex items-center gap-2 whitespace-nowrap">
                <Users className="h-4 w-4" />내 스터디
              </Tabs.TabsTrigger>
              {user?.data.roles?.includes('USER') && (
                <Tabs.TabsTrigger value="admin" className="flex items-center gap-2 whitespace-nowrap">
                  <Shield className="h-4 w-4" />
                  관리자
                </Tabs.TabsTrigger>
              )}
            </Tabs.TabsList>
          </div>

          <Tabs.TabsContent value="profile" className="mt-6">
            <Card.Card className="w-full rounded-2xl border-0 bg-white p-6 shadow-sm">
              <div className="space-y-6">
                <div className="flex items-center gap-4">
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
              </div>
            </Card.Card>
          </Tabs.TabsContent>

          <Tabs.TabsContent value="roadmap" className="mt-6">
            <Card.Card className="w-full rounded-2xl border-0 bg-white p-6 shadow-sm">
              <div className="space-y-4">
                <h3 className="text-lg font-medium">내 로드맵</h3>
                <div className="py-8 text-center text-gray-500">아직 등록된 로드맵이 없습니다.</div>
              </div>
            </Card.Card>
          </Tabs.TabsContent>

          <Tabs.TabsContent value="study" className="mt-6">
            <Card.Card className="w-full rounded-2xl border-0 bg-white p-6 shadow-sm">
              <div className="space-y-4">
                <h3 className="text-lg font-medium">내 스터디</h3>
                <div className="py-8 text-center text-gray-500">아직 참여한 스터디가 없습니다.</div>
              </div>
            </Card.Card>
          </Tabs.TabsContent>

          {user?.data.roles?.includes('USER') && (
            <Tabs.TabsContent value="admin" className="mt-6">
              <Card.Card className="w-full rounded-2xl border-0 bg-white p-6 shadow-sm">
                <div className="space-y-4">
                  <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                    <div className="rounded-lg bg-gray-50 p-4">
                      <h4 className="mb-2 font-medium">모든 게시글</h4>
                      <p className="text-sm text-gray-600">게시글 관리</p>
                    </div>
                    <div className="rounded-lg bg-gray-50 p-4">
                      <h4 className="mb-2 font-medium">모든 스터디</h4>
                      <p className="text-sm text-gray-600">스터디 관리</p>
                    </div>
                  </div>
                </div>
              </Card.Card>
            </Tabs.TabsContent>
          )}
        </Tabs.Tabs>
      </div>
    </div>
  );
};

export { MyPage };
