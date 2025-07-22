// import { StudyPost, StudyApiResponse, Category } from '@/core/config/studyApiConfig';

// export const MOCK_CATEGORIES: Category[] = [
//   {
//     id: 1,
//     name: 'Programming Languages',
//     subCategories: [
//       {
//         id: 2,
//         name: 'Java',
//         subCategories: [],
//       },
//     ],
//   },
//   {
//     id: 12,
//     name: '클라우드 서비스',
//     subCategories: [
//       {
//         id: 21,
//         name: 'Python',
//         subCategories: [],
//       },
//     ],
//   },
//   {
//     id: 13,
//     name: '운영체제',
//     subCategories: [],
//   },
//   {
//     id: 14,
//     name: 'AWS',
//     subCategories: [],
//   },
//   {
//     id: 15,
//     name: 'Linux',
//     subCategories: [],
//   },
//   {
//     id: 16,
//     name: 'Azure',
//     subCategories: [],
//   },
// ];

// export const MOCK_STUDY_POSTS: StudyPost[] = [
//   {
//     postId: 1,
//     title: '[Front] 소상공인 급여 정산 자동화 스케줄링 프로그램을 함께 제작할 팀...',
//     content: `<p>안녕하세요 👋 프론트엔드 스터디원을 모집합니다.</p>
// <p>React와 TypeScript를 활용한 실전 프로젝트를 통해 함께 성장하고 싶습니다.</p>
// <p><br></p>
// <p><strong>스터디 내용</strong></p>
// <p>- React 심화 학습</p>
// <p>- TypeScript 실전 활용</p>
// <p>- 실무 프로젝트 진행</p>
// <p><br></p>
// <p><strong>이런 분을 찾습니다!</strong></p>
// <p>- React 기본기가 있으신 분</p>
// <p>- 꾸준히 참여 가능하신 분</p>`,
//     recruits: 4,
//     expectedPeriod: '3개월',
//     startDate: '2024-12-23',
//     endDate: '2025-03-23',
//     studies: ['Programming Languages'],
//     studyDetails: ['Java'],
//     authorName: '구렁',
//     views: 130,
//     totalComments: 0,
//     isLiked: false,
//     totalLikes: 0,
//     tags: ['React', 'TypeScript', 'Next.js'],
//   },
//   {
//     postId: 2,
//     title: '식당 운영 서비스 개발 팀원 모집',
//     content: `<p>Python 백엔드 스터디 모집합니다 🐍</p>
// <p><br></p>
// <p><strong>스터디 목표</strong></p>
// <p>백엔드 기초부터 심화까지 같이 공부하실 분들을 모집합니다.</p>
// <p><br></p>
// <p><strong>진행 방식</strong></p>
// <p>- 주 1회 온라인 미팅</p>
// <p>- 과제 리뷰</p>
// <p>- 실습 위주의 학습</p>`,
//     recruits: 3,
//     expectedPeriod: '4개월',
//     startDate: '2024-12-23',
//     endDate: '2025-04-23',
//     studies: ['클라우드 서비스'],
//     studyDetails: ['Python'],
//     authorName: 'nearworld',
//     views: 91,
//     totalComments: 0,
//     isLiked: true,
//     totalLikes: 5,
//     tags: ['Spring', 'JPA', 'AWS'],
//   },
//   {
//     postId: 3,
//     title: '취업용 프로젝트 멤버 구합니다(FE 1월 마무리 예정)',
//     content: `<p>취준생들과 함께 공부할 스터디원을 모집합니다!</p>
// <p><br></p>
// <p><strong>스터디 내용</strong></p>
// <p>- 코딩테스트 준비</p>
// <p>- 기술 면접 준비</p>
// <p>- 포트폴리오 리뷰</p>`,
//     recruits: 4,
//     expectedPeriod: '1개월',
//     startDate: '2024-12-07',
//     endDate: '2025-01-14',
//     studies: ['AWS'],
//     studyDetails: [''],
//     authorName: 'ts',
//     views: 28,
//     totalComments: 0,
//     isLiked: false,
//     totalLikes: 2,
//     tags: ['TypeScript', 'React'],
//   },
//   {
//     postId: 26,
//     title: 'LLM을 활용한 회고 관리 서비스에서 백엔드 개발자를 찾고 있습니다!',
//     content: `<p>AI/머신러닝 스터디 함께하실 분들 모집합니다 🤖</p>
// <p><br></p>
// <p><strong>스터디 대상</strong></p>
// <p>- Python 기초 가능하신 분</p>
// <p>- AI/ML에 관심 있으신 분</p>
// <p><br></p>
// <p><strong>커리큘럼</strong></p>
// <p>1. 머신러닝 기초</p>
// <p>2. 딥러닝 입문</p>
// <p>3. LLM 실습</p>`,
//     recruits: 2,
//     expectedPeriod: '3개월',
//     startDate: '2024-11-31',
//     endDate: '2025-12-06',
//     studies: ['Linux'],
//     studyDetails: [''],
//     authorName: '팀오디세이',
//     views: 77,
//     totalComments: 0,
//     isLiked: true,
//     totalLikes: 8,
//     tags: ['Spring', 'AWS', 'Docker', 'Jenkins'],
//   },
//   {
//     postId: 5,
//     title: '[모집 마감 D-5] 재직자는 100% 무료 데이터 스킬업 프로젝트',
//     content: `<p>데이터 분석 스터디 모집합니다! 📊</p>
// <p><br></p>
// <p><strong>학습 내용</strong></p>
// <p>- SQL 기초/심화</p>
// <p>- Python Pandas</p>
// <p>- 데이터 시각화</p>
// <p><br></p>
// <p><strong>대상</strong></p>
// <p>- 현업 데이터 분석가</p>
// <p>- 데이터 직무 희망자</p>`,
//     recruits: 5,
//     expectedPeriod: '2개월',
//     startDate: '2024-12-13',
//     endDate: '2025-02-13',
//     studies: ['Azure'],
//     studyDetails: [''],
//     authorName: '팀스파르타1',
//     views: 23,
//     totalComments: 0,
//     isLiked: false,
//     totalLikes: 3,
//     tags: ['Python', 'Pandas', 'SQL'],
//   },
// ];

