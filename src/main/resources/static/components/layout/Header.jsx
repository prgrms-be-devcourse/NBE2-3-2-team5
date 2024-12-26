import React from 'react';
import Navigation from './Navigation';

const Header = () => {
    return (
        <header className="py-4 px-6 flex items-center justify-between bg-[#4D4B88] text-white">
            <div className="flex items-center">
                <div className="text-2xl font-bold flex items-center">
                    <span>Festimo</span>
                    <span className="ml-1 px-1 bg-white text-[#4D4B88] rounded text-sm">🎆</span>
                </div>
            </div>
            <Navigation />
            <div className="w-10 h-10 rounded-full overflow-hidden">
                <img
                    src="/assets/images/default-avatar.png"
                    alt="Profile"
                    className="w-full h-full object-cover"
                />
            </div>
        </header>
    );
};

export default Header;
