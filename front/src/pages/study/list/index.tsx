import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { StudyPost, studyApiService } from '@/core/config/studyApiConfig';
import { StudyCard } from '@/components/features/study/list/card';
import { Button } from '@/components/shared/ui';

const StudyList = () => {
  const navigate = useNavigate();
  const [posts, setPosts] = useState<StudyPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPosts = async (page: number) => {
    try {
      console.log(page);
      setLoading(true);
      setError(null);

      const response = await studyApiService.getStudyList();

      setPosts(response.data);

      console.log(response.data);
    } catch (error) {
      setError(error instanceof Error ? error.message : '데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts(1);
  }, []);

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <div className="h-8 w-8 animate-spin rounded-full border-b-2 border-blue-500" />
      </div>
    );
  }

  if (error) {
    return <div className="flex h-64 items-center justify-center text-red-500">{error}</div>;
  }

  return (
    <div className="container mx-auto py-8">
      <div className="mb-8 flex items-center justify-between">
        <h1 className="text-3xl font-bold">스터디 목록</h1>
        <Button onClick={() => navigate('/study/write')}>스터디 만들기</Button>
      </div>

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
        {posts.map((post: StudyPost) => (
          <StudyCard
            key={post.postId}
            postId={post.postId.toString()}
            title={post.title}
            description={stripHtmlTags(post.content)}
            location="온라인" // 기본값으로 설정
            maxParticipants={post.recruits}
            currentParticipants={0} // API에서 제공되지 않는 정보
            startDate={formatDate(post.startDate)}
            endDate={formatDate(post.endDate)}
            tags={[...post.studies, ...post.studyDetails]}
            creator={{
              name: post.authorName,
              avatar: `https://api.dicebear.com/7.x/initials/svg?seed=${post.authorName}`, // 아바타 이미지 추가
            }}
          />
        ))}
      </div>
    </div>
  );
};

// HTML 태그를 제거하는 함수
function stripHtmlTags(html: string): string {
  const tmp = document.createElement('div');
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || '';
}

// 날짜 포맷팅 함수
function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

export default StudyList;
