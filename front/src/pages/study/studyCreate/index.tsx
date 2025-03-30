import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Category, CreateStudyDTO, studyApiService } from '@/core/config/studyApiConfig';
import 'react-quill/dist/quill.snow.css';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { format } from 'date-fns';
import { Card, Input, Button, Select, Popover, Form } from '@/components';
import { Calendar } from '@/components/shared/ui/calendar';
import ReactQuill from 'react-quill';
import { CalendarIcon, Users, Clock, BookOpen, PenTool, CheckCircle, LayoutGrid, ArrowLeft } from 'lucide-react';

const formSchema = z
  .object({
    title: z.string().min(2, { message: '제목은 최소 2글자 이상이어야 합니다.' }),
    content: z.string().min(10, { message: '내용은 최소 10글자 이상이어야 합니다.' }),
    recruits: z.string().min(1, { message: '모집 인원을 선택해주세요.' }),
    expectedPeriod: z.string().min(1, { message: '예상 기간을 선택해주세요.' }),
    studies: z.string().min(1, { message: '모집 구분을 선택해주세요.' }),
    studyDetails: z.array(z.string()).optional(),
    startDate: z.date({ required_error: '모집 시작일을 선택해주세요.' }),
    endDate: z.date({ required_error: '모집 마감일을 선택해주세요.' }),
  })
  .refine((data) => data.endDate > data.startDate, {
    message: '마감일은 시작일 이후여야 합니다.',
    path: ['endDate'],
  });

type FormValues = z.infer<typeof formSchema>;

