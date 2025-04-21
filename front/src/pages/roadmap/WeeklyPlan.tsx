import { usePostRoadmapCreate } from "@/features/roadmap/api/createRoadmap";

export default function WeeklyPlan() {
  const { data, isSuccess } = usePostRoadmapCreate();
  console.log(data);
  return isSuccess && <div></div>;
}
