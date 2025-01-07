import React, { useState, useRef } from 'react';

const SearchBox: React.FC = () => {
    const [isActive, setIsActive] = useState<boolean>(false);
    const inputRef = useRef<HTMLInputElement>(null);

    const handleToggle = (e: React.MouseEvent<HTMLAnchorElement>) => {
        e.preventDefault();
        setIsActive(!isActive);
        if (!isActive) {
            setTimeout(() => inputRef.current?.focus(), 0); // 검색창 포커스
        }
    };

    return (
        <>
            <li className="search-box flex items-center">
                <a
                    className="search-toggle no-pdd-right cursor-pointer flex items-center"
                    onClick={handleToggle}
                    href="#"
                >
                    {/* 상태에 따라 돋보기 아이콘 또는 X 아이콘 보여줌 */}
                    <i className={`search-icon ti-search pdd-right-10 ${isActive ? 'hidden' : ''}`}></i>
                    <i className={`search-icon ti-close pdd-right-10 ${isActive ? '' : 'hidden'}`}></i>
                </a>
            </li>
            {/* 검색창: 상태가 활성화될 때만 표시 */}
            <li className={`search-input ${isActive ? 'block' : 'hidden'}`}>
                <input
                    ref={inputRef}
                    className="form-control w-[200px] rounded bg-transparent border border-gray-300 p-2 focus:outline-none"
                    type="text"
                    placeholder="Search..."
                />
            </li>
        </>
    );
};

export default SearchBox;
