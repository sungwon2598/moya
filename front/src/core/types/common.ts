export type Status = 'idle' | 'loading' | 'succeeded' | 'failed';

export interface ApiResponse<T> {
    status: 'SUCCESS' | 'ERROR';
    data?: T;
    error?: {
        code: string;
        message: string;
    };
}

export interface PaginationParams {
    page: number;
    size: number;
}

export interface PaginatedResponse<T> {
    content: T[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}

// core/types/validation.ts
export interface ValidationError {
    field: string;
    message: string;
}

export interface FormValidation {
    isValid: boolean;
    errors: ValidationError[];
}

// core/types/auth.ts
export interface User {
    id: number;
    email: string;
    nickname: string;
    profileImage?: string;
    createdAt: string;
}

export interface AuthState {
    user: User | null;
    token: string | null;
    status: Status;
    error: string | null;
}

// features/layout/types/header.types.ts
export interface DropdownItem {
    label: string;
    href?: string;
    onClick?: () => void;
    icon?: React.ComponentType;
    type?: 'default' | 'danger';
}

export interface NavDropdownProps {
    type: 'roadmap' | 'study' | 'user';
    items: DropdownItem[];
    isOpen: boolean;
    onClose: () => void;
}

// features/layout/types/footer.types.ts
export interface FooterLink {
    label: string;
    href: string;
}

export interface FooterSection {
    title: string;
    links: FooterLink[];
}

// features/profile/types/profile.types.ts
export interface ProfileFormData {
    nickname: string;
    position: string;
    school: string;
    experience: string;
    description: string;
    links: string[];
}

export type ProfilePosition =
    | '프론트엔드'
    | '백엔드'
    | '풀스택'
    | '데브옵스'
    | '모바일';

export type ExperienceYear =
    | '0년'
    | '1년'
    | '2년'
    | '3년'
    | '4년'
    | '5년 이상';

export interface ProfileValidation extends Partial<ProfileFormData> {
    submit?: string;
}

// features/roadmap/types/roadmap.types.ts
export interface RoadmapNode {
    id: string;
    title: string;
    description: string;
    status: 'not-started' | 'in-progress' | 'completed';
    children?: RoadmapNode[];
}

export interface Roadmap {
    id: string;
    title: string;
    description: string;
    category: string;
    difficulty: 'beginner' | 'intermediate' | 'advanced';
    nodes: RoadmapNode[];
    createdAt: string;
    updatedAt: string;
}

// features/auth/types/auth.types.ts
export interface SignUpFormData {
    nickname: string;
    token: string;
    tokenExpirationTime: string;
    termsAgreed: boolean;
    privacyPolicyAgreed: boolean;
    marketingAgreed: boolean;
}

export interface GoogleLoginButtonProps {
    onLoginSuccess?: () => void;
    isLoading?: boolean;
}

export interface AuthResponse {
    token: string;
    memberInfo: {
        id: number;
        email: string;
        nickname: string;
        profileImage?: string;
    };
}
