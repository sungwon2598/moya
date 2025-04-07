import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Category, CreateStudyDTO, studyApiService } from '@/core/config/studyApiConfig';
import 'react-quill/dist/quill.snow.css';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { format } from 'date-fns';
import { Card, Input, Button, Select, Form } from '@/components';
import ReactQuill from 'react-quill';
import { ArrowLeft } from 'lucide-react';
import { cn } from '@/lib/utils';

import { postSchema } from '@/schema';

type FormValues = z.infer<typeof postSchema>;

const StudyCreate = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);

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
    mode: 'onChange',
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
    const fetchCategories = async () => {
      try {
        const response = await studyApiService.getCategoriesHierarchy();
        setCategories(response);
      } catch (error) {
        console.error('카테고리 데이터를 불러오는데 실패했습니다:', error);
      }
    };

    fetchCategories();
  }, []);

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
      setLoading(true);

      const postData: CreateStudyDTO = {
        title: values.title,
        content: values.content,
        recruits: parseInt(values.recruits),
        expectedPeriod: values.expectedPeriod,
        studies: [values.studies],
        studyDetails: values.studyDetails || [],
        startDate: values.startDate.toISOString(),
        endDate: values.endDate.toISOString(),
      };

      await studyApiService.createPost(postData);
      navigate('/study');
    } catch (error) {
      console.error('스터디 생성에 실패했습니다:', error);
    } finally {
      setLoading(false);
    }
  };

  // Create a flattened option list from categories
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

  // 로딩 상태 컴포넌트는 제거하고 버튼에서 처리

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      {/* 헤더 */}
      <div className="mx-auto mb-6 max-w-3xl px-4">
        <button
          onClick={() => navigate('/study')}
          className="flex items-center text-gray-600 transition-colors hover:text-gray-900">
          <ArrowLeft className="mr-2 h-4 w-4" />
          <span>스터디 목록으로 돌아가기</span>
        </button>
      </div>

      <Card.Card className="mx-auto max-w-3xl rounded-2xl border-0 bg-white px-4 py-6 shadow-sm">
        <Card.CardHeader className="px-6 pb-4">
          <Card.CardTitle className="text-2xl font-bold text-gray-800">스터디 모집하기</Card.CardTitle>
          <p className="text-gray-500">필요한 정보를 입력하여 새로운 스터디를 등록해보세요</p>
        </Card.CardHeader>

        <Card.CardContent className="px-6">
          <Form.Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              {/* 기본 정보 섹션 */}
              <div className="space-y-4">
                <Form.FormField
                  control={form.control}
                  name="title"
                  render={({ field }) => (
                    <Form.FormItem>
                      <Form.FormLabel className="text-gray-700">
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
                          className={cn(
                            'rounded-lg',
                            form.formState.errors.title ? 'border-red-300 bg-red-50' : 'border-gray-200'
                          )}
                        />
                      </Form.FormControl>
                      {/* 필드 아래 오류 메시지 제거 */}
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
                        <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <Select.SelectTrigger
                              className={cn(
                                'rounded-lg',
                                form.formState.errors.studies ? 'border-red-300 bg-red-50' : 'border-gray-200'
                              )}>
                              <Select.SelectValue placeholder="모집 구분을 선택하세요" />
                            </Select.SelectTrigger>
                          </Form.FormControl>
                          <Select.SelectContent
                            className="animate-in zoom-in-95 fade-in-50 origin-top bg-white duration-100"
                            position="popper"
                            sideOffset={5}>
                            {getCategoryOptions().map((option) => (
                              <Select.SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </Select.SelectItem>
                            ))}
                          </Select.SelectContent>
                        </Select.Select>
                        {/* 필드 아래 오류 메시지 제거 */}
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
                        <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <Select.SelectTrigger
                              className={cn(
                                'rounded-lg',
                                form.formState.errors.recruits ? 'border-red-300 bg-red-50' : 'border-gray-200'
                              )}>
                              <Select.SelectValue placeholder="모집 인원을 선택하세요" />
                            </Select.SelectTrigger>
                          </Form.FormControl>
                          <Select.SelectContent
                            className="animate-in zoom-in-95 fade-in-50 origin-top bg-white duration-100"
                            position="popper"
                            sideOffset={5}>
                            {recruitOptions.map((option) => (
                              <Select.SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </Select.SelectItem>
                            ))}
                          </Select.SelectContent>
                        </Select.Select>
                        {/* 필드 아래 오류 메시지 제거 */}
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
                          진행 기간
                          {form.formState.errors.expectedPeriod && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.expectedPeriod.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                          <Form.FormControl>
                            <Select.SelectTrigger
                              className={cn(
                                'rounded-lg',
                                form.formState.errors.expectedPeriod ? 'border-red-300 bg-red-50' : 'border-gray-200'
                              )}>
                              <Select.SelectValue placeholder="진행 기간을 선택하세요" />
                            </Select.SelectTrigger>
                          </Form.FormControl>
                          <Select.SelectContent
                            className="animate-in zoom-in-95 fade-in-50 origin-top bg-white duration-100"
                            position="popper"
                            sideOffset={5}>
                            {periodOptions.map((option) => (
                              <Select.SelectItem key={option.value} value={option.value}>
                                {option.label}
                              </Select.SelectItem>
                            ))}
                          </Select.SelectContent>
                        </Select.Select>
                        {/* 필드 아래 오류 메시지 제거 */}
                      </Form.FormItem>
                    )}
                  />
                </div>

                <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
                  <Form.FormField
                    control={form.control}
                    name="startDate"
                    render={({ field }) => (
                      <Form.FormItem>
                        <Form.FormLabel className="text-gray-700">
                          모집 시작일
                          {form.formState.errors.startDate && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.startDate.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Form.FormControl>
                          <Input
                            type="date"
                            className={cn(
                              'rounded-lg',
                              form.formState.errors.startDate ? 'border-red-300 bg-red-50' : 'border-gray-200'
                            )}
                            value={field.value ? format(field.value, 'yyyy-MM-dd') : ''}
                            onChange={(e) => {
                              const date = e.target.valueAsDate;
                              if (date) field.onChange(date);
                            }}
                          />
                        </Form.FormControl>
                        {/* 필드 아래 오류 메시지 제거 */}
                      </Form.FormItem>
                    )}
                  />

                  <Form.FormField
                    control={form.control}
                    name="endDate"
                    render={({ field }) => (
                      <Form.FormItem>
                        <Form.FormLabel className="text-gray-700">
                          모집 마감일
                          {form.formState.errors.endDate && (
                            <span className="ml-2 text-xs font-medium text-red-500">
                              {form.formState.errors.endDate.message}
                            </span>
                          )}
                        </Form.FormLabel>
                        <Form.FormControl>
                          <Input
                            type="date"
                            className={cn(
                              'rounded-lg',
                              form.formState.errors.endDate ? 'border-red-300 bg-red-50' : 'border-gray-200'
                            )}
                            value={field.value ? format(field.value, 'yyyy-MM-dd') : ''}
                            onChange={(e) => {
                              const date = e.target.valueAsDate;
                              if (date) field.onChange(date);
                            }}
                          />
                        </Form.FormControl>
                        {/* 필드 아래 오류 메시지 제거 */}
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
                        <div
                          className={cn(
                            'min-h-[200px] overflow-hidden rounded-lg',
                            form.formState.errors.content ? 'border border-red-300 bg-red-50' : 'border border-gray-200'
                          )}>
                          <ReactQuill
                            theme="snow"
                            value={field.value}
                            onChange={field.onChange}
                            modules={modules}
                            className="h-64"
                          />
                        </div>
                      </Form.FormControl>
                      {/* 필드 아래 오류 메시지 제거 */}
                      <Form.FormDescription className="mt-2 text-sm text-gray-500">
                        스터디의 목표, 진행 방식, 일정, 예상 결과물 등을 상세히 작성해주세요.
                      </Form.FormDescription>
                    </Form.FormItem>
                  )}
                />
              </div>

              {/* 도움말 섹션 */}
              <div className="rounded-lg border border-gray-200 bg-gray-50 p-4">
                <h3 className="mb-2 text-sm font-medium text-gray-700">💡 스터디 소개 작성 팁</h3>
                <ul className="space-y-1 text-sm text-gray-600">
                  <li>• 스터디의 목표와 방향성을 명확히 설명해주세요</li>
                  <li>• 진행 일정과 방식에 대해 구체적으로 작성해주세요</li>
                  <li>• 참여자에게 기대하는 역할과 준비물이 있다면 언급해주세요</li>
                  <li>• 스터디 종료 후 예상되는 결과물이 있다면 소개해주세요</li>
                </ul>
              </div>

              {/* 버튼 영역 */}
              <div className="flex justify-end gap-3 border-t border-gray-100 pt-4">
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => navigate('/study')}
                  className="rounded-lg border-gray-300"
                  disabled={loading}>
                  취소
                </Button>
                <Button
                  type="submit"
                  className={`rounded-lg transition-colors duration-300 ${
                    form.formState.isValid
                      ? 'bg-blue-600 text-white hover:bg-blue-700'
                      : 'bg-gray-300 text-gray-700 hover:bg-gray-400'
                  }`}
                  disabled={loading}>
                  {loading ? (
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
