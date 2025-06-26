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

// 테스트용 상세 데이터
const MOCK_ROADMAP_DETAILS = {
  1: {
    progress: 65,
    completedTasks: 15,
    totalTasks: 23,
    currentPhase: 'React 컴포넌트 심화',
    nextMilestone: 'TypeScript 고급 기능',
    estimatedCompletion: '2024-09-15',
    difficulty: 'intermediate',
  },
  2: {
    progress: 32,
    completedTasks: 8,
    totalTasks: 25,
    currentPhase: 'Python 기초 문법',
    nextMilestone: 'NumPy와 Pandas 실습',
    estimatedCompletion: '2024-11-20',
    difficulty: 'beginner',
  },
  3: {
    progress: 78,
    completedTasks: 19,
    totalTasks: 24,
    currentPhase: 'Figma 고급 기능',
    nextMilestone: '프로토타이핑 완성',
    estimatedCompletion: '2024-08-10',
    difficulty: 'intermediate',
  },
  4: {
    progress: 45,
    completedTasks: 12,
    totalTasks: 27,
    currentPhase: 'React Native 기초',
    nextMilestone: '네비게이션 구현',
    estimatedCompletion: '2024-10-05',
    difficulty: 'intermediate',
  },
  5: {
    progress: 58,
    completedTasks: 14,
    totalTasks: 24,
    currentPhase: 'Express 미들웨어',
    nextMilestone: '데이터베이스 연동',
    estimatedCompletion: '2024-09-30',
    difficulty: 'advanced',
  },
  6: {
    progress: 23,
    completedTasks: 5,
    totalTasks: 22,
    currentPhase: 'AWS 기초 개념',
    nextMilestone: 'EC2 인스턴스 생성',
    estimatedCompletion: '2024-10-15',
    difficulty: 'intermediate',
  },
};

// 테스트용 가라 데이터
const MOCK_ROADMAP_DATA: RoadmapData[] = [
  {
    id: 1,
    mainCategory: '웹 개발',
    subCategory: 'React와 TypeScript로 현대적인 웹 애플리케이션 구축',
    duration: 90,
  },
  {
    id: 2,
    mainCategory: '데이터 사이언스',
    subCategory: 'Python과 머신러닝을 활용한 데이터 분석 및 예측 모델링',
    duration: 120,
  },
  {
    id: 3,
    mainCategory: 'UI/UX 디자인',
    subCategory: 'Figma와 Adobe XD를 활용한 사용자 중심 인터페이스 설계',
    duration: 60,
  },
  {
    id: 4,
    mainCategory: '모바일 앱 개발',
    subCategory: 'React Native로 크로스 플랫폼 모바일 앱 개발',
    duration: 75,
  },
  {
    id: 5,
    mainCategory: '백엔드 개발',
    subCategory: 'Node.js와 Express를 활용한 서버 사이드 개발',
    duration: 100,
  },
  {
    id: 6,
    mainCategory: '클라우드 컴퓨팅',
    subCategory: 'AWS를 활용한 클라우드 인프라 구축 및 운영',
    duration: 80,
  },
];

export default function MyRoadmap() {
  const navigate = useNavigate();
  const { isAuthenticated: isLoggedIn } = useAuth();
  const { data: roadmapList } = useMyRoadmapList();

  // 테스트 모드 활성화 (실제 데이터가 없을 때 가라 데이터 사용)
  const USE_MOCK_DATA = process.env.NODE_ENV === 'development' || !roadmapList;
  const displayRoadmapList = USE_MOCK_DATA ? MOCK_ROADMAP_DATA : roadmapList;

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

  // 테스트 데이터를 URL 파라미터로 생성하는 함수
  const createTestDataParams = (roadmapId: number) => {
    if (!USE_MOCK_DATA) return '';

    const detailData = MOCK_ROADMAP_DETAILS[roadmapId as keyof typeof MOCK_ROADMAP_DETAILS];
    if (!detailData) return '';

    const params = new URLSearchParams({
      progress: detailData.progress.toString(),
      completedTasks: detailData.completedTasks.toString(),
      totalTasks: detailData.totalTasks.toString(),
      currentPhase: detailData.currentPhase,
      nextMilestone: detailData.nextMilestone,
      estimatedCompletion: detailData.estimatedCompletion,
      difficulty: detailData.difficulty,
      isMockData: 'true',
    });

    return `&${params.toString()}`;
  };

  // 로드맵 없을때
  if (displayRoadmapList?.length === 0)
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

  // 로드맵 있을때
  return (
    <>
      <h3 className="mt-6 px-4 text-2xl font-bold">내 로드맵</h3>
      {USE_MOCK_DATA && (
        <div className="mx-4 mb-4 rounded-lg border border-yellow-200 bg-yellow-50 p-3">
          <p className="text-sm text-yellow-800">🧪 테스트 모드: 가라 데이터를 사용 중입니다.</p>
        </div>
      )}
      <div className="grid gap-4 p-4 sm:grid-cols-2 md:grid-cols-3">
        {displayRoadmapList?.map((roadmap: RoadmapData) => {
          const emoji = getConsistentEmoji(roadmap.id);
          const testDataParams = createTestDataParams(roadmap.id);

          return (
            <Link
              key={roadmap.id}
              to={`/my-info/roadmap/${roadmap.id}?mainCategory=${encodeURIComponent(roadmap.mainCategory)}&subCategory=${encodeURIComponent(roadmap.subCategory)}${testDataParams}`}>
              <div className="group relative rounded-2xl bg-gradient-to-br from-blue-50 to-white p-6 shadow-lg transition-all duration-300 hover:scale-[1.02] hover:shadow-xl">
                <div className="mb-3 flex items-center gap-2">
                  <h3 className="text-xl font-semibold text-gray-800 transition-colors group-hover:text-blue-700">
                    {emoji} {roadmap.mainCategory}
                  </h3>
                </div>

                <p className="mb-4 text-sm text-gray-600">{roadmap.subCategory}</p>

                {/* 테스트 데이터일 때 진행률 표시 */}
                {USE_MOCK_DATA && (
                  <div className="mb-6">
                    <div className="mb-1 flex justify-between text-xs text-gray-500">
                      <span>진행률</span>
                      <span>{MOCK_ROADMAP_DETAILS[roadmap.id as keyof typeof MOCK_ROADMAP_DETAILS]?.progress}%</span>
                    </div>
                    <div className="h-2 rounded-full bg-gray-200">
                      <div
                        className="h-2 rounded-full bg-blue-500 transition-all duration-300"
                        style={{
                          width: `${MOCK_ROADMAP_DETAILS[roadmap.id as keyof typeof MOCK_ROADMAP_DETAILS]?.progress}%`,
                        }}
                      />
                    </div>
                  </div>
                )}

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
