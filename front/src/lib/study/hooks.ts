import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { studyApi } from './api';
import { STUDY_KEY } from './key';
import type {
  CreateStudyMutationData,
  UpdateStudyMutationData,
  DeleteStudyMutationData,
  LikeMutationData,
  CreateCommentMutationData,
  CreateReplyMutationData,
  DeleteCommentMutationData,
  CreateCategoryMutationData,
  UpdateCategoryMutationData,
  DeleteCategoryMutationData,
  StudyListQueryData,
  StudyDetailQueryData,
} from './types';

export const useStudyList = (params: StudyListQueryData) => {
  return useQuery({
    queryKey: [STUDY_KEY, 'list', params.page],
    queryFn: () => studyApi.getStudyList(params),
  });
};

export const useStudyDetail = (params: StudyDetailQueryData) => {
  return useQuery({
    queryKey: [STUDY_KEY, 'detail', params.postId],
    queryFn: () => studyApi.getStudyDetail(params),
    enabled: !!params.postId,
  });
};

export const useCategoriesHierarchy = () => {
  return useQuery({
    queryKey: [STUDY_KEY, 'categories'],
    queryFn: studyApi.getCategoriesHierarchy,
  });
};

export const useHotStudyList = () => {
  return useQuery({
    queryKey: [STUDY_KEY, 'hot'],
    queryFn: studyApi.getHotStudyList,
  });
};

export const useCreateStudy = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateStudyMutationData) => studyApi.createPost(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'list'] });
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'hot'] });
    },
  });
};

export const useUpdateStudy = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateStudyMutationData) => studyApi.updatePost(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'list'] });
    },
  });
};

export const useDeleteStudy = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: DeleteStudyMutationData) => studyApi.deletePost(data),
    onSuccess: (_, variables) => {
      queryClient.removeQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'list'] });
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'hot'] });
    },
  });
};

export const useAddLike = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: LikeMutationData) => studyApi.addLike(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
    },
  });
};

export const useRemoveLike = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: LikeMutationData) => studyApi.removeLike(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
    },
  });
};

export const useCreateComment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateCommentMutationData) => studyApi.createComment(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
    },
  });
};

export const useCreateReplyComment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateReplyMutationData) => studyApi.createReplyComment(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
    },
  });
};

export const useDeleteComment = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: DeleteCommentMutationData) => studyApi.deleteComment(data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'detail', variables.postId] });
    },
  });
};

export const useCreateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateCategoryMutationData) => studyApi.createCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'categories'] });
    },
  });
};

export const useUpdateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: UpdateCategoryMutationData) => studyApi.updateCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'categories'] });
    },
  });
};

export const useDeleteCategory = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: DeleteCategoryMutationData) => studyApi.deleteCategory(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [STUDY_KEY, 'categories'] });
    },
  });
};
