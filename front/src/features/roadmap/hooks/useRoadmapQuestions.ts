import { useEffect, useState } from "react";
import { useRoadmapFormData } from "@/features/roadmap/api/createRoadmap";

type SubCategoriesType = {
  id: string;
  name: string;
};

type CategoryType = {
  id: string;
  name: string;
  subCategories: SubCategoriesType[];
};

export interface Question {
  id: number;
  name: string;
  title: string;
  subQuestion: string;
  choices: { id: string; name: string }[];
}

export function useRoadmapQuestions() {
  const { data: roadmapData, isLoading, error } = useRoadmapFormData();
  const [selectedCategoryId, setSelectedCategoryId] = useState<string | null>(
    null
  );
  const [questions, setQuestions] = useState<Question[]>([]);

  useEffect(() => {
    if (!roadmapData) return;
    const selectedCategory = selectedCategoryId
      ? roadmapData.categories.find(
          (cat: CategoryType) => cat.id === selectedCategoryId
        )
      : null;

    setQuestions([
      {
        id: 1,
        name: "learningObjective",
        title: "학습 목표",
        subQuestion: "어떤 목표를 가지고 있나요?",
        choices: roadmapData.learningObjectives,
      },
      {
        id: 2,
        name: "duration",
        title: "학습 목표를 달성하기까지 얼마의 시간을 투자할 계획인가요?",
        subQuestion: "기간을 선택해주세요.",
        choices: [
          { id: "ONE_WEEK", name: 7 },
          { id: "ONE_MONTH", name: 30 },
          { id: "THREE_MONTHS", name: 90 },
        ],
      },
      {
        id: 3,
        name: "mainCategory",
        title: "대분류 선택",
        subQuestion: "어떤 분야의 기술을 배우고 싶나요?",
        choices: roadmapData.categories,
      },
      {
        id: 4,
        name: "subCategory",
        title: "소분류 선택",
        subQuestion: "선택한 분야에서 어떤 기술을 학습할까요?",
        choices: selectedCategory?.subCategories || [],
      },
    ]);
  }, [roadmapData, selectedCategoryId]);

  return {
    questions,
    isLoading,
    error,
    setSelectedCategoryId,
  };
}
