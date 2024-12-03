import React, { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import SignUpForm from '@/component/signup/SignUpForm';

const SignUpPage: React.FC = () => {
    const [accessToken, setAccessToken] = useState<string | null>(null);
    const [nextStep, setNextStep] = useState<string | null>(null);

    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const token = urlParams.get('accessToken');
        const step = urlParams.get('nextStep');

        console.log('URL Parameters:', {
            accessToken: token,
            nextStep: step
        });

        if (token) {
            setAccessToken(token);
            setNextStep(step);
        }
    }, []);

    // 유효성 검사
    if (!accessToken) {
        return <Navigate to="/" replace state={{ error: '유효하지 않은 접근입니다.' }} />;
    }

    return (
        <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12">
            <div className="sm:mx-auto sm:w-full sm:max-w-md">
                <h2 className="text-center text-3xl font-bold text-gray-900">
                    회원가입
                </h2>
            </div>
            <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
                <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
                    <SignUpForm tempToken={accessToken} />
                </div>
            </div>
        </div>
    );
};

export default SignUpPage;
