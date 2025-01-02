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

            const response = await studyApiService.getStudyList(
                page - 1,
                20,
                {
                    studies: filters.studies,
                    studyDetails: filters.studyDetails,
                    progressType: filters.progressType,
                    techStack: filters.techStack,
                    recruitmentStatus: filters.recruitmentStatus,
                }
            );

            setPosts(response.data);
            if (response.meta.pagination) {
                setTotalPages(response.meta.pagination.totalPages);
            }
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
                return (
                    <span className="px-3 py-1 bg-blue-50 text-blue-600 text-sm rounded-full">
                        🔜 모집 예정
                    </span>
                );
            } else if (now > endDate) {
                return (
                    <span className="px-3 py-1 bg-gray-100 text-gray-600 text-sm rounded-full">
                        🔒 모집 마감
                    </span>
                );
            } else {
                return (
                    <span className="px-3 py-1 bg-green-50 text-green-600 text-sm rounded-full">
                        ✨ 모집 중
                    </span>
                );
            }
        };

        return (
            <div
                onClick={() => navigate(`/study/${post.postId}`)}
                className="bg-white rounded-2xl border border-gray-200 hover:border-gray-300 transition-all cursor-pointer p-6"
            >
                <div className="flex gap-2 mb-4 flex-wrap">
                    {post.studies && (
                        <span className="px-3 py-1 bg-gray-100 text-gray-600 text-sm rounded-full">
                            🎯 {post.studies} {post.studyDetails && `- ${post.studyDetails}`}
                        </span>
                    )}
                    {getStatusBadge()}
                    {isNew && (
                        <span className="px-3 py-1 bg-yellow-50 text-yellow-700 text-sm rounded-full">
                            🎉 따끈따끈 새글
                        </span>
                    )}
                </div>

                <h2 className="text-xl font-bold mb-4 truncate">{post.title}</h2>
                <p className="text-gray-600 mb-4 line-clamp-2">{post.content}</p>

                <div className="flex flex-col gap-2">
                    <div className="text-sm text-gray-600">
                        📅 모집 기간: {new Date(post.startDate).toLocaleDateString()} ~ {new Date(post.endDate).toLocaleDateString()}
                    </div>

                    <div className="flex items-center justify-between text-gray-500 text-sm">
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
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex justify-center items-center h-64 text-red-500">
                {error}
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-6xl mx-auto px-4 py-8">
                <div className="text-center mb-8">
                    <h1 className="text-3xl font-bold">스터디 전체보기</h1>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {posts.map(post => (
                        <StudyCard key={post.postId} post={post} />
                    ))}
                </div>

                {totalPages > 1 && (
                    <div className="flex justify-center mt-8 gap-2">
                        {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                            <button
                                key={page}
                                onClick={() => handlePageChange(page)}
                                className={`px-4 py-2 rounded ${
                                    currentPage === page
                                        ? 'bg-blue-500 text-white'
                                        : 'bg-white text-gray-700 hover:bg-gray-100'
                                }`}
                            >
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
