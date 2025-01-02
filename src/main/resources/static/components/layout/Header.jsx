import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = () => {
    const navItems = [
        { title: 'Home', path: '/', hasDropdown: true },
        { title: 'Festival', path: '/festival', hasDropdown: true },
        { title: 'Community', path: '/community', hasDropdown: true },
        { title: 'MyPage', path: '/mypage', hasDropdown: true },
    ];

    return (
        <nav className="hidden md:flex items-center space-x-6">
            {navItems.map((item, index) => (
                <div key={index} className="relative group">
                    <Link
                        to={item.path}
                        className="flex items-center space-x-1 text-[#5D5A88] hover:text-[#4D4B88] font-medium"
                    >
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
                </div>
            ))}
        </nav>
    );
};

const Header = () => {
    return (
        <header className="py-4 px-6 flex items-center justify-between bg-white shadow-sm">
            <div className="flex items-center">
                <Link to="/" className="flex items-center space-x-2">
                    <img
                        src="/imgs/festimoLogo.svg"
                        alt="Festimo Logo"
                        className="h-8 w-auto"
                    />
                </Link>
            </div>

            <Navigation />

            <div className="flex items-center space-x-4">
                <Link
                    to="/api/login"
                    className="px-4 py-2 text-[#5D5A88] hover:bg-gray-50 font-medium rounded-lg transition-colors"
                >
                    Login
                </Link>
                <Link
                    to="/api/register"
                    className="px-4 py-2 bg-[#5D5A88] text-white rounded-lg hover:bg-[#4D4B88] font-medium transition-colors"
                >
                    Sign Up
                </Link>
            </div>
        </header>
    );
};

export default Header;