import * as z from 'zod';

export const postSchema = z.object({
    title: z.string().min(2, { message: '제목은 최소 2글자 이상이어야 합니다.' }),
    content: z.string().min(10, { message: '내용은 최소 10글자 이상이어야 합니다.' }),
    recruits: z.string().min(1, { message: '모집 인원을 선택해주세요.' }),
    expectedPeriod: z.string().min(1, { message: '예상 기간을 선택해주세요.' }),
    studies: z.string().min(1, { message: '모집 구분을 선택해주세요.' }),
    studyDetails: z.array(z.string()).optional(),
    startDate: z.date({ required_error: '모집 시작일을 선택해주세요.' }),
    endDate: z.date({ required_error: '모집 마감일을 선택해주세요.' }),
}).refine((data) => data.endDate > data.startDate, {
    message: '마감일은 시작일 이후여야 합니다.',
    path: ['endDate'],
});
