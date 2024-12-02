import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';
import SignUpForm from '../../component/signup/SignUpForm';

const SignUpPage: React.FC = () => {
    const location = useLocation();
    const tempToken = location.state?.accessToken;

    if (!tempToken) {
        return <Navigate to="/" replace />;
    }

    return <SignUpForm tempToken={tempToken} />;
};

export default SignUpPage;
