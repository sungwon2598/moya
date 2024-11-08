import React from 'react';
import { RouterProvider } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { router } from './routes';
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