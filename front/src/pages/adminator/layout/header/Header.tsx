import React from 'react';
import SearchBox from './SearchBox.tsx';

const Header: React.FC = () => {
    return (
        <div className="header navbar">
            <div className="header-container">
                {/* Left Navigation */}
                <ul className="nav-left">
                    <li>
                        <a id="sidebar-toggle" className="sidebar-toggle" href="#">
                            <i className="ti-menu"></i>
                        </a>
                    </li>
                    <SearchBox /> {/* SearchBox 컴포넌트 사용 */}
                </ul>

                {/* Right Navigation */}
                <ul className="nav-right">
                    {/* Notifications */}
                    <li className="notifications dropdown">
                        <span className="counter bgc-red">3</span>
                        <a href="#" className="dropdown-toggle no-after" data-bs-toggle="dropdown">
                            <i className="ti-bell"></i>
                        </a>
                    </li>

                    {/* Emails */}
                    <li className="notifications dropdown">
                        <span className="counter bgc-blue">3</span>
                        <a href="#" className="dropdown-toggle no-after" data-bs-toggle="dropdown">
                            <i className="ti-email"></i>
                        </a>
                    </li>

                    {/* User Profile */}
                    <li className="dropdown">
                        <a href="#" className="dropdown-toggle no-after peers fxw-nw ai-c lh-1">
                            <div className="peer mR-10">
                                <img
                                    className="w-2r bdrs-50p"
                                    src="https://randomuser.me/api/portraits/men/10.jpg"
                                    alt="user"
                                />
                            </div>
                            <div className="peer">
                                <span className="fsz-sm c-grey-900">John Doe</span>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    );
};

export default Header;
