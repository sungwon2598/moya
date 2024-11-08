import React from 'react'
import MainContent from './MainContent.tsx'
import {useModal} from "../../hooks/useModal.ts";

const Main: React.FC = () => {

    const {showModal} = useModal();

    const handleTestModal = () => {
        showModal(
            <div className="space-y-4">
                <h3 className="text-lg font-medium">테스트 모달</h3>
                <p className="text-gray-600">
                    이것은 모달 컴포넌트 테스트입니다.
                    여러 가지 기능을 테스트해볼 수 있습니다.
                </p>
                <div className="flex flex-col gap-3">
                    {/* 다양한 버튼 테스트 */}
                    <button className="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                        기본 버튼
                    </button>
                    <button className="w-full px-4 py-2 border border-blue-600 text-blue-600 rounded-lg hover:bg-blue-50 transition-colors">
                        아웃라인 버튼
                    </button>

                    {/* 입력 필드 테스트 */}
                    <input
                        type="text"
                        placeholder="텍스트 입력"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
                    />

                    {/* 중첩 모달 테스트 */}
                    <button
                        onClick={() => {
                            showModal(
                                <div className="p-4 text-center">
                                    <h4 className="text-lg font-medium mb-2">중첩 모달</h4>
                                    <p>모달 안의 모달입니다!</p>
                                </div>,
                                {
                                    title: '중첩 모달 테스트',
                                    size: 'sm'
                                }
                            );
                        }}
                        className="w-full px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors"
                    >
                        중첩 모달 열기
                    </button>
                </div>
            </div>,
            {
                title: '모달 테스트',
                size: 'md',
                showCloseButton: true,
            }
        );
    };

    const handleSizeTestModal = (size: 'sm' | 'md' | 'lg') => {
        showModal(
            <div className="space-y-4">
                <h3 className="text-lg font-medium">{size.toUpperCase()} 사이즈 모달</h3>
                <p className="text-gray-600">
                    이 모달은 {size} 크기로 설정되었습니다.
                </p>
            </div>,
            {
                title: `${size.toUpperCase()} 모달`,
                size: size,
                showCloseButton: true,
            }
        );
    };



    return (
        <div className="min-h-screen bg-gray-50">
            <MainContent/>
            <div className="w-full max-w-4xl flex flex-col items-center gap-4 mb-12">
                <div className="flex flex-wrap gap-4 justify-center">
                    <button
                        onClick={handleTestModal}
                        className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        기본 모달 테스트
                    </button>
                    <button
                        onClick={() => handleSizeTestModal('sm')}
                        className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
                    >
                        Small 모달
                    </button>
                    <button
                        onClick={() => handleSizeTestModal('md')}
                        className="px-6 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 transition-colors"
                    >
                        Medium 모달
                    </button>
                    <button
                        onClick={() => handleSizeTestModal('lg')}
                        className="px-6 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                    >
                        Large 모달
                    </button>
                </div>
            </div>
        </div>
    )
}

export default Main