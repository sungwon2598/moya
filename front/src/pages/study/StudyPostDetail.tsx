import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Eye, MessageSquare, Heart, Calendar, Users, Clock, ArrowLeft } from 'lucide-react';
import { StudyPost } from '@/core/config/studyApiConfig';
// import { mockStudyApiService as studyApiService } from './studyMockData';
import { studyApiService } from '@/core/config/studyApiConfig';
import ReactQuill from 'react-quill';
import 'react-quill/dist/quill.bubble.css';

import { useAuth } from '../../components/features/auth/hooks/useAuth.ts';
import { toast } from 'sonner';

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
    if (!post) return;
    try {
      if (user?.data.nickname === post.authorName) {
        await studyApiService.deletePost(post.postId);
        toast('스터디가 삭제되었습니다.', {
          description: '',
        });
        navigate('/study');
      }
    } catch (error) {
      console.error('게시글 삭제 중 오류가 발생했습니다:', error);
    }
  };

  const getStatusBadge = () => {
    if (!post) return null;

    const now = new Date();
    const start = new Date(post.startDate);
    const end = new Date(post.endDate);

    if (now < start) {
      return (
        <span className="inline-flex items-center gap-1 px-4 py-2 font-medium text-blue-600 rounded-full bg-blue-50">
          🔜 모집 예정
        </span>
      );
    } else if (now > end) {
      return (
        <span className="inline-flex items-center gap-1 px-4 py-2 font-medium text-gray-600 bg-gray-100 rounded-full">
          🔒 모집 마감
        </span>
      );
    } else {
      return (
        <span className="inline-flex items-center gap-1 px-4 py-2 font-medium text-green-600 rounded-full bg-green-50">
          ✨ 모집 중
        </span>
      );
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="w-12 h-12 border-4 border-blue-500 rounded-full animate-spin border-t-transparent" />
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="flex items-center justify-center min-h-screen text-lg text-red-500">
        {error || '게시글을 찾을 수 없습니다.'}
      </div>
    );
  }

  return (
    <div className="min-h-screen py-12 bg-gray-50">
      <div className="max-w-5xl px-6 mx-auto">
        {/* 뒤로가기 버튼 */}
        <button
          onClick={() => navigate('/study')}
          className="flex items-center mb-8 font-medium text-gray-600 transition-colors hover:text-gray-900">
          <ArrowLeft className="w-5 h-5 mr-2" />
          스터디 목록으로
        </button>

        {/* 헤더 섹션 */}
        <div className="p-10 mb-8 bg-white border border-gray-200 shadow-sm rounded-3xl">
          {/* 카테고리 및 상태 배지 */}
          <div className="flex flex-wrap gap-3 mb-8">
            <span className="px-4 py-2 font-medium text-gray-700 bg-gray-100 rounded-full">
              🎯 {post.studies} {post.studyDetails && `- ${post.studyDetails}`}
            </span>
            {getStatusBadge()}
          </div>

          <h1 className="mb-8 text-4xl font-bold leading-tight text-gray-900">{post.title}</h1>

          {/* 작성자 정보 및 메타데이터 */}
          <div className="flex items-center justify-between pb-8 mb-8 border-b border-gray-100">
            <div className="flex items-center gap-4">
              <img
                src={user?.data.profileImageUrl}
                alt="avatar"
                className="w-12 h-12 border-2 border-gray-100 rounded-full"
              />
              <div>
                <div className="text-lg font-semibold text-gray-900">{post.authorName}</div>
                <div className="text-gray-500">{new Date(post.startDate).toLocaleDateString()}</div>
              </div>
            </div>

            <div className="flex items-center gap-6 text-gray-500">
              <div className="flex items-center gap-2">
                <Eye className="w-5 h-5" />
                <span className="font-medium">{post.views}</span>
              </div>
              <div className="flex items-center gap-2">
                <MessageSquare className="w-5 h-5" />
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
          <div className="grid grid-cols-1 gap-6 pb-8 mb-8 border-b border-gray-100 md:grid-cols-3">
            <div className="flex items-center gap-3 text-gray-700">
              <Calendar className="w-6 h-6 text-gray-500" />
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
              <Users className="w-6 h-6 text-gray-500" />
              <div>
                <div className="mb-1 font-medium">모집 인원</div>
                <div className="text-sm">{post.recruits}명</div>
              </div>
            </div>
            <div className="flex items-center gap-3 text-gray-700">
              <Clock className="w-6 h-6 text-gray-500" />
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
                <span key={index} className="px-4 py-2 text-sm font-medium text-blue-600 rounded-full bg-blue-50">
                  {tag}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* 본문 섹션 - Quill 에디터로 변경 */}
        <div className="p-10 mb-8 bg-white border border-gray-200 shadow-sm rounded-3xl">
          <ReactQuill value={post.content} readOnly={true} modules={modules} formats={formats} theme="bubble" />
        </div>

        {/* 하단 버튼 섹션 */}
        <div className="flex justify-center gap-4">
          <button className="px-8 py-4 font-semibold text-white transition-colors bg-blue-500 shadow-sm rounded-xl hover:bg-blue-600">
            참여 신청하기
          </button>
          {isLoggedIn && user?.data.nickname === post.authorName ? (
            <button
              onClick={() => navigate(`/study/${post.postId}/edit`)}
              className="px-8 py-4 font-semibold text-gray-700 transition-colors bg-white border-2 border-gray-200 rounded-xl hover:border-gray-300 hover:bg-gray-50">
              수정하기
            </button>
          ) : (
            <></>
          )}
          {isLoggedIn && user?.data.nickname === post.authorName ? (
            <button
              className="px-8 py-4 font-semibold text-white transition-colors bg-red-500 shadow-sm rounded-xl hover:bg-blue-600"
              onClick={deletePost}>
              삭제하기
            </button>
          ) : (
            <></>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudyPostDetail;
