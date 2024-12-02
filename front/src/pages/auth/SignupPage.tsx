import React from 'react';
import { useLocation } from 'react-router-dom';
import SignUpForm from '@/component/signup/SignUpForm';

const SignupPage: React.FC = () => {
    const location = useLocation();
    const accessToken = location.state?.accessToken;

    // if (!asscessToken) {
    //     return <Navigate to="/" replace />;
    // }

    return <SignUpForm asscessToken={accessToken} />;
};

export default SignupPage;
