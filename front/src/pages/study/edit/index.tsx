import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Category, StudyPost, studyApiService, UpdateStudyDTO } from '@/core/config/studyApiConfig';
import 'react-quill/dist/quill.snow.css';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { format } from 'date-fns';
import { Card, Input, Button, Form } from '@/components';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/shared/ui/select';
import ReactQuill from 'react-quill';
import { CalendarIcon, ChevronLeft } from 'lucide-react';

import { cn } from '@/lib/utils';

import { ko } from 'date-fns/locale';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';

import { postSchema } from '@/schema';
import { toast } from 'sonner';
import { useAuth } from '@/components/features/auth/hooks/useAuth';

type FormValues = z.infer<typeof postSchema>;

const StudyEdit = () => {
  const navigate = useNavigate();
  const { postId } = useParams<{ postId: string }>();
  const [loading, setLoading] = useState(false);
  const [isOpenStartDate, setIsOpenStartDate] = useState(false);
  const [isOpenEndDate, setIsOpenEndDate] = useState(false);
  const [post, setPost] = useState<StudyPost | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const { isAuthenticated, user } = useAuth();

  const form = useForm<FormValues>({
    resolver: zodResolver(postSchema),
    defaultValues: {
      title: '',
      content: '',
      recruits: '',
      expectedPeriod: '',
      studies: '',
      studyDetails: [],
      startDate: undefined,
      endDate: undefined,
    },
    mode: 'onSubmit',
  });

  // Options for form selects
  const recruitOptions = [
    { value: '1', label: '1명' },
    { value: '2', label: '2명' },
    { value: '3', label: '3명' },
    { value: '4', label: '4명' },
    { value: '5', label: '5명' },
    { value: '6', label: '6명 이상' },
  ];

  const periodOptions = [
    { value: '1개월', label: '1개월' },
    { value: '2개월', label: '2개월' },
    { value: '3개월', label: '3개월' },
    { value: '6개월', label: '6개월' },
    { value: '6개월 이상', label: '6개월 이상' },
  ];

  useEffect(() => {
    const fetchPost = async () => {
      if (!postId) return;
      try {
        setLoading(true);
        const response = await studyApiService.getStudyDetail(parseInt(postId));
        setPost(response.data);

        // 본인이 작성한 글이 아니면 리디렉션
        if (user?.data?.nickname !== response.data.authorName) {
          toast('자신이 작성한 글만 수정할 수 있습니다.', {
            description: '',
          });
          navigate(`/study/${postId}`);
          return;
        }

        // 폼에 데이터 설정
        form.reset({
          title: response.data.title,
          content: response.data.content,
          recruits: response.data.recruits.toString(),
          expectedPeriod: response.data.expectedPeriod,
          studies: Array.isArray(response.data.studies) ? response.data.studies[0] : response.data.studies,
          studyDetails: response.data.studyDetails || [],
          startDate: new Date(response.data.startDate),
          endDate: new Date(response.data.endDate),
        });
      } catch (error) {
        console.error('스터디 정보를 불러오는데 실패했습니다:', error);
        toast('스터디 정보를 불러오는데 실패했습니다', {
          description: '잠시 후 다시 시도해주세요',
        });
        navigate('/study');
      } finally {
        setLoading(false);
      }
    };

    const fetchCategories = async () => {
      try {
        const response = await studyApiService.getCategoriesHierarchy();
        setCategories(response);
      } catch (error) {
        console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
      }
    };

    // 로그인 상태 확인
    if (!isAuthenticated) {
      toast('로그인이 필요한 서비스입니다.', {
        description: '',
      });
      navigate('/login');
      return;
    }

    fetchPost();
    fetchCategories();
  }, [postId, form, navigate, isAuthenticated, user]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (isOpenStartDate || isOpenEndDate) {
        // 클릭된 요소가 캘린더 내부인지 확인
        const isCalendarClick = (event.target as Element).closest('.rdp');

        // 데이터 속성으로 버튼 식별
        const isStartButton = (event.target as Element).closest('button[data-calendar="start"]');
        const isEndButton = (event.target as Element).closest('button[data-calendar="end"]');
        const isCalendarButton = (event.target as Element).closest('button.rdp-button');

        if (!isCalendarClick && !isStartButton && !isEndButton && !isCalendarButton) {
          setIsOpenStartDate(false);
          setIsOpenEndDate(false);
        }
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpenStartDate, isOpenEndDate]);

  const modules = {
    toolbar: [
      [{ header: [1, 2, false] }],
      ['bold', 'italic', 'underline', 'strike'],
      [{ list: 'ordered' }, { list: 'bullet' }],
      ['link', 'image'],
      ['clean'],
    ],
  };

  const onSubmit = async (values: FormValues) => {
    try {
      if (!postId || !post) return;

      setLoading(true);

      const updateData: UpdateStudyDTO = {
        postId: parseInt(postId),
        title: values.title,
        content: values.content,
        recruits: parseInt(values.recruits),
        expectedPeriod: values.expectedPeriod,
        studies: [values.studies],
        studyDetails: values.studyDetails || [],
        startDate: values.startDate.toISOString(),
        endDate: values.endDate.toISOString(),
      };

      await studyApiService.updatePost(parseInt(postId), updateData);

      navigate(`/study/${postId}`);
      toast('스터디 수정이 완료되었습니다', {
        description: '',
      });
    } catch (error) {
      console.error('스터디 수정에 실패했습니다:', error);
      toast('스터디 수정에 실패했습니다.', {
        description: '잠시 후 다시 시도해주세요',
      });
    } finally {
      setLoading(false);
    }
  };

  if (loading || !post) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="mx-auto mb-3 h-8 w-8 animate-spin rounded-full border-4 border-blue-600 border-t-transparent"></div>
          <p className="text-gray-600">스터디 정보를 불러오는 중...</p>
        </div>
      </div>
    );
  }

  const getCategoryOptions = () => {
    const options: { value: string; label: string }[] = [];

    const addOptions = (category: Category) => {
      options.push({ value: category.name, label: category.name });

      category.subCategories.forEach((subCategory) => {
        options.push({
          value: subCategory.name,
          label: `${category.name} > ${subCategory.name}`,
        });
      });
    };

    categories.forEach(addOptions);
    return options;
  };

  return (
    <div className="min-h-screen bg-gray-50 px-5 py-10">
      {/* 헤더 부분 */}
      <div className="mx-auto mb-5 max-w-3xl">
        <button
          onClick={() => navigate(`/study/${postId}`)}
          className="duration-400 flex items-center gap-1 rounded-lg px-4 py-2 text-gray-500 transition-colors hover:text-gray-600">
          <ChevronLeft className="h-4 w-4" />
          <span>스터디 상세로 돌아가기</span>
        </button>
      </div>

      <Card.Card className="mx-auto max-w-3xl rounded-2xl border-0 bg-white px-4 py-6 shadow-sm">
        <Card.CardHeader className="px-6 pb-8">
          <Card.CardTitle className="text-2xl font-bold text-gray-800">스터디 수정하기</Card.CardTitle>
          <p className="text-gray-600">스터디 정보를 수정할 수 있습니다</p>
        </Card.CardHeader>

        <Card.CardContent className="px-6">
          <Form.Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              <div className="space-y-4">
                <Form.FormField
                  control={form.control}
                  name="title"
                  render={({ field }) => (
                    <Form.FormItem>
                      <Form.FormLabel className="text-gray-600">
                        제목
                        {form.formState.errors.title && (
                          <span className="ml-2 text-xs font-medium text-red-500">
                            {form.formState.errors.title.message}
                          </span>
                        )}
                      </Form.FormLabel>
                      <Form.FormControl>
                        <Input
                          placeholder="스터디 제목을 입력해주세요"
                          {...field}
                          className="rounded-lg border-gray-200"
                        />
                      </Form.FormControl>
                    </Form.FormItem>
                  )}
                />

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <Form.FormField
                    control={form.control}
                    name="studies"
                    render={({ field }) => (
                      <Form.FormItem>
                        <Form.FormLabel className="text-gray-700">
                          모집 구분
                          {form.formState.errors.studies && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.studies.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <SelectTrigger className="rounded-lg border-gray-200 focus:border-gray-200 focus:ring-0">
                              <SelectValue placeholder="모집 구분을 선택하세요" />
                            </SelectTrigger>
                          </Form.FormControl>
                          <SelectContent>
                            {getCategoryOptions().map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </Form.FormItem>
                    )}
                  />

                  <Form.FormField
                    control={form.control}
                    name="recruits"
                    render={({ field }) => (
                      <Form.FormItem>
                        <Form.FormLabel className="text-gray-700">
                          모집 인원
                          {form.formState.errors.recruits && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.recruits.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <SelectTrigger className="rounded-lg border-gray-200 focus:border-gray-200 focus:ring-0">
                              <SelectValue placeholder="모집 인원을 선택하세요" />
                            </SelectTrigger>
                          </Form.FormControl>
                          <SelectContent>
                            {recruitOptions.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </Form.FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <Form.FormField
                    control={form.control}
                    name="expectedPeriod"
                    render={({ field }) => (
                      <Form.FormItem>
                        <Form.FormLabel className="text-gray-700">
                          예상 진행 기간
                          {form.formState.errors.expectedPeriod && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.expectedPeriod.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <SelectTrigger className="rounded-lg border-gray-200 focus:border-gray-200 focus:ring-0">
                              <SelectValue placeholder="진행 기간을 선택하세요" />
                            </SelectTrigger>
                          </Form.FormControl>
                          <SelectContent>
                            {periodOptions.map((option) => (
                              <SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </Form.FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <Form.FormField
                    control={form.control}
                    name="startDate"
                    render={({ field }) => (
                      <Form.FormItem className="flex flex-col">
                        <Form.FormLabel className="text-gray-700">
                          스터디 시작일
                          {form.formState.errors.startDate && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.startDate.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <div className="relative">
                          <Button
                            variant="outline"
                            data-calendar="start"
                            type="button"
                            onClick={(e) => {
                              e.preventDefault();
                              const newState = !isOpenStartDate;
                              setIsOpenStartDate(newState);
                              if (isOpenEndDate && newState) setIsOpenEndDate(false);
                            }}
                            className={cn(
                              'w-full pl-3 text-left font-normal',
                              !field.value && 'text-gray-500',
                              'border-gray-200'
                            )}>
                            {field.value ? format(field.value, 'PPP', { locale: ko }) : <span>날짜 선택</span>}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>

                          {isOpenStartDate && (
                            <DayPicker
                              mode="single"
                              selected={field.value}
                              onSelect={(date) => {
                                if (date) {
                                  field.onChange(date);
                                  setIsOpenStartDate(false);
                                }
                              }}
                              locale={ko}
                              showOutsideDays
                              fixedWeeks
                              className="absolute left-0 top-full z-50 rounded-md border bg-white p-3 shadow-lg"
                            />
                          )}
                        </div>
                      </Form.FormItem>
                    )}
                  />

                  <Form.FormField
                    control={form.control}
                    name="endDate"
                    render={({ field }) => (
                      <Form.FormItem className="flex flex-col">
                        <Form.FormLabel className="text-gray-700">
                          모집 마감일
                          {form.formState.errors.endDate && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.endDate.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <div className="relative">
                          <Button
                            variant="outline"
                            data-calendar="end"
                            type="button"
                            onClick={(e) => {
                              e.preventDefault();
                              const newState = !isOpenEndDate;
                              setIsOpenEndDate(newState);
                              if (isOpenStartDate && newState) setIsOpenStartDate(false);
                            }}
                            className={cn(
                              'w-full pl-3 text-left font-normal',
                              !field.value && 'text-gray-500',
                              'border-gray-200'
                            )}>
                            {field.value ? format(field.value, 'PPP', { locale: ko }) : <span>날짜 선택</span>}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>

                          {isOpenEndDate && (
                            <DayPicker
                              mode="single"
                              selected={field.value}
                              onSelect={(date) => {
                                if (date) {
                                  field.onChange(date);
                                  setIsOpenEndDate(false);
                                }
                              }}
                              locale={ko}
                              showOutsideDays
                              fixedWeeks
                              className="absolute left-0 top-full z-50 mt-1 rounded-md border bg-white p-3 shadow-lg"
                            />
                          )}
                        </div>
                      </Form.FormItem>
                    )}
                  />
                </div>
              </div>

              {/* 스터디 내용 섹션 */}
              <div className="space-y-4 pt-4">
                <Form.FormField
                  control={form.control}
                  name="content"
                  render={({ field }) => (
                    <Form.FormItem>
                      <Form.FormLabel className="text-gray-700">
                        스터디 소개
                        {form.formState.errors.content && (
                          <span className="ml-2 text-xs font-medium text-red-500">
                            {form.formState.errors.content.message}
                          </span>
                        )}
                      </Form.FormLabel>
                      <Form.FormControl>
                        <div className="min-h-[200px] overflow-hidden rounded-lg border border-gray-200">
                          <ReactQuill
                            theme="snow"
                            value={field.value}
                            onChange={field.onChange}
                            modules={modules}
                            className="h-64"
                          />
                        </div>
                      </Form.FormControl>
                      <Form.FormDescription className="mt-2 text-sm text-gray-500">
                        스터디 내용을 수정해주세요.
                      </Form.FormDescription>
                    </Form.FormItem>
                  )}
                />
              </div>

              <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-2 text-sm font-medium text-gray-700">💡 수정 가능한 항목</h3>
                <ul className="space-y-1 text-sm text-gray-600">
                  <li>• 제목: 스터디 제목을 수정할 수 있습니다</li>
                  <li>• 모집 구분: 스터디 카테고리를 변경할 수 있습니다</li>
                  <li>• 모집인원: 필요한 인원 수를 조정할 수 있습니다</li>
                  <li>• 예상 진행 기간: 스터디 진행 예상 기간을 변경할 수 있습니다</li>
                  <li>• 스터디 시작일: 스터디 시작 날짜를 변경할 수 있습니다</li>
                  <li>• 모집 마감일: 모집 마감일을 연장하거나 단축할 수 있습니다</li>
                  <li>• 내용: 스터디 상세 내용을 수정할 수 있습니다</li>
                </ul>
              </div>

              <div className="flex justify-end gap-3 border-t border-gray-100 pt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    navigate(`/study/${postId}`);
                    toast('스터디 수정이 취소되었습니다', {
                      description: '',
                    });
                  }}
                  className="rounded-lg border-gray-300"
                  disabled={loading}>
                  취소
                </Button>
                <Button
                  type="submit"
                  className="rounded-lg bg-blue-600 text-white transition-colors duration-300 hover:bg-blue-700"
                  disabled={loading}>
                  {loading ? (
                    <div className="flex items-center">
                      <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                      <span>수정 중...</span>
                    </div>
                  ) : (
                    '스터디 수정하기'
                  )}
                </Button>
              </div>
            </form>
          </Form.Form>
        </Card.CardContent>
      </Card.Card>
    </div>
  );
};

export { StudyEdit };
