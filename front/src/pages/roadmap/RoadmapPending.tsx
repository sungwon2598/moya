import { useRotatingMessage } from "@/features/roadmap/hooks/useRotatingMessage";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function RoadmapPending() {
  const pendingMessages = [
    "최상의 학습 경로를 준비하고 있습니다.",
    "더 나은 학습을 위한 완벽한 로드맵을 생성중입니다.",
    "당신만을 위한 맞춤형 로드맵을 생성하고 있습니다.",
  ];
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { currentMessage } = useRotatingMessage(pendingMessages, 5000);
  const roadmapStatus = queryClient.getQueryData<{
    status: string;
    data?: unknown;
  }>(["roadmapStatus"]);
  useEffect(() => {
    if ((roadmapStatus?.status as string) === "success") {
      navigate("/roadmap/weeklyPlan");
    }
  }, [roadmapStatus]);
  return (
    <div className="@container mx-auto p-4 text-center">
      <div>
        <div className="relative mx-auto my-6 aspect-square w-xs">
          <div className="absolute inset-0 border-4 border-transparent rounded-full border-t-blue-500 border-r-blue-500 border-b-blue-500 animate-spin-custom" />
          <div className="flex flex-col justify-center h-full p-8 m-auto ">
            <h3>로드맵 생성 중</h3>
            <p className="h-8 pt-2">{currentMessage}</p>
          </div>
        </div>
      </div>
    </div>
  );
}
