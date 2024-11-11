import React from 'react';
import { RouterProvider } from 'react-router-dom';
import { router } from './routes';
import { AuthProvider } from './context/AuthContext';
import {ModalProvider} from "./context/ModalContext.tsx";

const App: React.FC = () => {
    return (
        <AuthProvider>
            <ModalProvider>
            <RouterProvider router={router} />
            </ModalProvider>
        </AuthProvider>
    );
};

export default App;