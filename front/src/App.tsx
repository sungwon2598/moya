import React from 'react';
import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { ModalProvider } from './core/providers/context/ModalContext';
import RootLayout from './components/layouts/RootLayout.tsx';
import Main from './pages/main/MainPage.tsx';
import RoadmapPreview from './features/roadmap/RoadmapPreview.tsx';
import { ProfilePage } from './pages/profile/index.tsx';
import { store } from './store/store.ts';
import { Provider } from 'react-redux';
import SignInPage from './pages/auth/SignInPage.tsx';
import SignUpPage from './pages/auth/SignUpPage.tsx';
import StudyList from './pages/study/list/index.tsx';

// StudyList.tsx
import StudyPostDetail from './pages/study/StudyPostDetail.tsx';
import AdminLayout from '@pages/adminator/layout/AdminLayout.tsx';
import CategoryManagement from '@pages/category/CategoryManagement.tsx';
import { ProtectedRoute } from '@/components/features/auth/components/ProtectedRoute.tsx';
import { AdminRoute } from '@/components/features/auth/components/AdminRoute.tsx';

import { StudyCreate } from '@pages/study/index.ts';

import CreateSample from '@pages/create-sample/CreateSample.tsx';
import LearningRoad from '@pages/learningRoad/LearningRoad.tsx';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import CreatePage from './pages/roadmap/CreatePage.tsx';
import RoadmapPending from './pages/roadmap/RoadmapPending.tsx';
import WeeklyPlan from './pages/roadmap/WeeklyPlan.tsx';

const App: React.FC = () => {
  const queryClient = new QueryClient();

  return (
    <QueryClientProvider client={queryClient}>
      <Provider store={store}>
        <ModalProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<RootLayout />}>
                <Route index element={<Main />} />
                <Route path="signin" element={<SignInPage />} />
                <Route path="signup" element={<SignUpPage />} />

                {/* 로드맵 관련 라우트 */}
                <Route path="roadmap">
                  <Route path="create" element={<CreatePage />} />
                  <Route path="pending" element={<RoadmapPending />} />
                  <Route path="weeklyPlan" element={<WeeklyPlan />} />

                  <Route path="preview" element={<RoadmapPreview />} />
                  <Route path="road" element={<LearningRoad />} />
                </Route>

                {/* 스터디 관련 라우트 */}
                <Route path="study">
                  <Route index element={<StudyList />} />
                  <Route path=":postId" element={<StudyPostDetail />} />
                  <Route path="create" element={<StudyCreate />} />
                </Route>

                <Route
                  path="go"
                  element={
                    <ProtectedRoute>
                      <AdminRoute>
                        <Outlet />
                      </AdminRoute>
                    </ProtectedRoute>
                  }>
                  <Route path="categorys" element={<CategoryManagement />} />
                  <Route path="create-sample" element={<CreateSample />} />
                </Route>
                <Route path="settings/profile" element={<ProfilePage />} />

                {/* 404 페이지 처리 */}
                <Route
                  path="*"
                  element={
                    <div className="flex min-h-screen items-center justify-center">
                      <h1 className="text-2xl font-bold text-gray-800">페이지를 찾을 수 없습니다</h1>
                    </div>
                  }
                />
              </Route>

              <Route path="ggo" element={<AdminLayout />}>
                <Route path="categories" element={<CategoryManagement />} />
                <Route
                  path="*"
                  element={
                    <div className="flex min-h-screen items-center justify-center">
                      <h1 className="text-2xl font-bold text-gray-800">관리자 페이지를 찾을 수 없습니다</h1>
                    </div>
                  }
                />
              </Route>
            </Routes>
          </BrowserRouter>
        </ModalProvider>
      </Provider>
    </QueryClientProvider>
  );
};

export default App;
