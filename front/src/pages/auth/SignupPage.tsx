import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';
import SignUpForm from '@/component/signup/SignUpForm';

const SignupPage: React.FC = () => {
    const location = useLocation();
    const tempToken = location.state?.tempToken;

    if (!tempToken) {
        return <Navigate to="/login" replace />;
    }

    return <SignUpForm tempToken={tempToken} />;
};

export default SignupPage;
