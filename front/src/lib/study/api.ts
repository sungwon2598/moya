import { studyService } from '@/services/study';
import type {
  CreateStudyMutationData,
  CreateStudyResponse,
  UpdateStudyMutationData,
  UpdateStudyResponse,
  DeleteStudyMutationData,
  DeleteStudyResponse,
  LikeMutationData,
  LikeResponse,
  CreateCommentMutationData,
  CreateCommentResponse,
  CreateReplyMutationData,
  DeleteCommentMutationData,
  DeleteCommentResponse,
  CreateCategoryMutationData,
  CreateCategoryResponse,
  UpdateCategoryMutationData,
  UpdateCategoryResponse,
  DeleteCategoryMutationData,
  DeleteCategoryResponse,
  StudyListQueryData,
  StudyListResponse,
  StudyDetailQueryData,
  StudyDetailResponse,
  CategoriesResponse,
  HotStudyListResponse,
} from './types';

export const studyApi = {
  getStudyList: async ({ page }: StudyListQueryData): Promise<StudyListResponse> => {
    return studyService.getStudyList(page);
  },

  getStudyDetail: async ({ postId }: StudyDetailQueryData): Promise<StudyDetailResponse> => {
    return studyService.getStudyDetail(postId);
  },

  getCategoriesHierarchy: async (): Promise<CategoriesResponse> => {
    return studyService.getCategoriesHierarchy();
  },

  getHotStudyList: async (): Promise<HotStudyListResponse> => {
    return studyService.getHotStudyList();
  },

  createPost: async ({ postData }: CreateStudyMutationData): Promise<CreateStudyResponse> => {
    return studyService.createPost(postData);
  },

  updatePost: async ({ postId, postData }: UpdateStudyMutationData): Promise<UpdateStudyResponse> => {
    return studyService.updatePost(postId, postData);
  },

  deletePost: async ({ postId }: DeleteStudyMutationData): Promise<DeleteStudyResponse> => {
    return studyService.deletePost(postId);
  },

  addLike: async ({ postId }: LikeMutationData): Promise<LikeResponse> => {
    return studyService.addLike(postId);
  },

  removeLike: async ({ postId }: LikeMutationData): Promise<LikeResponse> => {
    return studyService.removeLike(postId);
  },

  createComment: async ({ postId, commentData }: CreateCommentMutationData): Promise<CreateCommentResponse> => {
    return studyService.createComment(postId, commentData) as unknown as Promise<CreateCommentResponse>;
  },

  createReplyComment: async ({
    postId,
    parentCommentId,
    commentData,
  }: CreateReplyMutationData): Promise<CreateCommentResponse> => {
    return studyService.createReplyComments(
      postId,
      parentCommentId,
      commentData
    ) as unknown as Promise<CreateCommentResponse>;
  },

  deleteComment: async ({ postId, commentId }: DeleteCommentMutationData): Promise<DeleteCommentResponse> => {
    return studyService.deleteComments(postId, commentId);
  },

  createCategory: async ({ data }: CreateCategoryMutationData): Promise<CreateCategoryResponse> => {
    return studyService.createCategory(data);
  },

  updateCategory: async ({ categoryId, data }: UpdateCategoryMutationData): Promise<UpdateCategoryResponse> => {
    return studyService.updateCategory(categoryId, data);
  },

  deleteCategory: async ({ categoryId }: DeleteCategoryMutationData): Promise<DeleteCategoryResponse> => {
    return studyService.deleteCategory(categoryId);
  },
};
