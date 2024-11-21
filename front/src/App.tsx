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


const App: React.FC = () => {
    return (
        <AuthProvider>
            <ModalProvider>
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
                            <Route path="chat">
                                <Route index element={<ChatList />} />
                                <Route path=":roomId" element={<ChatRoom />} />
                            </Route>

                            {/* 404 페이지 처리 */}
                            <Route path="*" element={<div>Page Not Found</div>} />
                        </Route>
                    </Routes>
                </BrowserRouter>
            </ModalProvider>
        </AuthProvider>
    );
};

export default App;