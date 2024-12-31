import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { ModalProvider } from './core/providers/context/ModalContext';
import RootLayout from './features/layout/RootLayout';
import Main from './pages/main/MainContent.tsx';
import RoadmapPreview from './features/roadmap/RoadmapPreview.tsx';
import EditProfile from "./features/profile/EditProfile.tsx";
import {store} from './core/store/store.ts'
import {Provider} from "react-redux";
import SignInPage from "./pages/auth/SignInPage.tsx";
import SignUpPage from "./pages/auth/SignUpPage.tsx";
import StudyList from "./pages/study/StudyList.tsx";
import StudyPostDetail from "./pages/study/StudyPostDetail.tsx";
import AdminLayout from "@pages/adminator/layout/AdminLayout.tsx";
import CategoryManagement from "@pages/category/CategoryManagement.tsx";

const App: React.FC = () => {
    return (
        <Provider store={store}>
            <ModalProvider>
                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<RootLayout />}>
                            <Route index element={<Main />} />
                            <Route path="signin" element={<SignInPage />} />
                            <Route path="signup" element={<SignUpPage /> } />

                                {/* 로드맵 관련 라우트 */}
                                <Route path="roadmap">
                                    <Route path="preview" element={<RoadmapPreview />} />
                                </Route>

                                {/* 스터디 관련 라우트 */}
                                <Route path="study">
                                    <Route index element={<StudyList />} />
                                    <Route path=":postId" element={<StudyPostDetail />} />
                                </Route>

                                <Route path="admin">
                                    <Route path="categorys" element={<CategoryManagement />} />
                                </Route>

                            <Route path="settings/profile" element={<EditProfile />} />

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
                        </Route>

                        <Route path="admin" element={<AdminLayout />}>
                            <Route path="categories" element={<CategoryManagement />} />
                            <Route
                                path="*"
                                element={
                                    <div className="flex min-h-screen items-center justify-center">
                                        <h1 className="text-2xl font-bold text-gray-800">
                                            관리자 페이지를 찾을 수 없습니다
                                        </h1>
                                    </div>
                                }
                            />
                        </Route>
                    </Routes>
                </BrowserRouter>
            </ModalProvider>
        </Provider>
    );
};

export default App;
