import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Header from './components/layout/Header';
import HeroSection from './components/home/HeroSection';
import CommunityPage from './components/community/CommunityPage';
import PostDetail from './components/community/PostDetail';
import WritePost from "@/main/resources/static/components/community/PostWrite";
import PostWrite from "@/main/resources/static/components/community/PostWrite";  // 추가

const Home = () => (
    <main>
        <HeroSection />
        <div className="text-center text-gray-500 py-12">
            수천 명의 축제 애호가들이 선택한 믿을 수 있는 동행 플랫폼
        </div>
    </main>
);

const App = () => {
    return (
        <BrowserRouter>
            <div className="min-h-screen bg-white">
                <Header />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/community" element={<CommunityPage />} />
                    <Route path="/post/:id" element={<PostDetail />} />
                    <Route path="/post/write" element={<PostWrite />} />
                    <Route path="/post/edit/:postId" element={<PostWrite />} />
                </Routes>
            </div>
        </BrowserRouter>
    );
};

export default App;