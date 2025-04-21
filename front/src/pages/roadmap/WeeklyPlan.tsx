// import { usePostRoadmapCreate } from "@/features/roadmap/api/createRoadmap";

import { useRotatingMessage } from "@/features/roadmap/hooks/useRotatingMessage";

export default function WeeklyPlan() {
  // const { isSuccess } = usePostRoadmapCreate();
  const data = {
    weeklyPlans: [
      {
        week: 1,
        weeklyKeyword: "C# 언어의 기본 문법 이해",
        dailyPlans: [
          {
            day: 1,
            dailyKeyword: "C# 소개 및 개발 환경 설정",
          },
          {
            day: 2,
            dailyKeyword: "기본 데이터 타입과 변수 선언",
          },
          {
            day: 3,
            dailyKeyword: "연산자와 표현식",
          },
          {
            day: 4,
            dailyKeyword: "조건문과 제어 흐름",
          },
          {
            day: 5,
            dailyKeyword: "반복문 기본 이해",
          },
          {
            day: 6,
            dailyKeyword: "배열과 리스트",
          },
          {
            day: 7,
            dailyKeyword: "메서드와 함수 기초",
          },
        ],
      },
      {
        week: 2,
        weeklyKeyword: "객체 지향 프로그래밍 기본 이해",
        dailyPlans: [
          {
            day: 1,
            dailyKeyword: "클래스와 객체",
          },
          {
            day: 2,
            dailyKeyword: "속성과 메서드",
          },
          {
            day: 3,
            dailyKeyword: "생성자와 소멸자",
          },
          {
            day: 4,
            dailyKeyword: "상속과 다형성",
          },
          {
            day: 5,
            dailyKeyword: "인터페이스와 추상 클래스",
          },
          {
            day: 6,
            dailyKeyword: "예외 처리",
          },
          {
            day: 7,
            dailyKeyword: "기본 파일 입출력",
          },
        ],
      },
    ],
    overallTips: [
      "C# 공식 문서와 튜토리얼을 적극 활용하세요.",
      "각 개념을 실습 코딩으로 체화하세요.",
      "질문이 생기면 커뮤니티에서 토론을 통해 해결하세요.",
    ],
    curriculumEvaluation:
      "이 로드맵은 C#에 대한 초급 학습자가 기본 개념을 이해하기에 적합합니다.",
    hasRestrictedTopics: "없음",
  };
  const { currentMessage } = useRotatingMessage(data.overallTips, 5000);

  return (
    <div className="@container px-4">
      <h4 className="py-2">{data.curriculumEvaluation}</h4>
      <div className="relative pb-2 group">
        <div className="p-2 rounded bg-neutral-100">
          <p className="mb-1 font-semibold">📚 Tip.</p>
          <p>{currentMessage}</p>
        </div>
        <div className="absolute z-10 hidden w-full p-2 border rounded shadow-2xl border-neutral-400 bg-neutral-200 group-hover:block">
          {data.overallTips.map((tip, index) => (
            <p
              key={index}
              className={`${currentMessage === tip ? "font-semibold text-moya-primary" : "text-neutral-400"}`}
            >
              {tip}
            </p>
          ))}
        </div>
      </div>
      <div className="grid grid-cols-1 gap-4 mb-12 overflow-hidden sm:grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 border-neutral-200">
        {data.weeklyPlans.map((week) => (
          <div key={week.week} className="pt-4 border border-neutral-200">
            <div className="px-3 border-b ">
              <h6 className="text-moya-primary">{week.week}주차</h6>
              <h5 className="py-2 ">{week.weeklyKeyword}</h5>
            </div>

            <div className="">
              {week.dailyPlans.map((day, index) => (
                <p
                  key={day.day}
                  className={`flex ${index < week.dailyPlans.length - 1 ? "border-b" : "border-0"}`}
                >
                  <span className="inline-block py-1 mr-2 text-center border-r basis-1/5 text-neutral-700">
                    {day.day}일차
                  </span>
                  <span className="py-1 basis-4/5"> {day.dailyKeyword}</span>
                </p>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
