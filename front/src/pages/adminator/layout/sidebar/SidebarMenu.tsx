import React from 'react';
// import '../../assets/styles/vendor/themify-icons.css';
import '../../assets/styles/index.scss';

type MenuItem = {
    title: string;
    href: string;
    iconClass: string;
    subMenu?: MenuItem[]; // 드롭다운 메뉴일 경우 하위 항목
};

const menuItems: MenuItem[] = [
    { title: 'Dashboard', href: 'index.html', iconClass: 'text-blue-500 ti-home' },
    { title: 'Email', href: 'email.html', iconClass: 'c-brown-500 ti-email' },
    { title: 'Compose', href: 'compose.html', iconClass: 'c-blue-500 ti-share' },
    { title: 'Calendar', href: 'calendar.html', iconClass: 'c-deep-orange-500 ti-calendar' },
    { title: 'Chat', href: 'chat.html', iconClass: 'c-deep-purple-500 ti-comment-alt' },
    { title: 'Charts', href: 'charts.html', iconClass: 'c-indigo-500 ti-bar-chart' },
    { title: 'Forms', href: 'forms.html', iconClass: 'c-light-blue-500 ti-pencil' },
    {
        title: 'Tables',
        href: 'javascript:void(0);',
        iconClass: 'c-orange-500 ti-layout-list-thumb',
        subMenu: [
            { title: 'Basic Table', href: 'basic-table.html', iconClass: '' },
            { title: 'Data Table', href: 'datatable.html', iconClass: '' },
        ],
    },
    {
        title: 'Maps',
        href: 'javascript:void(0);',
        iconClass: 'c-purple-500 ti-map',
        subMenu: [
            { title: 'Google Map', href: 'google-maps.html', iconClass: '' },
            { title: 'Vector Map', href: 'vector-maps.html', iconClass: '' },
        ],
    },
    {
        title: 'Pages',
        href: 'javascript:void(0);',
        iconClass: 'c-red-500 ti-files',
        subMenu: [
            { title: 'Blank', href: 'blank.html', iconClass: '' },
            { title: '404', href: '404.html', iconClass: '' },
            { title: '500', href: '500.html', iconClass: '' },
            { title: 'Sign In', href: 'signin.html', iconClass: '' },
            { title: 'Sign Up', href: 'signup.html', iconClass: '' },
        ],
    },
    {
        title: 'Multiple Levels',
        href: 'javascript:void(0);',
        iconClass: 'c-teal-500 ti-view-list-alt',
        subMenu: [
            { title: 'Menu Item', href: 'javascript:void(0);', iconClass: '' },
            {
                title: 'Menu Item',
                href: 'javascript:void(0);',
                iconClass: '',
                subMenu: [
                    { title: 'Sub Menu Item 1', href: 'javascript:void(0);', iconClass: '' },
                    { title: 'Sub Menu Item 2', href: 'javascript:void(0);', iconClass: '' },
                ],
            },
        ],
    },
];

const SidebarMenu: React.FC = () => {
    const renderMenu = (menu: MenuItem[]) =>
        menu.map((item, index) => (
            <li className={`nav-item ${item.subMenu ? 'dropdown' : ''}`} key={index}>
                <a
                    className={`sidebar-link ${item.subMenu ? 'dropdown-toggle' : ''}`}
                    href={item.href}
                >
          <span className="icon-holder">
            <i className={item.iconClass}></i>
          </span>
                    <span className="title">{item.title}</span>
                    {item.subMenu && <span className="arrow"><i className="ti-angle-right"></i></span>}
                </a>
                {item.subMenu && (
                    <ul className="dropdown-menu">
                        {renderMenu(item.subMenu)}
                    </ul>
                )}
            </li>
        ));

    return <ul className="sidebar-menu scrollable pos-r">{renderMenu(menuItems)}</ul>;
};

export default SidebarMenu;
