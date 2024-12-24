import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = () => {
    const navItems = [
        { title: 'Home', path: '/', hasDropdown: true },
        { title: 'Festival', path: '/festival', hasDropdown: true },
        { title: 'Community', path: '/community', hasDropdown: true },
        { title: 'MyPage', path: '/mypage', hasDropdown: true },
        { title: 'Login', path: '/login', hasDropdown: false },
    ];

    return (
        <nav className="hidden md:flex items-center space-x-6">
            {navItems.map((item, index) => (
                <div key={index} className="relative group">
                    {/* Link 태그 시작 */}
                    <Link
                        to={item.path || '#'}
                        className="flex items-center space-x-1 text-white hover:text-gray-200"
                    >
                        {/* 버튼 태그는 제거 또는 스타일링만 유지 */}
                        <span>{item.title}</span>
                        {item.hasDropdown && (
                            <svg
                                className="w-4 h-4"
                                fill="none"
                                stroke="currentColor"
                                viewBox="0 0 24 24"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M19 9l-7 7-7-7"
                                />
                            </svg>
                        )}
                    </Link>
                    {/* Link 태그 끝 */}
                </div>
            ))}
        </nav>
    );
};

export default Navigation;
