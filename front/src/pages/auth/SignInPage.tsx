import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, Loader2 } from 'lucide-react';

interface LoginFormData {
    email: string;
    password: string;
}

export const SignInPage: React.FC = () => {
    const [formData, setFormData] = useState<LoginFormData>({
        email: '',
        password: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string>('');

    const navigate = useNavigate();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        setError(''); // 입력이 변경되면 에러 메시지 초기화
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            // 로그인 로직 구현
            // const response = await login(formData);
            setIsLoading(false);
            navigate('/'); // 로그인 성공 시 홈으로 이동
        } catch (err) {
            setError('이메일 또는 비밀번호가 올바르지 않습니다.');
            console.log(err)
            setIsLoading(false);
        }
    };

    const handleGoogleLogin = () => {
        // Google 로그인 로직 구현
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                {/* 헤더 섹션 */}
                <div className="text-center">
                    <h2 className="text-3xl font-bold text-gray-900">
                        MOYA 로그인
                    </h2>
                    <p className="mt-2 text-sm text-gray-600">
                        또는{' '}
                        <Link to="/signup" className="font-medium text-moya-primary hover:text-moya-primary/80">
                            새로운 계정 만들기
                        </Link>
                    </p>
                </div>

                {/* 폼 섹션 */}
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm space-y-4">
                        {/* 이메일 입력 필드 */}
                        <div>
                            <label htmlFor="email" className="sr-only">이메일</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Mail className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="email"
                                    name="email"
                                    type="email"
                                    required
                                    className="appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm"
                                    placeholder="이메일 주소"
                                    value={formData.email}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </div>

                        {/* 비밀번호 입력 필드 */}
                        <div>
                            <label htmlFor="password" className="sr-only">비밀번호</label>
                            <div className="relative">
                                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                                    <Lock className="h-5 w-5 text-gray-400" />
                                </div>
                                <input
                                    id="password"
                                    name="password"
                                    type="password"
                                    required
                                    className="appearance-none rounded-lg relative block w-full pl-10 pr-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-moya-primary focus:border-moya-primary sm:text-sm"
                                    placeholder="비밀번호"
                                    value={formData.password}
                                    onChange={handleInputChange}
                                />
                            </div>
                        </div>
                    </div>

                    {/* 에러 메시지 */}
                    {error && (
                        <div className="text-red-500 text-sm text-center">
                            {error}
                        </div>
                    )}

                    {/* 추가 옵션 */}
                    <div className="flex items-center justify-between">
                        <div className="flex items-center">
                            <input
                                id="remember-me"
                                name="remember-me"
                                type="checkbox"
                                className="h-4 w-4 text-moya-primary focus:ring-moya-primary border-gray-300 rounded"
                            />
                            <label htmlFor="remember-me" className="ml-2 block text-sm text-gray-900">
                                로그인 상태 유지
                            </label>
                        </div>

                        <div className="text-sm">
                            <Link to="/forgot-password" className="font-medium text-moya-primary hover:text-moya-primary/80">
                                비밀번호 찾기
                            </Link>
                        </div>
                    </div>

                    {/* 로그인 버튼 */}
                    <div>
                        <button
                            type="submit"
                            disabled={isLoading}
                            className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-moya-primary hover:bg-moya-primary/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-moya-primary"
                        >
                            {isLoading ? (
                                <Loader2 className="w-5 h-5 animate-spin" />
                            ) : (
                                '로그인'
                            )}
                        </button>
                    </div>

                    {/* 구분선 */}
                    <div className="relative">
                        <div className="absolute inset-0 flex items-center">
                            <div className="w-full border-t border-gray-300" />
                        </div>
                        <div className="relative flex justify-center text-sm">
                            <span className="px-2 bg-gray-50 text-gray-500">또는</span>
                        </div>
                    </div>

                    {/* 소셜 로그인 버튼 */}
                    <div>
                        <button
                            type="button"
                            onClick={handleGoogleLogin}
                            className="w-full flex items-center justify-center px-4 py-2 border border-gray-300 rounded-lg shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-moya-primary"
                        >
                            <img
                                className="h-5 w-5 mr-2"
                                src="https://www.svgrepo.com/show/475656/google-color.svg"
                                alt="Google logo"
                            />
                            Google로 계속하기
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default SignInPage;
