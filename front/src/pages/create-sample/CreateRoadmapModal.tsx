import React from "react";
import { useModal } from "@shared/hooks/useModal";

const CreateRoadmapModal: React.FC = () => {
    const { hideModal } = useModal();

    return (
        <div className="p-6 max-w-2xl mx-auto">
            <h2 className="text-xl font-bold mb-4">새 로드맵 샘플 만들기</h2>
            {/* 여기에 로드맵 생성 폼이 들어갈 예정 */}
            <div className="mt-6 flex justify-end gap-2">
                <button
                    onClick={hideModal}
                    className="px-4 py-2 text-gray-600 hover:text-gray-800"
                >
                    취소
                </button>
                <button
                    onClick={() => {
                        // TODO: 저장 로직
                        hideModal();
                    }}
                    className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                    만들기
                </button>
            </div>
        </div>
    );
};

export default CreateRoadmapModal;