import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Eye, MessageSquare } from 'lucide-react';
import { StudyPost } from '@/core/config/studyApiConfig';
import Dropdown from './Dropdown';
import { mockStudyApiService as studyApiService } from './studyMockData';
// 카테고리 타입 정의
interface Category {
    id: number;
    name: string;
    subCategories: Category[];
}

// 필터 옵션 타입
interface FilterOptions {
    studies: string;
    studyDetails: string;
    techStack: string;
    progressType: string;
}
const FilterMenu = ({
                        categories,
                        onFilterChange
                    }: {
    categories: Category[];
    onFilterChange: (filter: Partial<FilterOptions>) => void;
}) => {
    const [selectedCategory, setSelectedCategory] = useState<string>('');


    const progressTypeOptions = [
        { value: '', label: '진행 방식' },
        { value: 'online', label: '온라인' },
        { value: 'offline', label: '오프라인' },
        { value: 'hybrid', label: '하이브리드' }
    ];

    const handleCategoryChange = (value: string) => {
        setSelectedCategory(value);
        // 카테고리가 선택되면 해당 값을 studies나 studyDetails로 설정
        const mainCategory = categories.find(cat => cat.name === value);
        const subCategory = categories.flatMap(cat => cat.subCategories).find(sub => sub.name === value);

        if (mainCategory) {
            onFilterChange({ studies: value, studyDetails: '' });
        } else if (subCategory) {
            const parentCategory = categories.find(cat =>
                cat.subCategories.some(sub => sub.name === value)
            );
            if (parentCategory) {
                onFilterChange({
                    studies: parentCategory.name,
                    studyDetails: value
                });
            }
        } else {
            onFilterChange({ studies: '', studyDetails: '' });
        }
    };

    return (
        <div className="flex flex-wrap gap-4 mb-8 justify-center">
            <Dropdown
                label="카테고리"
                categories={categories}
                onChange={handleCategoryChange}
                value={selectedCategory}
                isCategory={true}
            />

            <Dropdown
                label="진행 방식"
                options={progressTypeOptions}
                onChange={(value) => onFilterChange({ progressType: value })}
            />
            <div className="flex gap-4 w-full justify-center mt-4">
                <button className="px-4 py-2 rounded-full border border-emerald-500 text-emerald-500 hover:bg-emerald-50 transition-colors duration-200">
                    👋 모집 중만 보기
                </button>
                <button className="px-4 py-2 rounded-full border border-gray-300 text-gray-600 hover:bg-gray-50 transition-colors duration-200">
                    👀 내 북마크 보기
                </button>
            </div>
        </div>
    );
};
// StudyCard 컴포넌트
const StudyCard = ({ post }: { post: StudyPost }) => {
    const navigate = useNavigate();
    const date = new Date(post.startDate);
    const isNew = Date.now() - date.getTime() < 3 * 24 * 60 * 60 * 1000;

    return (
        <div
            onClick={() => navigate(`/studies/${post.postId}`)}
            className="bg-white rounded-2xl border border-gray-200 hover:border-gray-300 transition-all cursor-pointer p-6"
        >
            <div className="flex gap-2 mb-4">
                <span className="px-3 py-1 bg-gray-100 text-gray-600 text-sm rounded-full">
                    🎯 {post.studies} {post.studyDetails && `- ${post.studyDetails}`}
                </span>
                {isNew && (
                    <span className="px-3 py-1 bg-yellow-50 text-yellow-700 text-sm rounded-full">
                        🎉 따끈따끈 새글
                    </span>
                )}
            </div>

            <h2 className="text-xl font-bold mb-4 truncate">{post.title}</h2>

            <div className="flex flex-wrap gap-2 mb-6">
                {post.tags?.map((tag, index) => (
                    <span
                        key={index}
                        className="inline-flex items-center px-2 py-1 rounded-md bg-blue-50 text-blue-600 text-sm"
                    >
                        {tag}
                    </span>
                ))}
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
                        <span>{post.views}</span>
                    </div>
                    <div className="flex items-center gap-1">
                        <MessageSquare className="w-4 h-4" />
                        <span>{post.totalComments}</span>
                    </div>
                </div>
            </div>
        </div>
    );
};

// 메인 컴포넌트
const StudyList = () => {
    const [posts, setPosts] = useState<StudyPost[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [filters, setFilters] = useState<Partial<FilterOptions>>({});

    // 카테고리 데이터 fetch
    const fetchCategories = async () => {
        try {
            console.log("fetch categories")
            const response = await studyApiService.getCategoriesHierarchy();
            setCategories(response.data);
        } catch (error) {
            console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
        }
    };

    const fetchPosts = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await studyApiService.getStudyList(
                0,
                10,
                {
                    studies: filters.studies,
                    studyDetails: filters.studyDetails,
                    progressType: filters.progressType,
                    techStack: filters.techStack,
                }
            );
            setPosts(response.data);
        } catch (error) {
            setError('데이터를 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCategories();
    }, []);

    useEffect(() => {
        fetchPosts();
    }, [filters]);

    const handleFilterChange = (newFilter: Partial<FilterOptions>) => {
        setFilters(prev => ({ ...prev, ...newFilter }));
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
                    <h1 className="text-3xl font-bold">스터디 모집 게시글</h1>
                </div>

                <FilterMenu
                    categories={categories}
                    onFilterChange={handleFilterChange}
                />

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {posts.map(post => (
                        <StudyCard key={post.postId} post={post} />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default StudyList;
