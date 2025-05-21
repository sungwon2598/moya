export const EXPERIENCE_OPTIONS = {
  EMPTY: '',
  ZERO: '0년',
  ONE: '1년',
  TWO: '2년',
  THREE: '3년',
  FOUR: '4년',
  FIVE_PLUS: '5년 이상',
} as const;

export type Experience = (typeof EXPERIENCE_OPTIONS)[keyof typeof EXPERIENCE_OPTIONS];

export interface ProfileFormData {
  nickname: string;
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
  SUBMIT_ERROR: '프로필 저장 중 오류가 발생했습니다',
} as const;
