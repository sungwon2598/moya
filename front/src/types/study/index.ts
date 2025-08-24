export interface Category {
  id: number;
  name: string;
  subCategories: Category[];
}

// 카테고리 생성 dto 인터페이스
export interface CreateCategoryRequest {
  name: string;
  parentId?: number; // Long -> number, optional로 설정
}

// 카테고리 수정 dto 인터페이스
export interface UpdateCategoryRequest {
  name: string;
}

// 필터 관련 타입 정의
export interface StudyFilters {
  studies?: string;
  studyDetails?: string;
  techStack?: string;
  progressType?: string;
  recruitmentStatus?: string;
}

export interface Replies {
  replyId: number;
  replyAuthorName: string;
  replyContent: string;
  replyCreatedAt: string;
}

export interface Comment {
  commentId: number;
  content: string;
  authorName: string;
  createdAt: string;
  replies?: Replies[];
}

// 스터디 게시글 관련 타입 정의
export interface StudyPost {
  postId: number;
  title: string;
  content: string;
  recruits: number;
  expectedPeriod: string;
  startDate: string;
  endDate: string;
  studies: string[];
  studyDetails: string[];
  authorName: string;
  views: number;
  comments: Comment[];
  totalComments: number;
  isLiked: boolean;
  totalLikes: number;
  tags?: string[];
}

export interface HotPost {
  postId: number;
  title: string;
  views: number;
  studies: string[];
  studyDetails: string[];
  endDate: string;
}

export interface CreateStudyDTO {
  title: string;
  content: string;
  recruits: number;
  expectedPeriod: string;
  startDate: string;
  endDate: string;
  studies: string[];
  studyDetails: string[];
}

export interface CreateCommentDTO {
  content: string;
  parentCommentId?: number;
  commentId?: number;
}

export interface UpdateStudyDTO extends CreateStudyDTO {
  postId: number;
}

export interface StudyApiResponse<T> {
  data: T;
  meta: {
    status: number;
    pagination?: {
      page: number;
      size: number;
      totalElements: number;
      totalPages: number;
      first: boolean;
      last: boolean;
    };
  };
  pagination?: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
  };
  error?: {
    code: string;
    message: string;
    details: Record<string, string>;
  };
}
