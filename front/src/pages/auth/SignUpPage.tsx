import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, User, Loader2, Check } from 'lucide-react';

interface SignupFormData {
    email: string;
    password: string;
    passwordConfirm: string;
    nickname: string;
    agreements: {
        terms: boolean;
        privacy: boolean;
        marketing: boolean;
    };
}

interface ValidationErrors {
    email?: string;
    password?: string;
    passwordConfirm?: string;
    nickname?: string;
}

export const SignUpPage: React.FC = () => {
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(false);
    const [formData, setFormData] = useState<SignupFormData>({
        email: '',
        password: '',
        passwordConfirm: '',
        nickname: '',
        agreements: {
            terms: false,
            privacy: false,
            marketing: false,
        }
    });
    const [errors, setErrors] = useState<ValidationErrors>({});

    const validateForm = (): boolean => {
        const newErrors: ValidationErrors = {};

        // 닉네임 검증
        if (formData.nickname.length < 2 || formData.nickname.length > 10) {
            newErrors.nickname = '닉네임은 2-10자 사이여야 합니다.';
        }

        // 이메일 검증
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(formData.email)) {
            newErrors.email = '유효한 이메일 주소를 입력해주세요.';
        }

        // 비밀번호 검증
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
        if (!passwordRegex.test(formData.password)) {
            newErrors.password = '비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.';
        }

        // 비밀번호 확인 검증
        if (formData.password !== formData.passwordConfirm) {
            newErrors.passwordConfirm = '비밀번호가 일치하지 않습니다.';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        // 입력 시 해당 필드의 에러 메시지 제거
        setErrors(prev => ({
            ...prev,
            [name]: undefined
        }));
    };

    const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            agreements: {
                ...prev.agreements,
                [name]: checked
            }
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!formData.agreements.terms || !formData.agreements.privacy) {
            alert('필수 약관에 동의해주세요.');
            return;
        }

        if (!validateForm()) {
            return;
        }

        setIsLoading(true);
        try {
            // API 호출 로직 구현
            // const response = await signup(formData);
            navigate('/login'); // 회원가입 성공 시 로그인 페이지로 이동
        } catch (error) {
            console.error('회원가입 실패:', error);
            alert('회원가입에 실패했습니다. 다시 시도해주세요.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                {/* 헤더 섹션 */}
                <div className="text-center">
                    <h2 className="text-3xl font-bold text-gray-900">MOYA 회원가입</h2>
                    <p className="mt-2 text-sm text-gray-600">
                        이미 계정이 있으신가요?{' '}
                        <Link to="/login" className="font-medium text-moya-primary hover:text-moya-primary/80">
                            로그인
                        </Link>
                    </p>
                </div>

                {/* 폼 섹션 */}
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm space-y-4">
                        {/* 닉네임 필드 */}
                        <div>
                            <label htmlFor="nickname" className="block text-sm font-medium text-gray-700 mb-1">
                                닉네임
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <User className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="nickname"
                                    name="nickname"
                                    type="text"
                                    required
                                    className={`appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border ${
                                        errors.nickname ? 'border-red-500' : 'border-gray-300'
                                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm`}
                                    placeholder="닉네임 (2-10자)"
                                    value={formData.nickname}
                                    onChange={handleInputChange}
                                />
                            </div>
                            {errors.nickname && (
                                <p className="mt-1 text-sm text-red-500">{errors.nickname}</p>
                            )}
                        </div>

                        {/* 이메일 필드 */}
                        <div>
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                이메일
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    required
                                    className={`appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border ${
                                        errors.email ? 'border-red-500' : 'border-gray-300'
                                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm`}
                                    placeholder="이메일 주소"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                />
                            </div>
                            {errors.email && (
                                <p className="mt-1 text-sm text-red-500">{errors.email}</p>
                            )}
                        </div>

                        {/* 비밀번호 필드 */}
                        <div>
                            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                                비밀번호
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    className={`appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border ${
                                        errors.password ? 'border-red-500' : 'border-gray-300'
                                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm`}
                                    placeholder="비밀번호 (영문, 숫자, 특수문자 포함 8자 이상)"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                />
                            </div>
                            {errors.password && (
                                <p className="mt-1 text-sm text-red-500">{errors.password}</p>
                            )}
                        </div>

                        {/* 비밀번호 확인 필드 */}
                        <div>
                            <label htmlFor="passwordConfirm" className="block text-sm font-medium text-gray-700 mb-1">
                                비밀번호 확인
                            </label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="passwordConfirm"
                                    name="passwordConfirm"
                                    type="password"
                                    required
                                    className={`appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border ${
                                        errors.passwordConfirm ? 'border-red-500' : 'border-gray-300'
                                    } placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm`}
                                    placeholder="비밀번호 확인"
                                    value={formData.passwordConfirm}
                                    onChange={handleInputChange}
                                />
                            </div>
                            {errors.passwordConfirm && (
                                <p className="mt-1 text-sm text-red-500">{errors.passwordConfirm}</p>
                            )}
                        </div>
                    </div>

                    {/* 약관 동의 섹션 */}
                    <div className="space-y-4">
                        <div className="flex items-start">
                            <div className="flex items-center h-5">
                                <input
                                    id="terms"
                                    name="terms"
                                    type="checkbox"
                                    checked={formData.agreements.terms}
                                    onChange={handleCheckboxChange}
                                    className="h-4 w-4 text-moya-primary focus:ring-moya-primary border-gray-300 rounded"
                                />
                            </div>
                            <div className="ml-3 text-sm">
                                <label htmlFor="terms" className="font-medium text-gray-700">
                                    이용약관 동의 <span className="text-red-500">*</span>
                                </label>
                                <p className="text-gray-500">
                                    <Link to="/terms" className="text-moya-primary hover:text-moya-primary/80">
                                        자세히 보기
                                    </Link>
                                </p>
                            </div>
                        </div>

                        <div className="flex items-start">
                            <div className="flex items-center h-5">
                                <input
                                    id="privacy"
                                    name="privacy"
                                    type="checkbox"
                                    checked={formData.agreements.privacy}
                                    onChange={handleCheckboxChange}
                                    className="h-4 w-4 text-moya-primary focus:ring-moya-primary border-gray-300 rounded"
                                />
                            </div>
                            <div className="ml-3 text-sm">
                                <label htmlFor="privacy" className="font-medium text-gray-700">
                                    개인정보 처리방침 동의 <span className="text-red-500">*</span>
                                </label>
                                <p className="text-gray-500">
                                    <Link to="/privacy" className="text-moya-primary hover:text-moya-primary/80">
                                        자세히 보기
                                    </Link>
                                </p>
                            </div>
                        </div>

                        <div className="flex items-start">
                            <div className="flex items-center h-5">
                                <input
                                    id="marketing"
                                    name="marketing"
                                    type="checkbox"
                                    checked={formData.agreements.marketing}
                                    onChange={handleCheckboxChange}
                                    className="h-4 w-4 text-moya-primary focus:ring-moya-primary border-gray-300 rounded"
                                />
                            </div>
                            <div className="ml-3 text-sm">
                                <label htmlFor="marketing" className="font-medium text-gray-700">
                                    마케팅 정보 수신 동의 <span className="text-gray-500">(선택)</span>
                                </label>
                                <p className="text-gray-500">이벤트 및 혜택 정보를 받아보실 수 있습니다.</p>
                            </div>
                        </div>
                    </div>

                    {/* 회원가입 버튼 */}
                    <div>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-moya-primary hover:bg-moya-primary/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-moya-primary disabled:bg-gray-400 disabled:cursor-not-allowed"
                        >
                            {isLoading ? (
                                <div className="flex items-center">
                                    <Loader2 className="w-5 h-5 mr-2 animate-spin" />
                                    처리중...
                                </div>
                            ) : (
                                <>
                                    <Check className="w-5 h-5 mr-2" />
                                    회원가입
                                </>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SignUpPage;
