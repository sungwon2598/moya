import { ArrowRight } from "lucide-react";
import { FC } from "react";
import { RoadmapCard } from "./RoadmapCard.tsx";
import { roadmapSamples } from "./data/roadmap-samples.ts";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/components/features/auth/hooks/useAuth.ts";

const RoadmapPreview: FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated: isLoggedIn } = useAuth(); // isLogin을 isLoggedIn으로 alias
  const roadmapCreatePageMoveHandler = () => {
    if (!isLoggedIn) {
      return alert("로그인이 필요한 서비스입니다.");
    } else {
      navigate("/roadmap/create");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl px-4 py-8 mx-auto">
        <div className="mb-12 text-center">
          <h1 className="mb-2 text-4xl font-bold text-black">
            로드맵 미리보기
          </h1>
          <p className="text-gray-600">
            AI가 만들어준 로드맵을 미리 확인해 볼 수 있어요
          </p>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-3">
          {/* 첫 번째 줄: 3개의 카드 */}
          <div className="grid grid-cols-3 col-span-3 gap-6">
            {roadmapSamples.slice(0, 3).map((roadmap, index) => (
              <RoadmapCard key={index} {...roadmap} />
            ))}
          </div>

          {/* 두 번째 줄: 2개의 카드와 버튼 */}
          <div className="grid grid-cols-3 col-span-3 gap-6">
            {/* 2개의 카드 */}
            {roadmapSamples.slice(3, 5).map((roadmap, index) => (
              <RoadmapCard key={index + 3} {...roadmap} />
            ))}

            {/* 버튼 섹션 */}
            <div className="flex flex-col items-center justify-center">
              <p className="mb-4 text-gray-600">맞춤 로드맵을 만들고 싶다면?</p>
              <button
                onClick={roadmapCreatePageMoveHandler}
                className="flex items-center gap-2 px-6 py-3 font-semibold text-white transition-all duration-300 bg-blue-600 rounded-lg group hover:bg-blue-700"
              >
                나만의 로드맵 생성하기
                <ArrowRight className="w-5 h-5 transition-transform group-hover:translate-x-1" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RoadmapPreview;
