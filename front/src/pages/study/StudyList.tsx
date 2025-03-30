import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, MessageSquare } from 'lucide-react';
import { StudyPost, studyApiService } from '@/core/config/studyApiConfig';

interface FilterOptions {
  studies: string;
  studyDetails: string;
  techStack: string;
  progressType: string;
  recruitmentStatus: string;
}

const StudyList = () => {
  const [posts, setPosts] = useState<StudyPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters] = useState<Partial<FilterOptions>>({});
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const fetchPosts = async (page: number) => {
    try {
      setLoading(true);
      setError(null);

      const response = await studyApiService.getStudyList(page - 1, 20, {
        studies: filters.studies,
        studyDetails: filters.studyDetails,
        progressType: filters.progressType,
        techStack: filters.techStack,
        recruitmentStatus: filters.recruitmentStatus,
      });

      setPosts(response.data);

      console.log(response.data);
      // if (response.meta.pagination) {
      //     setTotalPages(response.meta.pagination.totalPages);
      // }
    } catch (error) {
      setError(error instanceof Error ? error.message : '데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts(currentPage);
  }, [currentPage, filters]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
    window.scrollTo(0, 0);
  };

  const StudyCard = ({ post }: { post: StudyPost }) => {
    const navigate = useNavigate();
    const date = new Date(post.startDate);
    const now = new Date();
    const isNew = now.getTime() - date.getTime() < 3 * 24 * 60 * 60 * 1000;

    const getStatusBadge = () => {
      const startDate = new Date(post.startDate);
      const endDate = new Date(post.endDate);
      if (now < startDate) {
        return <span className="px-3 py-1 text-sm text-blue-600 rounded-full bg-blue-50">🔜 모집 예정</span>;
      } else if (now > endDate) {
        return <span className="px-3 py-1 text-sm text-gray-600 bg-gray-100 rounded-full">🔒 모집 마감</span>;
      } else {
        return <span className="px-3 py-1 text-sm text-green-600 rounded-full bg-green-50">✨ 모집 중</span>;
      }
    };

    return (
      <div
        onClick={() => navigate(`/study/${post.postId}`)}
        className="p-6 transition-all bg-white border border-gray-200 cursor-pointer rounded-2xl hover:border-gray-300">
        <div className="flex flex-wrap gap-2 mb-4">
          {post.studies && (
            <span className="px-3 py-1 text-sm text-gray-600 bg-gray-100 rounded-full">
              🎯 {post.studies} {post.studyDetails && `- ${post.studyDetails}`}
            </span>
          )}
          {getStatusBadge()}
          {isNew && (
            <span className="px-3 py-1 text-sm text-yellow-700 rounded-full bg-yellow-50">🎉 따끈따끈 새글</span>
          )}
        </div>

        <h2 className="mb-4 text-xl font-bold truncate">{post.title}</h2>
        <p className="mb-4 text-gray-600 line-clamp-2">{post.content}</p>

        <div className="flex flex-col gap-2">
          <div className="text-sm text-gray-600">
            📅 모집 기간: {new Date(post.startDate).toLocaleDateString()} ~{' '}
            {new Date(post.endDate).toLocaleDateString()}
          </div>

          <div className="flex items-center justify-between text-sm text-gray-500">
            <div className="flex items-center gap-2">
              <img
                src={`https://api.dicebear.com/7.x/initials/svg?seed=${post.authorName}`}
                alt="avatar"
                className="w-6 h-6 rounded-full"
              />
              <span>{post.authorName}</span>
            </div>

            <div className="flex items-center gap-4">
              <div className="flex items-center gap-1">
                <Eye className="w-4 h-4" />
                <span>{post.views ?? 0}</span>
              </div>
              <div className="flex items-center gap-1">
                <MessageSquare className="w-4 h-4" />
                <span>{post.totalComments}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-b-2 border-blue-500 rounded-full animate-spin" />
      </div>
    );
  }

  if (error) {
    return <div className="flex items-center justify-center h-64 text-red-500">{error}</div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-6xl px-4 py-8 mx-auto">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold">스터디 전체보기</h1>
        </div>

        <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
          {posts.map((post) => (
            <StudyCard key={post.postId} post={post} />
          ))}
        </div>

        {totalPages > 1 && (
          <div className="flex justify-center gap-2 mt-8">
            {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
              <button
                key={page}
                onClick={() => handlePageChange(page)}
                className={`rounded px-4 py-2 ${
                  currentPage === page ? 'bg-blue-500 text-white' : 'bg-white text-gray-700 hover:bg-gray-100'
                }`}>
                {page}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default StudyList;
