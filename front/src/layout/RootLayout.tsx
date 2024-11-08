import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from './header/Header';
// import Footer from './footer/Footer';

const RootLayout: React.FC = () => {
    return (
        <div className="flex min-h-screen flex-col">
            <Header />
            <main className="flex-1">
                <Outlet />
            </main>
            {/*<Footer />*/}
        </div>
    );
};

export default RootLayout;