import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Eye, MessageSquare, Heart, Calendar, Users, Clock, ArrowLeft, User } from 'lucide-react';
import { StudyPost } from '@/core/config/studyApiConfig';
// import { mockStudyApiService as studyApiService } from './studyMockData';
import { studyApiService } from '@/core/config/studyApiConfig';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.bubble.css';

import { useAuth } from '../../../components/features/auth/hooks/useAuth.ts';
import { toast } from 'sonner';

import { useModal } from '@/shared/hooks/useModal.ts';
import { ApplyModal } from '../applyModal/index.tsx';

// Quill 에디터 스타일 설정
const quillStyles = `
.ql-editor {
    padding: 0;
    font-size: 16px;
    line-height: 1.6;
}
.ql-editor p {
    margin-bottom: 1em;
}
.ql-editor h1, .ql-editor h2, .ql-editor h3 {
    margin-bottom: 0.5em;
    font-weight: 600;
}
.ql-editor ul, .ql-editor ol {
    padding-left: 1.5em;
    margin-bottom: 1em;
}
.ql-editor img {
    max-width: 100%;
    margin: 1em 0;
}
.ql-editor blockquote {
    border-left: 4px solid #e5e7eb;
    padding-left: 1em;
    margin: 1em 0;
    color: #6b7280;
}
`;

