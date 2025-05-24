import { Button } from '@/components';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { useMyRoadmapList } from '@/features/myinfo/api/getRoadmap';
import { ArrowRight } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

type RoadmapData = {
  duration: number;
  id: number;
  mainCategory: string;
  subCategory: string;
};

export default function MyRoadmap() {
  const navigate = useNavigate();
  const { isAuthenticated: isLoggedIn } = useAuth();
  const { data: roadmapList } = useMyRoadmapList();

  const emojiList = ['💻', '🎮', '🎨', '📚', '🧠', '📈', '🧩', '🌱', '🪄', '💡'];
  const getConsistentEmoji = (id: number) => {
    return emojiList[id % emojiList.length];
  };

  const roadmapCreatePageMoveHandler = () => {
    if (!isLoggedIn) {
      return alert('로그인이 필요한 서비스입니다.');
    } else {
      navigate('/roadmap/create');
    }
  };
  //로드맵 없을때
  if (roadmapList?.length === 0)
    return (
      <div className="p-11 text-center">
        <h3>아직 로드맵을 생성하지 않았어요.</h3>
        <h5 className="text-neutral-600">나에게 딱 맞는 로드맵 생성해보세요</h5>
        <Button onClick={roadmapCreatePageMoveHandler} className="mt-6">
          나만의 로드맵 생성하기
          <ArrowRight color="#fff" className="h-5 w-5 transition-transform group-hover:translate-x-1" />
        </Button>
      </div>
    );
  //로드맵 있을때
  return (
    <>
      <h3 className="mt-6 px-4 text-2xl font-bold">내 로드맵</h3>
      <div className="grid gap-4 p-4 sm:grid-cols-2 md:grid-cols-3">
        {roadmapList?.map((roadmap: RoadmapData) => {
          const emoji = getConsistentEmoji(roadmap.id);

          return (
            <Link key={roadmap.id} to={`/my-info/roadmap/${roadmap.id}`}>
              <div className="group relative rounded-2xl bg-gradient-to-br from-blue-50 to-white p-6 shadow-lg transition-all duration-300 hover:scale-[1.02] hover:shadow-xl">
                <div className="mb-3 flex items-center gap-2">
                  <h3 className="text-xl font-semibold text-gray-800 transition-colors group-hover:text-blue-700">
                    {emoji} {roadmap.mainCategory}
                  </h3>
                </div>

                <p className="mb-10 text-sm text-gray-600">{roadmap.subCategory}</p>

                <p className="absolute bottom-4 right-4 rounded-full bg-blue-100 px-3 py-1 text-sm font-medium text-blue-600 shadow-sm">
                  목표 : {roadmap.duration}일
                </p>
              </div>
            </Link>
          );
        })}
      </div>
    </>
  );
}
