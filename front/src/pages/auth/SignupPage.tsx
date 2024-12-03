import React from 'react';
import { useLocation, Navigate } from 'react-router-dom';
import SignUpForm from '@/component/signup/SignUpForm';

const SignUpPage: React.FC = () => {
    const location = useLocation();
    const { tempToken } = location.state || {};

    if (!tempToken) {
        return <Navigate to="/" replace />;
    }

    return (
        <div className="container mx-auto px-4 py-8">
            <SignUpForm tempToken={tempToken} />
        </div>
    );
};

export default SignUpPage;
