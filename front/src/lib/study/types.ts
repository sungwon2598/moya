import type {
  CreateStudyDTO,
  StudyPost,
  StudyApiResponse,
  UpdateStudyDTO,
  CreateCommentDTO,
  Comment as StudyComment,
  Category,
  CreateCategoryRequest,
  UpdateCategoryRequest,
  HotPost,
} from '@/types/study';

export interface CreateStudyMutationData {
  postData: CreateStudyDTO;
}

export interface UpdateStudyMutationData {
  postId: number;
  postData: UpdateStudyDTO;
}

export interface DeleteStudyMutationData {
  postId: number;
}

export interface LikeMutationData {
  postId: number;
}

export interface CreateCommentMutationData {
  postId: number;
  commentData: CreateCommentDTO;
}

export interface CreateReplyMutationData {
  postId: number;
  parentCommentId: number;
  commentData: CreateCommentDTO;
}

export interface DeleteCommentMutationData {
  postId: number;
  commentId: number;
}

export interface CreateCategoryMutationData {
  data: CreateCategoryRequest;
}

export interface UpdateCategoryMutationData {
  categoryId: number;
  data: UpdateCategoryRequest;
}

export interface DeleteCategoryMutationData {
  categoryId: number;
}

export type CreateStudyResponse = StudyApiResponse<StudyPost>;
export type UpdateStudyResponse = StudyApiResponse<StudyPost>;
export type DeleteStudyResponse = StudyApiResponse<void>;
export type LikeResponse = StudyApiResponse<void>;
export type CreateCommentResponse = StudyApiResponse<StudyComment>;
export type DeleteCommentResponse = StudyApiResponse<void>;
export type CreateCategoryResponse = StudyApiResponse<void>;
export type UpdateCategoryResponse = StudyApiResponse<void>;
export type DeleteCategoryResponse = StudyApiResponse<void>;

export interface StudyListQueryData {
  page: number;
}

export interface StudyDetailQueryData {
  postId: number;
}

export type StudyListResponse = StudyApiResponse<StudyPost[]>;
export type StudyDetailResponse = StudyApiResponse<StudyPost>;
export type CategoriesResponse = Category[];
export type HotStudyListResponse = StudyApiResponse<HotPost[]>;
