export interface ProfileFormData {
    nickname: string;
    position: ProfilePosition;
    school: string;
    experience: ExperienceYear;
    description: string;
    links: string[];
}

export const POSITION_OPTIONS = {
    FRONTEND: '프론트엔드',
    BACKEND: '백엔드',
    FULLSTACK: '풀스택',
    DEVOPS: '데브옵스',
    MOBILE: '모바일',
    EMPTY: ''
} as const;

export type ProfilePosition = typeof POSITION_OPTIONS[keyof typeof POSITION_OPTIONS];

export const EXPERIENCE_OPTIONS = {
    ZERO: '0년',
    ONE: '1년',
    TWO: '2년',
    THREE: '3년',
    FOUR: '4년',
    FIVE_PLUS: '5년 이상',
    EMPTY: ''
} as const;

export type ExperienceYear = typeof EXPERIENCE_OPTIONS[keyof typeof EXPERIENCE_OPTIONS];

export interface ProfileFormProps {
    initialData?: Partial<ProfileFormData>;
    onSubmit: (data: ProfileFormData) => Promise<void>;
}

export const VALIDATION_MESSAGES = {
    POSITION_REQUIRED: '직무를 선택해주세요' as const,
    EXPERIENCE_REQUIRED: '경력을 선택해주세요' as const
} as const;