const StudyCreate = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [activeSection, setActiveSection] = useState<string>('basic');

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
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

  const watchAllFields = form.watch();

  useEffect(() => {
    const totalFields = 7;
    let filledFields = 0;

    if (watchAllFields.title) filledFields++;
    if (watchAllFields.content && watchAllFields.content.length >= 10) filledFields++;
    if (watchAllFields.recruits) filledFields++;
    if (watchAllFields.expectedPeriod) filledFields++;
    if (watchAllFields.studies) filledFields++;
    if (watchAllFields.startDate) filledFields++;
    if (watchAllFields.endDate) filledFields++;

    const percentage = Math.round((filledFields / totalFields) * 100);
    setProgress(percentage);
  }, [watchAllFields]);

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

  const getStepStatus = (step: string) => {
    if (step === 'basic') {
      return watchAllFields.studies && watchAllFields.recruits && watchAllFields.expectedPeriod
        ? 'complete'
        : 'incomplete';
    } else if (step === 'details') {
      return watchAllFields.startDate && watchAllFields.endDate && watchAllFields.title ? 'complete' : 'incomplete';
    } else if (step === 'content') {
      return watchAllFields.content && watchAllFields.content.length >= 10 ? 'complete' : 'incomplete';
    }
    return 'incomplete';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="w-12 h-12 border-4 border-blue-500 rounded-full animate-spin border-t-transparent" />
      </div>
    );
  }

  return (
    <div className="min-h-screen py-12 bg-gradient-to-b from-gray-50 to-gray-100">
      {/* 헤더 */}
      <div className="max-w-3xl px-6 mx-auto mb-6">
        <button
          onClick={() => navigate('/study')}
          className="flex items-center text-gray-600 transition-colors hover:text-gray-900">
          <ArrowLeft className="w-4 h-4 mr-2" />
          <span>스터디 목록으로 돌아가기</span>
        </button>
      </div>

      <Card.Card className="max-w-3xl px-6 mx-auto bg-white border-0 shadow-lg rounded-3xl">
        <Card.CardHeader className="pb-4 border-b">
          <Card.CardTitle className="text-2xl font-bold text-gray-800">스터디 모집하기</Card.CardTitle>
          <p className="text-gray-500">함께할 팀원들을 모집하고 원하는 스터디를 생성해보세요</p>
        </Card.CardHeader>

        <div className="px-8 py-4 bg-gray-50">
          <div className="flex justify-between">
            <div
              className={`flex cursor-pointer flex-col items-center ${activeSection === 'basic' ? 'text-blue-600' : getStepStatus('basic') === 'complete' ? 'text-green-600' : 'text-gray-400'}`}
              onClick={() => setActiveSection('basic')}>
              <div
                className={`mb-2 flex h-10 w-10 items-center justify-center rounded-full ${
                  activeSection === 'basic'
                    ? 'border-2 border-blue-500 bg-blue-100 text-blue-600'
                    : getStepStatus('basic') === 'complete'
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-400'
                }`}>
                <LayoutGrid className="w-5 h-5" />
              </div>
              <span className="text-xs font-medium">기본 정보</span>
            </div>

            <div
              className={`flex cursor-pointer flex-col items-center ${activeSection === 'details' ? 'text-blue-600' : getStepStatus('details') === 'complete' ? 'text-green-600' : 'text-gray-400'}`}
              onClick={() => setActiveSection('details')}>
              <div
                className={`mb-2 flex h-10 w-10 items-center justify-center rounded-full ${
                  activeSection === 'details'
                    ? 'border-2 border-blue-500 bg-blue-100 text-blue-600'
                    : getStepStatus('details') === 'complete'
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-400'
                }`}>
                <PenTool className="w-5 h-5" />
              </div>
              <span className="text-xs font-medium">세부 정보</span>
            </div>

            <div
              className={`flex cursor-pointer flex-col items-center ${activeSection === 'content' ? 'text-blue-600' : getStepStatus('content') === 'complete' ? 'text-green-600' : 'text-gray-400'}`}
              onClick={() => setActiveSection('content')}>
              <div
                className={`mb-2 flex h-10 w-10 items-center justify-center rounded-full ${
                  activeSection === 'content'
                    ? 'border-2 border-blue-500 bg-blue-100 text-blue-600'
                    : getStepStatus('content') === 'complete'
                      ? 'bg-green-100 text-green-600'
                      : 'bg-gray-100 text-gray-400'
                }`}>
                <BookOpen className="w-5 h-5" />
              </div>
              <span className="text-xs font-medium">프로젝트 소개</span>
            </div>
          </div>

          {/* 연결선 */}
          <div className="relative flex justify-between mx-auto mb-1 -mt-5 px-14">
            <div
              className={`h-1 flex-1 ${getStepStatus('basic') === 'complete' ? 'bg-green-500' : 'bg-gray-300'}`}></div>
            <div
              className={`h-1 flex-1 ${getStepStatus('details') === 'complete' ? 'bg-green-500' : 'bg-gray-300'}`}></div>
          </div>
          {/* <p> {progress}</p> */}
        </div>

        <Card.CardContent className="p-8">
          <Form.Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              {activeSection === 'basic' && (
                <div className="space-y-6">
                  <h3 className="text-lg font-semibold text-gray-800">기본 정보</h3>
                  <p className="text-gray-500">스터디 유형과 인원, 기간을 설정해주세요</p>

                  <div className="p-6 border border-blue-100 rounded-xl bg-blue-50">
                    <Form.FormField
                      control={form.control}
                      name="studies"
                      render={({ field }) => (
                        <Form.FormItem className="mb-6">
                          <Form.FormLabel className="flex items-center text-gray-700">
                            <BookOpen className="w-4 h-4 mr-2 text-blue-500" />
                            모집 구분
                          </Form.FormLabel>
                          <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                            <Form.FormControl>
                              <Select.SelectTrigger className="bg-white border-blue-200 focus:ring-blue-500">
                                <Select.SelectValue placeholder="모집 구분을 선택하세요" />
                              </Select.SelectTrigger>
                            </Form.FormControl>
                            <Select.SelectContent className="bg-white">
                              {getCategoryOptions().map((option) => (
                                <Select.SelectItem key={option.value} value={option.value}>
                                  {option.label}
                                </Select.SelectItem>
                              ))}
                            </Select.SelectContent>
                          </Select.Select>
                          <Form.FormMessage />
                        </Form.FormItem>
                      )}
                    />

                    <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                      <Form.FormField
                        control={form.control}
                        name="recruits"
                        render={({ field }) => (
                          <Form.FormItem>
                            <Form.FormLabel className="flex items-center text-gray-700">
                              <Users className="w-4 h-4 mr-2 text-blue-500" />
                              모집 인원
                            </Form.FormLabel>
                            <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                              <Form.FormControl>
                                <Select.SelectTrigger className="bg-white border-blue-200 focus:ring-blue-500">
                                  <Select.SelectValue placeholder="모집 인원을 선택하세요" />
                                </Select.SelectTrigger>
                              </Form.FormControl>
                              <Select.SelectContent className="bg-white">
                                {recruitOptions.map((option) => (
                                  <Select.SelectItem key={option.value} value={option.value}>
                                    {option.label}
                                  </Select.SelectItem>
                                ))}
                              </Select.SelectContent>
                            </Select.Select>
                            <Form.FormMessage />
                          </Form.FormItem>
                        )}
                      />

                      <Form.FormField
                        control={form.control}
                        name="expectedPeriod"
                        render={({ field }) => (
                          <Form.FormItem>
                            <Form.FormLabel className="flex items-center text-gray-700">
                              <Clock className="w-4 h-4 mr-2 text-blue-500" />
                              진행 기간
                            </Form.FormLabel>
                            <Select.Select onValueChange={field.onChange} defaultValue={field.value}>
                              <Form.FormControl>
                                <Select.SelectTrigger className="bg-white border-blue-200 focus:ring-blue-500">
                                  <Select.SelectValue placeholder="진행 기간을 선택하세요" />
                                </Select.SelectTrigger>
                              </Form.FormControl>
                              <Select.SelectContent className="bg-white">
                                {periodOptions.map((option) => (
                                  <Select.SelectItem key={option.value} value={option.value}>
                                    {option.label}
                                  </Select.SelectItem>
                                ))}
                              </Select.SelectContent>
                            </Select.Select>
                            <Form.FormMessage />
                          </Form.FormItem>
                        )}
                      />
                    </div>
                  </div>

                  <div className="flex justify-end mt-6">
                    <Button
                      type="button"
                      onClick={() => setActiveSection('details')}
                      disabled={!watchAllFields.studies || !watchAllFields.recruits || !watchAllFields.expectedPeriod}
                      className="text-white bg-blue-600 hover:bg-blue-700">
                      다음 단계
                    </Button>
                  </div>
                </div>
              )}

              {activeSection === 'details' && (
                <div className="space-y-6">
                  <h3 className="text-lg font-semibold text-gray-800">세부 정보</h3>
                  <p className="text-gray-500">스터디 제목과 모집 기간을 입력해주세요</p>

                  <div className="p-6 border rounded-xl border-amber-100 bg-amber-50">
                    <Form.FormField
                      control={form.control}
                      name="title"
                      render={({ field }) => (
                        <Form.FormItem className="mb-6">
                          <Form.FormLabel className="flex items-center text-gray-700">
                            <PenTool className="w-4 h-4 mr-2 text-amber-500" />
                            제목
                          </Form.FormLabel>
                          <Form.FormControl>
                            <Input
                              placeholder="스터디 제목을 입력해주세요"
                              {...field}
                              className="bg-white border-amber-200 focus:ring-amber-500"
                            />
                          </Form.FormControl>
                          <Form.FormMessage />
                        </Form.FormItem>
                      )}
                    />

                    <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
                      <Form.FormField
                        control={form.control}
                        name="startDate"
                        render={({ field }) => (
                          <Form.FormItem className="flex flex-col">
                            <Form.FormLabel className="flex items-center text-gray-700">
                              <CalendarIcon className="w-4 h-4 mr-2 text-amber-500" />
                              모집 시작일
                            </Form.FormLabel>
                            <Form.FormControl>
                              <Input
                                type="date"
                                className="bg-white border-amber-200 focus:ring-amber-500"
                                value={field.value ? format(field.value, 'yyyy-MM-dd') : ''}
                                onChange={(e) => {
                                  const date = e.target.valueAsDate;
                                  if (date) field.onChange(date);
                                }}
                              />
                            </Form.FormControl>
                            <Form.FormMessage />
                          </Form.FormItem>
                        )}
                      />

                      <Form.FormField
                        control={form.control}
                        name="endDate"
                        render={({ field }) => (
                          <Form.FormItem className="flex flex-col">
                            <Form.FormLabel className="flex items-center text-gray-700">
                              <CalendarIcon className="w-4 h-4 mr-2 text-amber-500" />
                              모집 마감일
                            </Form.FormLabel>
                            <Form.FormControl>
                              <Input
                                type="date"
                                className="bg-white border-amber-200 focus:ring-amber-500"
                                value={field.value ? format(field.value, 'yyyy-MM-dd') : ''}
                                onChange={(e) => {
                                  const date = e.target.valueAsDate;
                                  if (date) field.onChange(date);
                                }}
                              />
                            </Form.FormControl>
                            <Form.FormMessage />
                          </Form.FormItem>
                        )}
                      />
                    </div>
                  </div>

                  <div className="flex justify-between mt-6">
                    <Button type="button" variant="outline" onClick={() => setActiveSection('basic')}>
                      이전 단계
                    </Button>
                    <Button
                      type="button"
                      onClick={() => setActiveSection('content')}
                      disabled={!watchAllFields.title || !watchAllFields.startDate || !watchAllFields.endDate}
                      className="text-white bg-blue-600 hover:bg-blue-700">
                      다음 단계
                    </Button>
                  </div>
                </div>
              )}

              {/* 내용 섹션 */}
              {activeSection === 'content' && (
                <div className="space-y-6">
                  <h3 className="text-lg font-semibold text-gray-800">프로젝트 소개</h3>
                  <p className="text-gray-500">어떤 스터디인지 자세히 소개해주세요</p>

                  <div className="p-6 border border-green-100 rounded-xl bg-green-50">
                    <Form.FormField
                      control={form.control}
                      name="content"
                      render={({ field }) => (
                        <Form.FormItem>
                          <Form.FormLabel className="flex items-center text-gray-700">
                            <BookOpen className="w-4 h-4 mr-2 text-green-500" />
                            프로젝트 소개
                          </Form.FormLabel>
                          <Form.FormControl>
                            <div className="min-h-[200px] rounded-md border border-green-200">
                              <ReactQuill
                                theme="snow"
                                value={field.value}
                                onChange={field.onChange}
                                modules={modules}
                                className="h-64"
                              />
                            </div>
                          </Form.FormControl>
                          <Form.FormDescription className="mt-4 text-green-700">
                            프로젝트 목표, 진행 방식, 예상 결과물, 필요한 기술 스택 등을 상세히 작성해주세요.
                          </Form.FormDescription>
                          <Form.FormMessage />
                        </Form.FormItem>
                      )}
                    />
                  </div>

                  <div className="flex justify-between mt-6">
                    <Button type="button" variant="secondary" onClick={() => setActiveSection('details')}>
                      이전 단계
                    </Button>
                    <Button
                      type="submit"
                      disabled={loading || progress < 100}
                      className="text-white bg-green-600 hover:bg-green-700">
                      <CheckCircle className="w-4 h-4 mr-2 text-white" />
                      스터디 생성하기
                    </Button>
                  </div>
                </div>
              )}
            </form>
          </Form.Form>
        </Card.CardContent>
      </Card.Card>
    </div>
  );
};

export { StudyCreate };
