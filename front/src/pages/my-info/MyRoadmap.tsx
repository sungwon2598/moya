import { Button } from '@/components';
import { useAuth } from '@/components/features/auth/hooks/useAuth';
import { useMyRoadmapList } from '@/features/myinfo/api/getRoadmap';
import { ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function MyRoadmap() {
  const navigate = useNavigate();
  const { isAuthenticated: isLoggedIn } = useAuth();
  const { data: roadmapList } = useMyRoadmapList();

  const roadmapCreatePageMoveHandler = () => {
    if (!isLoggedIn) {
      return alert('로그인이 필요한 서비스입니다.');
    } else {
      navigate('/roadmap/create');
    }
  };
  return (
    <div className="@container mx-auto p-4">
      {roadmapList ? (
        <div>
          <h3>내 로드맵</h3>
          <section className="mt-6 grid grid-cols-4">
            <div className="inline-block transform cursor-pointer rounded border border-blue-100 bg-blue-50 px-4 py-5 hover:scale-105">
              <p className="text-lg font-semibold">주제</p>
              <p>중분류</p>
              <p className="mt-4 text-right text-blue-800">목표 : 30일</p>
            </div>
          </section>
        </div>
      ) : (
        <div className="p-11 text-center">
          <h3>아직 로드맵을 생성하지 않았어요.</h3>
          <h5 className="text-neutral-600">나에게 딱 맞는 로드맵 생성해보세요</h5>
          <Button onClick={roadmapCreatePageMoveHandler} className="mt-6">
            나만의 로드맵 생성하기
            <ArrowRight color="#fff" className="h-5 w-5 transition-transform group-hover:translate-x-1" />
          </Button>
        </div>
      )}
    </div>
  );
}
