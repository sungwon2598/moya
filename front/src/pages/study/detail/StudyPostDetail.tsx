import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Eye,
  MessageSquare,
  Heart,
  Calendar,
  Users,
  Clock,
  ArrowLeft,
  User,
  Send,
  Reply,
  Trash2,
  MoreHorizontal,
} from 'lucide-react';
import { StudyPost } from '@/core/config/studyApiConfig';
import { studyApiService, type Comment, type Replies } from '@/core/config/studyApiConfig';
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

  // 댓글 관련 상태
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState('');
  const [isSubmittingComment, setIsSubmittingComment] = useState(false);

  // 답글 관련 상태
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');
  const [isSubmittingReply, setIsSubmittingReply] = useState(false);

  // 댓글 메뉴 상태
  const [activeMenuId, setActiveMenuId] = useState<number | null>(null);

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

  const formatDateTime = (dateString: string) => {
    const date = new Date(dateString + 'Z'); // UTC 시간으로 인식시기키 'Z'
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    // 1분 미만
    if (minutes < 1) {
      return '방금 전';
    }
    // 1시간 미만
    else if (minutes < 60) {
      return `${minutes}분 전`;
    }
    // 24시간 미만
    else if (hours < 24) {
      return `${hours}시간 전`;
    }
    // 7일 미만
    else if (days < 7) {
      return `${days}일 전`;
    }
    // 그 이후는 날짜와 시간 표시
    else {
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    }
  };

  // 댓글을 최신순으로 정렬하는 함수
  const sortCommentsByDate = (comments: Comment[]): Comment[] => {
    return comments
      .map((comment) => ({
        ...comment,
        replies: comment.replies
          ? [...comment.replies].sort(
              (a, b) => new Date(b.replyCreatedAt).getTime() - new Date(a.replyCreatedAt).getTime()
            )
          : [],
      }))
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  };

  // 게시글 및 댓글 데이터 가져오기
  const fetchPost = async () => {
    if (!postId) return;
    try {
      setLoading(true);
      const response = await studyApiService.getStudyDetail(parseInt(postId));
      console.log('게시글 상세 데이터:', response.data);
      setPost(response.data);

      // 댓글을 최신순으로 정렬하여 설정
      const sortedComments = sortCommentsByDate(response.data.comments || []);
      setComments(sortedComments);
      setIsLiked(response.data.isLiked);
    } catch (error) {
      console.log(error);
      setError('게시글을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPost();
  }, [postId]);

  useEffect(() => {
    const handleClickOutside = () => setActiveMenuId(null);
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, []);

  // 댓글 작성 (게시글 ID로 작성)
  const handleSubmitComment = async () => {
    if (!newComment.trim() || !isLoggedIn || !postId) {
      return;
    }

    setIsSubmittingComment(true);

    try {
      await studyApiService.createComments(parseInt(postId), {
        content: newComment.trim(),
      });

      setNewComment('');
      toast('댓글이 등록되었습니다.');

      // 댓글 작성 후 게시글 데이터 다시 가져오기
      await fetchPost();
    } catch (error) {
      console.error('댓글 등록 실패:', error);
      toast('댓글 등록에 실패했습니다.');
    } finally {
      setIsSubmittingComment(false);
    }
  };

  // 대댓글 작성 (게시글 ID + 댓글 ID로 작성)
  const handleSubmitReply = async (parentCommentId: number) => {
    if (!replyContent.trim() || !isLoggedIn || !postId) {
      return;
    }

    setIsSubmittingReply(true);
    try {
      console.log('대댓글 작성 시도:', { content: replyContent.trim(), parentCommentId });

      await studyApiService.createComment(parseInt(postId), {
        content: replyContent.trim(),
        parentCommentId: parentCommentId,
      });

      setReplyContent('');
      setReplyingTo(null);
      toast('답글이 등록되었습니다.');

      // 답글 작성 후 게시글 데이터 다시 가져오기
      await fetchPost();
    } catch (error) {
      console.error('답글 등록 실패:', error);
      toast('답글 등록에 실패했습니다.');
    } finally {
      setIsSubmittingReply(false);
    }
  };

  // 댓글 삭제 (게시글 ID + 댓글 ID로 삭제)
  const handleDeleteComment = async (commentId: number) => {
    if (!isLoggedIn || !postId) return;

    try {
      await studyApiService.deleteComments(parseInt(postId), commentId);
      toast('댓글이 삭제되었습니다.');

      // 댓글 삭제 후 게시글 데이터 다시 가져오기
      await fetchPost();
    } catch (error) {
      console.error('댓글 삭제 실패:', error);
      toast('댓글 삭제에 실패했습니다.');
    } finally {
      setActiveMenuId(null);
    }
  };

  // 답글 취소
  const handleCancelReply = () => {
    setReplyingTo(null);
    setReplyContent('');
  };

  const toggleCommentMenu = (e: React.MouseEvent, commentId: number) => {
    e.stopPropagation();
    setActiveMenuId(activeMenuId === commentId ? null : commentId);
  };

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
    const end = new Date(post.endDate); // 모집 마감일

    // 현재 시간이 모집 마감일보다 이후 (모집 기간 종료)
    if (now > end) {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-gray-100 px-4 py-2 font-medium text-gray-600">
          🔒 모집 마감
        </span>
      );
    }

    // 현재 시간이 스터디 시작일보다 이후이고 모집 마감일보다 이전
    else if (now >= start && now <= end) {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-orange-50 px-4 py-2 font-medium text-orange-600">
          🏃‍♂️ 스터디 진행 중
        </span>
      );
    }
    // 현재 시간이 스터디 시작일보다 이전 (아직 스터디 시작 전, 모집 중)
    else {
      return (
        <span className="inline-flex items-center gap-1 rounded-full bg-green-50 px-4 py-2 font-medium text-green-600">
          ✨ 모집 중
        </span>
      );
    }
  };

  // 댓글
  const CommentItem = ({
    comment,
    isReply = false,
  }: {
    comment: Comment | Replies;
    isReply?: boolean;
    parentCommentId?: number;
  }) => {
    // Reply 타입인지 Comment 타입인지 구분
    const isReplyType = 'replyId' in comment;

    const commentId = isReplyType ? comment.replyId : comment.commentId;
    const authorName = isReplyType ? comment.replyAuthorName : comment.authorName;
    const content = isReplyType ? comment.replyContent : comment.content;
    const createdAt = isReplyType ? comment.replyCreatedAt : comment.createdAt;

    const isAuthor = isLoggedIn && user?.data?.nickname === authorName;

    return (
      <div
        className={`flex gap-4 rounded-lg p-3 ${
          isReply ? 'ml-10 mt-3 border-l-2 border-gray-200 bg-gray-50' : 'border border-gray-100 bg-white'
        }`}>
        <img
          src={`https://api.dicebear.com/7.x/initials/svg?seed=${authorName}`}
          alt="avatar"
          className="h-10 w-10 rounded-full border-2 border-gray-100"
        />
        <div className="flex-1">
          <div className="mb-2 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <span className="font-semibold text-gray-900">{authorName}</span>
              <span className="text-sm text-gray-500">{formatDateTime(createdAt)}</span>
            </div>

            {isLoggedIn && (
              <div className="relative">
                <button
                  onClick={(e) => toggleCommentMenu(e, commentId)}
                  className="rounded-full p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600">
                  <MoreHorizontal className="h-4 w-4" />
                </button>

                {activeMenuId === commentId && (
                  <div className="absolute right-0 top-8 z-10 min-w-[120px] rounded-lg border border-gray-200 bg-white py-1 shadow-lg">
                    {!isReply && !isReplyType && (
                      <button
                        onClick={() => {
                          setReplyingTo(commentId);
                          setActiveMenuId(null);
                        }}
                        className="flex w-full items-center gap-2 px-3 py-2 text-sm text-gray-700 hover:bg-gray-50">
                        <Reply className="h-4 w-4" />
                        답글
                      </button>
                    )}
                    {isAuthor && (
                      <button
                        onClick={() => handleDeleteComment(commentId)}
                        className="flex w-full items-center gap-2 px-3 py-2 text-sm text-red-600 hover:bg-gray-50">
                        <Trash2 className="h-4 w-4" />
                        삭제
                      </button>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>

          <p className="mb-3 leading-relaxed text-gray-700">{content}</p>

          {replyingTo === commentId && !isReply && !isReplyType && (
            <div className="mt-4 flex gap-4">
              <img
                src={`https://api.dicebear.com/7.x/initials/svg?seed=${user?.data?.nickname || 'User'}`}
                alt="내 아바타"
                className="h-8 w-8 rounded-full border-2 border-gray-100"
              />
              <div className="flex-1">
                <textarea
                  value={replyContent}
                  onChange={(e) => setReplyContent(e.target.value)}
                  placeholder={`${authorName}님에게 답글 작성...`}
                  className="min-h-[80px] w-full resize-none rounded-lg border border-gray-300 p-3 text-sm focus:border-transparent focus:outline-none focus:ring-2 focus:ring-blue-500"
                  disabled={isSubmittingReply}
                />
                <div className="mt-2 flex justify-end gap-2">
                  <button
                    onClick={handleCancelReply}
                    className="px-4 py-1.5 text-sm text-gray-600 transition-colors hover:text-gray-800">
                    취소
                  </button>
                  <button
                    onClick={() => handleSubmitReply(commentId)}
                    disabled={!replyContent.trim() || isSubmittingReply}
                    className="flex items-center gap-2 rounded-md bg-blue-500 px-4 py-1.5 text-sm font-medium text-white transition-colors hover:bg-blue-600 disabled:cursor-not-allowed disabled:bg-gray-300">
                    {isSubmittingReply ? (
                      <div className="h-3 w-3 animate-spin rounded-full border-2 border-white border-t-transparent" />
                    ) : (
                      <Send className="h-3 w-3" />
                    )}
                    답글 등록
                  </button>
                </div>
              </div>
            </div>
          )}

          {/* 답글 목록 - Comment 타입이고 replies가 있을 때만 표시 */}
          {!isReplyType && 'replies' in comment && comment.replies && comment.replies.length > 0 && (
            <div className="mt-4 space-y-4">
              {comment.replies.map((reply: Replies) => (
                <CommentItem key={reply.replyId} comment={reply} isReply={true} parentCommentId={commentId} />
              ))}
            </div>
          )}
        </div>
      </div>
    );
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

  const totalComments = comments.reduce((total, comment) => total + 1 + (comment.replies?.length || 0), 0);

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
                className={`flex items-center gap-2 ${isLiked ? 'text-red-500' : ''} transition-colors`}>
                <Heart
                  className={`h-5 w-5 ${isLiked ? 'fill-red-500' : ''} hover-text-red-500 hover:fill-red-500 hover:text-red-500`}
                />
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

          {/* 본문 섹션 - Quill 에디터로 변경 */}
          <div className="rounded-3xl border border-gray-200 bg-white p-10">
            <ReactQuill value={post.content} readOnly={true} modules={modules} formats={formats} theme="bubble" />
          </div>

          {/* 하단 버튼 섹션 */}
          <div className="mt-4 flex justify-end gap-4">
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

        <div className="mb-8 rounded-3xl border border-gray-200 bg-white p-10 shadow-sm">
          <h3 className="mb-6 text-2xl font-bold text-gray-900">
            댓글 <span className="text-lg font-normal text-gray-500">({totalComments})</span>
          </h3>

          {comments.length > 0 && (
            <div className="mb-8 space-y-6">
              {comments.map((comment) => (
                <CommentItem key={comment.commentId} comment={comment} />
              ))}
            </div>
          )}

          {/* 댓글 작성 영역 */}
          {isLoggedIn ? (
            <div className="border-t border-gray-100 pt-8">
              <div className="flex gap-4">
                <img
                  src={`https://api.dicebear.com/7.x/initials/svg?seed=${user?.data?.nickname || 'User'}`}
                  alt="내 아바타"
                  className="h-10 w-10 rounded-full border-2 border-gray-100"
                />
                <div className="flex-1">
                  <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="댓글을 작성해주세요..."
                    className="min-h-[100px] w-full resize-none rounded-lg border border-gray-300 p-4 focus:border-transparent focus:outline-none focus:ring-2 focus:ring-blue-500"
                    disabled={isSubmittingComment}
                  />
                  <div className="mt-3 flex justify-end">
                    <button
                      onClick={handleSubmitComment}
                      disabled={!newComment.trim() || isSubmittingComment}
                      className="flex items-center gap-2 rounded-lg bg-blue-500 px-6 py-2 font-medium text-white transition-colors hover:bg-blue-600 disabled:cursor-not-allowed disabled:bg-gray-300">
                      {isSubmittingComment ? (
                        <div className="h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent" />
                      ) : (
                        <Send className="h-4 w-4" />
                      )}
                      댓글 등록
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <div className="border-t border-gray-100 pt-8 text-center">
              <p className="mb-4 text-gray-500">댓글을 작성하려면 로그인이 필요합니다.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default StudyPostDetail;
