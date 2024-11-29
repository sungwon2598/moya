import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ModalProvider } from './context/ModalContext';
import RootLayout from './layout/RootLayout';
import Main from './pages/main/Main';
import RoadmapPreview from './pages/RoadmapPreview';
// import LearningRoad from './pages/learningRoad/LearningRoad';
import ChatList from './pages/chat/ChatList';
import ChatRoom from './pages/chat/ChatRoom';
import OAuthCallback from './pages/auth/OAuthCallback';
import SignupPage from './pages/auth/SignupPage'; // SignupPage로 변경

const App: React.FC = () => {
    return (
        <ModalProvider>
        <AuthProvider>

                <BrowserRouter>
                    <Routes>
                        <Route path="/" element={<RootLayout />}>
                            <Route index element={<Main />} />
                            <Route path="roadmap">
                            <Route path="preview" element={<RoadmapPreview />}>
                                {/* <Route index element={<LearningRoad />} /> */}
                            </Route>
                            </Route>
                            {/* 향후 추가될 라우트들 */}
                            {/* <Route path="register" element={<Register />} /> */}
                            {/* <Route path="login" element={<Login />} /> */}

                            {/* 채팅 관련 라우트 */}

                            <Route path="/callback/google" element={<OAuthCallback />} />
                            <Route path="/signup" element={<SignupPage />} /> {/* SignupPage로 변경 */}

                            <Route path="chat">
                                <Route index element={<ChatList />} />
                                <Route path=":roomId" element={<ChatRoom />} />
                            </Route>

                            {/* 404 페이지 처리 */}
                            <Route path="*" element={<div>Page Not Found</div>} />
                        </Route>
                    </Routes>
                </BrowserRouter>
        </AuthProvider>
        </ModalProvider>

    );
};

export default App;
