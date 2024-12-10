import React from 'react';
import { useNavigate } from 'react-router-dom';
import { AlertCircle } from 'lucide-react';

interface LoginAlertModalProps {
    onClose: () => void;
}

const LoginAlertModal: React.FC<LoginAlertModalProps> = ({ onClose }) => {
    const navigate = useNavigate();

    const handleLogin = () => {
        onClose();
        navigate('/login');
    };

    return (
        <div className="flex flex-col items-center p-6">
            <AlertCircle className="w-12 h-12 text-yellow-500 mb-4" />
            <h2 className="text-xl font-semibold mb-4">로그인이 필요합니다</h2>
            <p className="text-gray-600 mb-6 text-center">
                팀원 모집은 로그인 후 이용할 수 있습니다.<br />
                로그인 페이지로 이동하시겠습니까?
            </p>
            <div className="flex gap-3">
                <button
                    onClick={handleLogin}
                    className="px-4 py-2 bg-emerald-500 text-white rounded-lg hover:bg-emerald-600 transition-colors"
                >
                    로그인하기
                </button>
                <button
                    onClick={onClose}
                    className="px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                    취소
                </button>
            </div>
        </div>
    );
};

export default LoginAlertModal;
