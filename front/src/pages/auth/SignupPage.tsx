import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';
import SignUpForm from '@/component/signup/SignUpForm';

const SignUpPage: React.FC = () => {
    const [searchParams] = useSearchParams();

    const accessToken = searchParams.get('accessToken');
    const nextStep = searchParams.get('nextStep');
    const isNewUser = searchParams.get('isNewUser');

    if (!accessToken || nextStep !== 'SIGNUP') {
        return <Navigate to="/" replace />;
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <SignUpForm tempToken={accessToken} />
        </div>
    );
};

export default SignUpPage;
