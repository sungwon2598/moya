import { useMutation, useQuery } from "@tanstack/react-query";
import { axiosInstance } from "@core/config/apiConfig";

export interface CreateFormDataType {
  mainCategory: string;
  subCategory: string;
  currentLevel: string;
  duration: number;
  learningObjective: string;
}

const getRoadmapFormData = async () => {
  const { data } = await axiosInstance.get("/api/categories/roadmap-form-data");
  return data;
};

const postRoadmapFormData = async (createFormData: CreateFormDataType) => {
  const { data } = await axiosInstance.post(
    "/api/roadmap/generate",
    createFormData
  );
  return data;
};
export const useRoadmapFormData = () => {
  return useQuery({
    queryKey: ["roadmapForm"],
    queryFn: getRoadmapFormData,
    staleTime: 1000 * 60 * 5,
  });
};

export const usePostRoadmapCreate = () => {
  return useMutation({
    mutationFn: postRoadmapFormData,
  });
};
