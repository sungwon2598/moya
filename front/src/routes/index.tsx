import { createBrowserRouter } from 'react-router-dom';
import RootLayout from '../layout/RootLayout';
import Main from '../pages/main/Main';
import RoadmapPreview from "../pages/RoadmapPreview";
import LearningRoad from "../pages/learningRoad/LearningRoad";

export const router = createBrowserRouter([
    {
        path: '/',
        element: <RootLayout />,
        children: [
            {
                index: true,
                element: <Main />,
            },
            {
                path: 'register',
                // element: <Register />,
            },
            // {
            //     path: 'login',
            //     element: <Login />,
            // },
            {
                path: 'roadmap-preview',
                element: <RoadmapPreview />
            },
            {
                path: 'preview',
                element: <LearningRoad />
            }
        ],
    },
]);