// export const mockStudyApiService = {
//   getCategoriesHierarchy: async (): Promise<StudyApiResponse<Category[]>> => {
//     return {
//       data: MOCK_CATEGORIES,
//       meta: { status: 200 },
//     };
//   },

//   getStudyList: async (page = 0, size = 10, filters?: Record<string, any>): Promise<StudyApiResponse<StudyPost[]>> => {
//     let filteredPosts = [...MOCK_STUDY_POSTS];

//     if (filters?.studies) {
//       filteredPosts = filteredPosts.filter((post) => post.studies === filters.studies);
//     }

//     if (filters?.studyDetails) {
//       filteredPosts = filteredPosts.filter((post) => post.studyDetails === filters.studyDetails);
//     }

//     if (filters?.techStack) {
//       filteredPosts = filteredPosts.filter((post) =>
//         post.tags?.some((tag) => tag.toLowerCase().includes(filters.techStack.toLowerCase()))
//       );
//     }

//     if (filters?.progressType) {
//       // 추후 progressType 필드가 추가되면 구현
//     }

//     const start = page * size;
//     const end = start + size;
//     const paginatedPosts = filteredPosts.slice(start, end);

//     return {
//       data: paginatedPosts,
//       meta: {
//         status: 200,
//         pagination: {
//           page,
//           size,
//           totalElements: filteredPosts.length,
//           totalPages: Math.ceil(filteredPosts.length / size),
//           first: page === 0,
//           last: page >= Math.ceil(filteredPosts.length / size) - 1,
//         },
//       },
//     };
//   },

//   getStudyDetail: async (postId: number): Promise<StudyApiResponse<StudyPost>> => {
//     const post = MOCK_STUDY_POSTS.find((post) => post.postId === postId);

//     if (!post) {
//       throw new Error('게시글을 찾을 수 없습니다.');
//     }

//     return {
//       data: { ...post },
//       meta: { status: 200 },
//     };
//   },

//   addLike: async (postId: number): Promise<StudyApiResponse<void>> => {
//     const postIndex = MOCK_STUDY_POSTS.findIndex((post) => post.postId === postId);

//     if (postIndex === -1) {
//       throw new Error('게시글을 찾을 수 없습니다.');
//     }

//     MOCK_STUDY_POSTS[postIndex].isLiked = true;

//     return {
//       data: undefined,
//       meta: { status: 200 },
//     };
//   },

//   removeLike: async (postId: number): Promise<StudyApiResponse<void>> => {
//     const postIndex = MOCK_STUDY_POSTS.findIndex((post) => post.postId === postId);

//     if (postIndex === -1) {
//       throw new Error('게시글을 찾을 수 없습니다.');
//     }

//     MOCK_STUDY_POSTS[postIndex].isLiked = false;

//     return {
//       data: undefined,
//       meta: { status: 200 },
//     };
//   },
// };
