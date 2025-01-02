import React from 'react';
import { Link } from 'react-router-dom';

const Navigation = () => {
    const navItems = [
        { title: 'Home', path: '/' },
        { title: 'Mypage', path: '/mypage' },
        { title: 'Festival', path: '/html/festival.html' },
        { title: 'Community', path: '/community' },
    ];

    return (
        <div className="hidden md:flex items-center gap-8 text-[14.5px] translate-y-[2px]">
            {navItems.map((item, index) => (
                <div key={index}>
                    <Link
                        to={item.path}
                        className="text-[#4a4a4a] hover:text-[#4a4a4a]/80 leading-[1.2]"
                    >
                        <span>{item.title}</span>
                    </Link>
                </div>
            ))}
            <Link
                to="/api/login"
                className="text-[#4a4a4a] hover:text-[#4a4a4a]/80 leading-[1.2]"
            >
                Login
            </Link>
            <Link
                to="/api/register"
                className="px-4 py-[0.5rem] bg-[#5c5d8d] text-white rounded-[3px] hover:shadow-md hover:shadow-black/30 transition-all leading-[1.2]"
            >
                Get started
            </Link>
        </div>
    );
};

const Header = () => {
    return (
        <header style={{ padding: '1rem 2rem' }} className="flex items-center justify-between bg-white text-[14.5px] font-[Verdana,Geneva,Tahoma,sans-serif]">
            <div className="flex items-center">
                <Link to="/" className="flex items-center">
                    <img
                        src="/imgs/festimoLogo.svg"
                        alt="Festimo Logo"
                        className="w-[140px] h-auto"
                    />
                </Link>
            </div>

            <Navigation />
        </header>
    );
};

export default Header;