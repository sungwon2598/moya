export const POSITION_OPTIONS = {
    EMPTY: '',
    FRONTEND: '프론트엔드',
    BACKEND: '백엔드',
    FULLSTACK: '풀스택',
    DEVOPS: '데브옵스',
    MOBILE: '모바일'
} as const;

export const EXPERIENCE_OPTIONS = {
    EMPTY: '',
    ZERO: '0년',
    ONE: '1년',
    TWO: '2년',
    THREE: '3년',
    FOUR: '4년',
    FIVE_PLUS: '5년 이상'
} as const;

export type Position = typeof POSITION_OPTIONS[keyof typeof POSITION_OPTIONS];
export type Experience = typeof EXPERIENCE_OPTIONS[keyof typeof EXPERIENCE_OPTIONS];

export interface ProfileFormData {
    nickname: string;
    position: Position;
    school: string;
    experience: Experience;
    description: string;
    links: string[];
}

export type ValidationErrors = {
    [K in keyof ProfileFormData]?: string;
} & {
    submit?: string;
};

export interface ProfileFormProps {
    initialData?: Partial<ProfileFormData>;
    onSubmit: (data: ProfileFormData) => Promise<void>;
}

export const VALIDATION_MESSAGES = {
    NICKNAME_REQUIRED: '닉네임을 입력해주세요',
    POSITION_REQUIRED: '직무를 선택해주세요',
    EXPERIENCE_REQUIRED: '경력을 선택해주세요',
    SUBMIT_ERROR: '프로필 저장 중 오류가 발생했습니다'
} as const;
