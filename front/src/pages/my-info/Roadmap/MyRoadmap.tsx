import { Button } from '@/components';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { useMyRoadmapList } from '@/features/myinfo/api/getRoadmap';
import { ArrowRight, Target, Sparkles } from 'lucide-react';
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
  const gradientList = [
    'from-blue-400/20 via-purple-400/20 to-pink-400/20',
    'from-green-400/20 via-teal-400/20 to-blue-400/20',
    'from-pink-400/20 via-rose-400/20 to-orange-400/20',
    'from-orange-400/20 via-red-400/20 to-pink-400/20',
    'from-indigo-400/20 via-blue-400/20 to-cyan-400/20',
    'from-purple-400/20 via-pink-400/20 to-rose-400/20',
    'from-teal-400/20 via-green-400/20 to-emerald-400/20',
    'from-yellow-400/20 via-orange-400/20 to-red-400/20',
    'from-cyan-400/20 via-blue-400/20 to-indigo-400/20',
    'from-red-400/20 via-pink-400/20 to-purple-400/20',
  ];

  const getConsistentEmoji = (id: number) => {
    return emojiList[id % emojiList.length];
  };

  const getConsistentGradient = (id: number) => {
    return gradientList[id % gradientList.length];
  };

  const roadmapCreatePageMoveHandler = () => {
    if (!isLoggedIn) {
      return alert('로그인이 필요한 서비스입니다.');
    } else {
      navigate('/roadmap/create');
    }
  };

  // 로드맵 없을때
  if (roadmapList?.length === 0)
    return (
      <div className="relative flex min-h-[500px] flex-col items-center justify-center px-4 py-16 text-center">
        <div className="relative rounded-3xl border border-white/20 bg-white/40 p-12 shadow-2xl backdrop-blur-md">
          <div className="mb-8">
            <div className="relative">
              <div className="mb-4 animate-pulse text-7xl">📚</div>
              <Sparkles className="absolute -right-2 -top-2 h-6 w-6 animate-spin text-purple-400" />
            </div>
          </div>

          <h3 className="mb-4 text-2xl font-bold text-gray-800 sm:text-3xl">아직 로드맵을 생성하지 않았어요</h3>
          <p className="mb-8 max-w-md text-base text-gray-600 sm:text-lg">
            나에게 딱 맞는 학습 로드맵을 생성하고 체계적으로 공부해보세요
          </p>

          <Button
            onClick={roadmapCreatePageMoveHandler}
            className="group relative overflow-hidden rounded-2xl border-0 bg-gradient-to-r from-blue-500 to-purple-600 px-8 py-4 font-medium text-white backdrop-blur-sm transition-all duration-300 hover:scale-105 hover:from-blue-600 hover:to-purple-700 hover:shadow-2xl">
            <span className="relative z-10 flex items-center gap-2">
              나만의 로드맵 생성하기
              <ArrowRight className="h-5 w-5 transition-transform group-hover:translate-x-1" />
            </span>
            <div className="absolute inset-0 bg-gradient-to-r from-white/20 to-transparent opacity-0 transition-opacity duration-300 group-hover:opacity-100" />
          </Button>
        </div>
      </div>
    );

  return (
    <div className="relative mx-auto w-full max-w-7xl px-4 py-6">
      <div className="mb-8">
        <h3 className="flex items-center gap-2 text-2xl font-bold sm:text-3xl">
          <Sparkles className="h-6 w-6 text-purple-500" />내 스터디 로드맵
        </h3>
        <p className="mt-2 opacity-70">진행 중인 학습 로드맵을 확인하고 관리해보세요</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 sm:gap-6 lg:grid-cols-3 xl:grid-cols-4">
        {roadmapList?.map((roadmap: RoadmapData) => {
          const emoji = getConsistentEmoji(roadmap.id);
          const gradient = getConsistentGradient(roadmap.id);

          return (
            <Link
              key={roadmap.id}
              to={`/my-info/roadmap/${roadmap.id}?mainCategory=${encodeURIComponent(roadmap.mainCategory)}&subCategory=${encodeURIComponent(roadmap.subCategory)}`}
              className="group block w-full">
              <div className="relative h-full overflow-hidden rounded-3xl border border-white/20 bg-white/40 shadow-xl backdrop-blur-md transition-all duration-300 hover:-translate-y-2 hover:bg-white/50 hover:shadow-2xl">
                <div className={`bg-gradient-to-r ${gradient} border-b border-white/20 p-6 backdrop-blur-sm`}>
                  <div className="flex items-center justify-between">
                    <h4 className="mb-2 line-clamp-2 text-lg font-bold text-gray-900 transition-colors group-hover:text-blue-600">
                      <span className="text-4xl drop-shadow-sm">{emoji}</span> {roadmap.mainCategory}
                    </h4>
                    <div className="flex items-center gap-1 rounded-full border border-white/20 bg-white/30 px-3 py-1 text-sm font-medium text-gray-700 backdrop-blur-sm">
                      <Target className="h-3 w-3" />
                      {roadmap.duration}주
                    </div>
                  </div>
                </div>

                <div className="p-6">
                  <div className="mb-4">
                    <p className="line-clamp-2 rounded-lg bg-white/20 p-2 text-sm text-gray-600 backdrop-blur-sm">
                      {roadmap.subCategory || roadmap.mainCategory}
                    </p>
                  </div>
                </div>

                <div className="absolute inset-0 rounded-3xl bg-gradient-to-r from-blue-400/0 to-purple-400/0 transition-all duration-300 group-hover:from-blue-400/10 group-hover:to-purple-400/10" />
                <div className="absolute right-4 top-4 h-2 w-2 animate-pulse rounded-full bg-white/40" />
              </div>
            </Link>
          );
        })}
      </div>

      {/* 새 로드맵 추가 카드 */}
      <div className="mt-6">
        <button
          onClick={roadmapCreatePageMoveHandler}
          className="group flex w-full items-center justify-center gap-3 rounded-3xl border-2 border-dashed border-white/40 bg-white/30 p-6 backdrop-blur-md transition-all duration-300 hover:border-blue-400/50 hover:bg-white/50 hover:text-blue-600 hover:shadow-xl sm:w-auto">
          <div className="text-2xl">✨</div>
          <span className="font-medium opacity-70">새 로드맵 추가하기</span>
          <ArrowRight className="h-4 w-4 opacity-70 transition-transform group-hover:translate-x-1" />
        </button>
      </div>
    </div>
  );
}
