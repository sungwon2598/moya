import { Button } from "@/components/shared/ui/button";
import { RoadmapQuestionStageType } from "@/pages/roadmap/type";
// import { roadmapCreationQuestion } from "@/pages/roadmap/data/createRoadmap";
interface SelectProp {
  roadmapQuestionStage: RoadmapQuestionStageType;
  setRoadmapQuestionStage: () => void;
}

export default function Select({
  roadmapQuestionStage,
  setRoadmapQuestionStage,
}: SelectProp) {
  const selectBTN =
    "inline-block px-4 py-2 mr-2 bg-blue-100 rounded min-w-32 min-h-10";
  const pBTN = "mt-2";
  const questionStep = () => {
    // setRoadmapQuestionStage(...currentStatusNumber);
  };

  return (
    <>
      <article className="p-8 mb-12 text-3xl font-bold bg-neutral-100 rounded-2xl">
        <div>
          <p>나는</p>
          <p className={`${pBTN}`}>
            <button className={`${selectBTN}`}></button>을 위해
          </p>
        </div>
        <div className="mt-8">
          <p>
            <button className={`${selectBTN}`}></button>동안
          </p>
          <p className={`${pBTN}`}>
            <button className={`${selectBTN}`}></button>의
          </p>
          <p className={`${pBTN}`}>
            <button className={`${selectBTN}`}></button>을/를
          </p>
          <p className={`${pBTN}`}>공부하고 싶어요.</p>
        </div>
      </article>
      <section>
        <h3>{nowQuestion}</h3>
        <p>어떤 목표를 가지고 있나요?</p>
        <form className="mt-3.5 gap-4 grid grid-cols-2">
          <label className="w-full p-6 transition border rounded-lg cursor-pointer bg-neutral-50 border-neutral-500 hover:bg-neutral-100 hover:border-neutral-900 has-checked:bg-blue-50 has-checked:text-blue-900 has-checked:border-blue-200">
            <input type="radio" name="roadmap" className="hidden" />
            <span>선택지 1</span>
          </label>
          <label className="w-full p-6 transition border rounded-lg cursor-pointer bg-neutral-50 border-neutral-500 hover:bg-neutral-100 hover:border-neutral-900 has-checked:bg-blue-50 has-checked:text-blue-900 has-checked:border-blue-200">
            <input type="radio" name="roadmap" className="hidden" />
            <span>선택지 1</span>
          </label>
          <div className="col-span-2 text-right">
            <Button
              type="button"
              className="w-44 "
              size="lg"
              onClick={questionStep}
            >
              계속하기
            </Button>
          </div>
        </form>
      </section>
    </>
  );
}
