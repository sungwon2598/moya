import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
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
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: postRoadmapFormData,
    onMutate: () => console.log("로드맵 생성 시도"),
    onSuccess: (data) => {
      console.log("로드맵 생성 성공", data);
      queryClient.setQueryData(["roadmapStatus"], {
        status: "success",
        data,
      });
    },
    onError: () => {
      console.log("로드맵 생성 실패");
    },
  });
};
