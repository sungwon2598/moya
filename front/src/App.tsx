import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ModalProvider } from './core/providers/context/ModalContext';
import RootLayout from './features/layout/RootLayout';
import Main from './pages/main/MainContent.tsx';
import RoadmapPreview from './features/roadmap/RoadmapPreview.tsx';
import EditProfile from "./features/profile/EditProfile.tsx";
import {store} from './core/store/store.ts'
import {Provider} from "react-redux";

const App: React.FC = () => {
    return (
        <Provider store={store}>
            <ModalProvider>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/" element={<RootLayout />}>
                                {/* 메인 페이지 */}
                                <Route index element={<Main />} />

                                {/* 로드맵 관련 라우트 */}
                                <Route path="roadmap">
                                    <Route path="preview" element={<RoadmapPreview />} />
                                </Route>


                                {/* 404 페이지 처리 */}
                                <Route
                                    path="*"
                                    element={
                                        <div className="flex min-h-screen items-center justify-center">
                                            <h1 className="text-2xl font-bold text-gray-800">
                                                페이지를 찾을 수 없습니다
                                            </h1>
                                        </div>
                                    }
                                />

                                <Route path="profile/edit" element={<EditProfile />} />

                                {/* 404 페이지 처리 */}
                                <Route path="*" element={<div>Page Not Found</div>} />
                            </Route>
                        </Routes>
                    </BrowserRouter>
            </ModalProvider>
        </Provider>
    );
};

export default App;