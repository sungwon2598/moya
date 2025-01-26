import React, { useEffect } from 'react';
import LeftSidebar from "@pages/adminator/layout/sidebar/LeftSidebar";
import Dashboard from "@pages/adminator/components/dashboard/Dashboard";

const AdminLayout: React.FC = () => {
    useEffect(() => {
        document.body.classList.add('adminator-body');
        return () => document.body.classList.remove('adminator-body');
    }, []);

    return (
        <div className="adminator">
            <LeftSidebar />
            <Dashboard />
        </div>
    );
};

export default AdminLayout;