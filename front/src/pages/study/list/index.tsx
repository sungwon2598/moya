import { useState, useEffect } from 'react';
import { StudyPost, studyApiService } from '@/core/config/studyApiConfig';
import { StudyCard } from '@/components/features/study/list/card';
import { Skeleton } from '@/components/shared/ui/skeleton';
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from '@/components/shared/ui/pagination';

const StudyList = () => {
  const [posts, setPosts] = useState<StudyPost[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);

  const fetchPosts = async (page: number) => {
    try {
      setLoading(true);
      setError(null);

      const response = await studyApiService.getStudyList(page);

      setPosts(response.data);
    } catch (error) {
      setError(error instanceof Error ? error.message : '데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPosts(currentPage);
  }, [currentPage]);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  if (error) {
    return <div className="flex h-64 items-center justify-center text-red-500">{error}</div>;
  }

  return (
    <div className="container mx-auto px-5 py-10">
      {/* <div className="flex items-center justify-center mb-8">
        <h1 className="text-3xl font-bold">스터디 목록</h1>
      </div> */}

      <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-4">
        {loading
          ? Array.from({ length: 8 }).map((_, index) => (
              <div key={index} className="h-[340px] w-full rounded-lg border p-4">
                <div className="space-y-4">
                  <Skeleton className="h-[56px] w-full" />
                  <div className="space-y-2">
                    <Skeleton className="h-4 w-3/4" />
                    <Skeleton className="h-4 w-1/2" />
                  </div>
                  <div className="space-y-2">
                    <Skeleton className="h-4 w-full" />
                    <Skeleton className="h-4 w-full" />
                    <Skeleton className="h-4 w-full" />
                  </div>
                  <div className="flex gap-2">
                    <Skeleton className="h-6 w-20" />
                    <Skeleton className="h-6 w-20" />
                  </div>
                  <div className="flex items-center justify-between">
                    <Skeleton className="h-8 w-8 rounded-full" />
                    <Skeleton className="h-8 w-20" />
                  </div>
                </div>
              </div>
            ))
          : posts.map((post: StudyPost) => (
              <StudyCard
                key={post.postId}
                postId={post.postId.toString()}
                title={post.title}
                description={stripHtmlTags(post.content)}
                maxParticipants={post.recruits}
                currentParticipants={0}
                startDate={formatDate(post.startDate)}
                endDate={formatDate(post.endDate)}
                tags={[...post.studies, ...post.studyDetails]}
                creator={{
                  name: post.authorName,
                  avatar: `https://api.dicebear.com/7.x/initials/svg?seed=${post.authorName}`,
                  // // 아바타 이미지 추가
                }}
              />
            ))}
      </div>

      <div className="mt-8 flex justify-center">
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
                className={currentPage === 0 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
              />
            </PaginationItem>
            <PaginationItem>
              <PaginationLink onClick={() => handlePageChange(0)} isActive={currentPage === 0}>
                1
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationLink onClick={() => handlePageChange(1)} isActive={currentPage === 1}>
                2
              </PaginationLink>
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                onClick={() => handlePageChange(Math.min(1, currentPage + 1))}
                className={currentPage === 1 ? 'pointer-events-none opacity-50' : 'cursor-pointer'}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    </div>
  );
};

// 태그 제거
function stripHtmlTags(html: string): string {
  const tmp = document.createElement('div');
  tmp.innerHTML = html;
  return tmp.textContent || tmp.innerText || '';
}

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

export default StudyList;