const StudyPostDetail = () => {
  const { postId } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState<StudyPost | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isLiked, setIsLiked] = useState(false);

  const [isApplyed, setIsApplyed] = useState(false);

  // Quill 설정
  const modules = {
    toolbar: false,
  };

  const formats = [
    'header',
    'bold',
    'italic',
    'underline',
    'strike',
    'list',
    'bullet',
    'link',
    'image',
    'align',
    'color',
    'background',
    'code-block',
    'blockquote',
  ];

  useEffect(() => {
    // 스타일 주입
    const styleSheet = document.createElement('style');
    styleSheet.type = 'text/css';
    styleSheet.innerText = quillStyles;
    document.head.appendChild(styleSheet);

    return () => {
      document.head.removeChild(styleSheet);
    };
  }, []);

  const { isAuthenticated: isLoggedIn, user } = useAuth();

  useEffect(() => {
    console.log(isLoggedIn);
    console.log(user);
    const fetchPost = async () => {
      if (!postId) return;
      try {
        setLoading(true);
        const response = await studyApiService.getStudyDetail(parseInt(postId));
        console.group(response.data);
        setPost(response.data);
        setIsLiked(response.data.isLiked);
      } catch (error) {
        console.log(error);
        setError('게시글을 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchPost();
  }, [postId]);

  // 좋아요
  const handleLikeToggle = async () => {
    if (!post) return;
    try {
      if (isLiked) {
        await studyApiService.removeLike(post.postId);
      } else {
        await studyApiService.addLike(post.postId);
      }
      setIsLiked(!isLiked);
    } catch (error) {
      console.error('좋아요 처리 중 오류가 발생했습니다:', error);
    }
  };

  const deletePost = async () => {
    if (!post || !isLoggedIn || !user?.data?.nickname) {
      return;
    }

    if (user.data.nickname !== post.authorName) {
      toast('삭제 권한이 없습니다.');
      return;
    }

    toast('정말로 삭제하시겠습니까?', {
      description: '삭제 후 복구가 불가능합니다.',
      action: {
        label: '삭제',
        onClick: async () => {
          try {
            await studyApiService.deletePost(post.postId);
            toast('스터디가 삭제되었습니다.');
            navigate('/study');
          } catch (error) {
            console.error('게시글 삭제 중 오류가 발생했습니다:', error);
            toast('삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
          }
        },
      },
    });
  };

  const { showModal } = useModal();

  const handleShowApplicants = () => {
    showModal(<ApplyModal postId={post?.postId || 0} />, {
      title: (
        <div className="flex items-center gap-3">
          <User className="h-6 w-6 text-blue-500" />
          <h2 className="text-2xl font-bold text-gray-900">신청자 목록</h2>
        </div>
      ),
      size: 'lg',
    });
  };

  const getStatusBadge = () => {
    if (!post) return null;

    const now = new Date();

    const start = new Date(post.startDate); // 스터디 시작일
    const end = new Date(post.endDate); // 스터디 마감일

    if (now < start) {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-blue-50 px-4 py-2 font-medium text-blue-600">
          🔜 모집 예정
        </span>
      );
    } else if (now < end) {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-gray-100 px-4 py-2 font-medium text-gray-600">
          🔒 모집 마감
        </span>
      );
    } else {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-4 py-2 font-medium text-green-600">
          ✨ 모집 중
        </span>
      );
    }
  };

  if (loading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-blue-500 border-t-transparent" />
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="flex min-h-screen items-center justify-center text-lg text-red-500">
        {error || '게시글을 찾을 수 없습니다.'}
      </div>
    );
  }

  const toggleStudyApply = async () => {
    if (!isLoggedIn) {
      toast('로그인이 필요합니다.');
      return;
    }

    try {
      if (isApplyed) {
        // 취소 요청
        setIsApplyed(false);
        toast('참여 신청이 취소되었습니다.');
      } else {
        // 신청 요청
        setIsApplyed(true);
        toast('참여 신청이 완료되었습니다.');
      }
    } catch (error) {
      console.error('스터디 참여 신청 오류', error);
      toast('스터디 참여 신청 중 오류가 발생했습니다, 다시 시도해주세요');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="mx-auto max-w-5xl px-6">
        {/* 뒤로가기 버튼 */}

        <div className="mb-4 flex content-center items-center justify-between">
          <button
            onClick={() => navigate('/study')}
            className="flex items-center font-medium text-gray-600 transition-colors hover:text-gray-900">
            <ArrowLeft className="mr-2 h-5 w-5" />
            스터디 목록으로
          </button>
          {isLoggedIn && user?.data.nickname === post.authorName && (
            <div className="mr-2 flex gap-1">
              <button
                onClick={() => navigate(`/study/${post.postId}/edit`)}
                className="hover:text-moya-primary flex items-center space-x-2 px-1 py-1 text-sm font-medium text-gray-500 transition-colors duration-300 hover:rounded-md hover:bg-gray-50">
                수정
              </button>
              <button
                className="flex items-center space-x-2 px-1 py-1 text-sm font-medium text-gray-500 transition-colors duration-300 hover:rounded-md hover:bg-gray-50 hover:text-red-600"
                onClick={deletePost}>
                삭제
              </button>
            </div>
          )}
        </div>

        {/* 헤더 섹션 */}
        <div className="mb-8 rounded-3xl border border-gray-200 bg-white p-10 shadow-sm">
          {/* 카테고리 및 상태 배지 */}
          <div className="mb-8 flex flex-wrap gap-3">
            <span className="rounded-full bg-gray-100 px-4 py-2 font-medium text-gray-700">
              🎯 {post.studies} {post.studyDetails && `- ${post.studyDetails}`}
            </span>
            {getStatusBadge()}
          </div>

          <h1 className="mb-8 text-4xl font-bold leading-tight text-gray-900">{post.title}</h1>

          {/* 작성자 정보 및 메타데이터 */}
          <div className="mb-8 flex items-center justify-between border-b border-gray-100 pb-8">
            <div className="flex items-center gap-4">
              <img
                src={`https://api.dicebear.com/7.x/initials/svg?seed=${post.authorName}`}
                alt="avatar"
                className="h-12 w-12 rounded-full border-2 border-gray-100"
              />
              <div>
                <div className="text-lg font-semibold text-gray-900">{post.authorName}</div>
                <div className="text-gray-500">{new Date(post.startDate).toLocaleDateString()}</div>
              </div>
            </div>

            <div className="flex items-center gap-6 text-gray-500">
              <div className="flex items-center gap-2">
                <Eye className="h-5 w-5" />
                <span className="font-medium">{post.views}</span>
              </div>
              <div className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5" />
                <span className="font-medium">{post.totalComments}</span>
              </div>
              <button
                onClick={handleLikeToggle}
                className={`flex items-center gap-2 ${
                  isLiked ? 'text-red-500' : 'text-gray-500'
                } transition-colors hover:text-red-500`}>
                <Heart className={`h-5 w-5 ${isLiked ? 'fill-current' : ''}`} />
              </button>
            </div>
          </div>

          {/* 모집 정보 */}
          <div className="mb-8 grid grid-cols-1 gap-6 border-b border-gray-100 pb-8 md:grid-cols-3">
            <div className="flex items-center gap-3 text-gray-700">
              <Calendar className="h-6 w-6 text-gray-500" />
              <div>
                <div className="flex items-center gap-2">
                  <p className="font-medium">스터디 시작</p>
                  <p className="text-sm text-gray-600">{new Date(post.startDate).toLocaleDateString()}</p>
                </div>
                <div className="flex items-center gap-2">
                  <p className="font-medium">모집 마감</p>
                  <p className="text-sm text-gray-600"> {new Date(post.endDate).toLocaleDateString()}</p>
                </div>
              </div>
            </div>
            <div className="flex items-center gap-3 text-gray-700">
              <Users className="h-6 w-6 text-gray-500" />
              <div>
                <div className="mb-1 font-medium">모집 인원</div>
                <div className="text-sm">{post.recruits}명</div>
              </div>
            </div>
            <div className="flex items-center gap-3 text-gray-700">
              <Clock className="h-6 w-6 text-gray-500" />
              <div>
                <div className="mb-1 font-medium">예상 기간</div>
                <div className="text-sm">{post.expectedPeriod}</div>
              </div>
            </div>
          </div>

          {/* 기술 스택 태그 */}
          {post.tags && post.tags.length > 0 && (
            <div className="flex flex-wrap gap-2">
              {post.tags.map((tag, index) => (
                <span key={index} className="rounded-full bg-blue-50 px-4 py-2 text-sm font-medium text-blue-600">
                  {tag}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* 본문 섹션 - Quill 에디터로 변경 */}
        <div className="mb-8 rounded-3xl border border-gray-200 bg-white p-10 shadow-sm">
          <ReactQuill value={post.content} readOnly={true} modules={modules} formats={formats} theme="bubble" />
        </div>

        {/* 하단 버튼 섹션 */}
        <div className="flex justify-center gap-4">
          {isLoggedIn && user?.data.nickname === post.authorName ? (
            <button
              className="rounded-xl bg-blue-500 px-8 py-4 font-semibold text-white shadow-sm transition-colors hover:bg-blue-600"
              onClick={handleShowApplicants}>
              신청자 확인
            </button>
          ) : (
            <button
              className={`rounded-xl px-8 py-4 font-semibold shadow-sm transition-all duration-200 ${
                isApplyed ? 'bg-red-500 text-white hover:bg-red-600' : 'bg-blue-500 text-white hover:bg-blue-600'
              }`}
              onClick={toggleStudyApply}>
              {isApplyed ? '참여 취소' : '참여 신청'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudyPostDetail;
