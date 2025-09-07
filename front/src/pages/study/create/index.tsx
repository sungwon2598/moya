import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
// import { Category, CreateStudyDTO, studyApiService } from '@/core/config/studyApiConfig';
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
import 'react-day-picker/dist/style.css'; // 기본 스타일 가져오기

import { postSchema } from '@/schema';
import { toast } from 'sonner';
import type { Category, CreateStudyDTO } from '@/types/study';
import { useCreateStudy, useCategoriesHierarchy } from '@/lib/study/hooks';

type FormValues = z.infer<typeof postSchema>;

const StudyCreate = () => {
  const navigate = useNavigate();
  const [isOpenStartDate, setIsOpenStartDate] = useState(false);
  const [isOpenEndDate, setIsOpenEndDate] = useState(false);
  
  // React Query 훅 사용
  const createStudyMutation = useCreateStudy();
  const { data: categories = [] } = useCategoriesHierarchy();

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

  // 카테고리는 React Query로 자동 관리되므로 useEffect 제거

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
    const postData: CreateStudyDTO = {
      title: values.title,
      content: values.content,
      recruits: parseInt(values.recruits),
      expectedPeriod: values.expectedPeriod,
      studies: [],
      studyDetails: values.studyDetails || [],
      startDate: values.startDate.toISOString(),
      endDate: values.endDate.toISOString(),
    };

    createStudyMutation.mutate(
      { postData },
      {
        onSuccess: () => {
          navigate('/study');
          toast('스터디 등록이 완료되었습니다', {
            description: '',
          });
        },
        onError: (error) => {
          console.error('스터디 생성에 실패했습니다:', error);
          toast('스터디 등록에 실패했습니다.', {
            description: '',
          });
        },
      }
    );
  };

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
          onClick={() => navigate('/study')}
          className="duration-400 flex items-center gap-1 rounded-lg px-4 py-2 text-gray-500 transition-colors hover:text-gray-600">
          <ChevronLeft className="h-4 w-4" />
          <span>스터디 목록</span>
        </button>
      </div>

      <Card.Card className="mx-auto max-w-3xl rounded-2xl border-0 bg-white px-4 py-6 shadow-sm">
        <Card.CardHeader className="px-6 pb-8">
          <Card.CardTitle className="text-2xl font-bold text-gray-800">스터디 모집하기</Card.CardTitle>
          <p className="text-gray-600">필요한 정보를 입력하여 새로운 스터디를 등록해보세요</p>
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
                        <Select onValueChange={field.onChange} defaultValue={'test'}>
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
                              e.preventDefault(); // 폼 제출 방지
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
                                field.onChange(date);
                                setIsOpenStartDate(false);
                              }}
                              locale={ko}
                              showOutsideDays
                              fixedWeeks
                              className="absolute left-0 top-full z-50 rounded-md border bg-white p-3 shadow-lg"
                              disabled={[{ before: form.getValues('endDate') || new Date() }]}
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
                              e.preventDefault(); // 폼 제출 방지
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
                                field.onChange(date);
                                setIsOpenEndDate(false);
                              }}
                              locale={ko}
                              showOutsideDays
                              fixedWeeks
                              className="absolute left-0 top-full z-50 mt-1 rounded-md border bg-white p-3 shadow-lg"
                              // disabled={[{ before: form.getValues('startDate') || new Date() }]}
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
                        스터디의 목표, 진행 방식, 일정, 예상 결과물 등을 상세히 작성해주세요.
                      </Form.FormDescription>
                    </Form.FormItem>
                  )}
                />
              </div>

              <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-2 text-sm font-medium text-gray-700">💡 스터디 소개 작성 팁</h3>
                <ul className="space-y-1 text-sm text-gray-600">
                  <li>• 스터디의 목표와 방향성을 명확히 설명해주세요</li>
                  <li>• 진행 일정과 방식에 대해 구체적으로 작성해주세요</li>
                  <li>• 참여자에게 기대하는 역할과 준비물이 있다면 언급해주세요</li>
                  <li>• 스터디 종료 후 예상되는 결과물이 있다면 소개해주세요</li>
                </ul>
              </div>

              <div className="flex justify-end gap-3 border-t border-gray-100 pt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => {
                    navigate('/study');
                    toast('스터디 등록이 취소되었습니다', {
                      description: '',
                    });
                  }}
                  className="rounded-lg border-gray-300"
                  disabled={createStudyMutation.isPending}>
                  취소
                </Button>
                <Button
                  type="submit"
                  className="rounded-lg bg-blue-600 text-white transition-colors duration-300 hover:bg-blue-700"
                  disabled={createStudyMutation.isPending}>
                  {createStudyMutation.isPending ? (
                    <div className="flex items-center">
                      <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
                      <span>등록 중...</span>
                    </div>
                  ) : (
                    '스터디 등록하기'
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

export { StudyCreate };
