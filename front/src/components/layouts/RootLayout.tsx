import React from 'react';
import { Outlet } from 'react-router-dom';
import { Header } from './header/Header.tsx';
import { ModalRoot } from '@/components/shared/Modal/ModalRoot.tsx';
import Footer from './footer/Footer.tsx';

const RootLayout: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <ModalRoot />
      <Footer />
    </div>
  );
};

export default RootLayout;
