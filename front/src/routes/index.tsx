import { createBrowserRouter } from 'react-router-dom';
import RootLayout from '../layout/RootLayout';
import Main from '../pages/main/Main';

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
        ],
    },
]);