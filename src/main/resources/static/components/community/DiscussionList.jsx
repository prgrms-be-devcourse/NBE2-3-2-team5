import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Pagination from '../common/Pagination';

const DiscussionList = () => {
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(5);
    const [totalPages, setTotalPages] = useState(1);
    const [searchTerm, setSearchTerm] = useState('');
    const [isShowingResults, setIsShowingResults] = useState(false);
    const [isSearching, setIsSearching] = useState(false);
    const navigate = useNavigate();

    const getCategoryStyle = (category) => {
        switch (category) {
            case 'COMPANION':
                return 'bg-blue-100 text-blue-600';
            case 'REVIEW':
                return 'bg-green-100 text-green-600';
            case 'QNA':
                return 'bg-purple-100 text-purple-600';
            default:
                return 'bg-gray-100 text-gray-600';
        }
    };

    const getCategoryLabel = (category) => {
        switch (category) {
            case 'COMPANION':
                return '동행자 모집';
            case 'REVIEW':
                return '후기';
            case 'QNA':
                return 'Q&A';
            default:
                return '기타';
        }
    };

    const fetchPosts = async (url) => {
        try {
            const response = await fetch(url, {
                headers: { 'Content-Type': 'application/json' },
            });
            if (!response.ok) throw new Error('Failed to fetch posts');
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error:', error);
            alert('게시글을 불러오는 중 오류가 발생했습니다.');
            return null;
        }
    };

    // 목록 데이터 로드
    useEffect(() => {
        const loadPosts = async () => {
            const url = `/api/companions?page=${currentPage}&size=${pageSize}`;
            const data = await fetchPosts(url);
            if (data) {
                setPosts(data.content || []);
                setTotalPages(data.totalPages || 1);
            }
        };

        loadPosts();
    }, [currentPage, pageSize, isSearching]);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchTerm.trim()) {
            setIsSearching(false);
            setIsShowingResults(false);
            return;
        }

        setIsSearching(true);
        setIsShowingResults(true);
        const url = `/api/companions/search?keyword=${encodeURIComponent(searchTerm)}`;
        const data = await fetchPosts(url);
        if (data) {
            setPosts(data || []);
            setTotalPages(Math.ceil((data.length || 0) / pageSize));
        }
    };

    const handleResetSearch = () => {
        setSearchTerm('');
        setIsSearching(false);
        setIsShowingResults(false);
        setCurrentPage(1);
    };

    const handlePageChange = (page) => {
        setCurrentPage(page);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleWriteClick = () => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요합니다. [로그인] 또는 [회원가입] 후 다시 시도해주세요.');
            navigate('/login');
            return;
        }
        navigate('/post/write');
    };

    return (
        <div className="bg-white rounded-lg p-6 shadow-lg">
            {/* 헤더 섹션 */}
            <div className="flex items-center justify-between mb-8">
                <div className="flex items-center gap-8">
                    <div className="flex items-center gap-3">
                        <span className="text-3xl">👥</span>
                        <h1 className="text-3xl font-bold text-gray-800">Community</h1>
                    </div>
                    <button
                        onClick={handleWriteClick}
                        className="bg-[#4D4B88] text-white px-5 py-2.5 rounded-lg hover:opacity-90 transition-all flex items-center gap-2 shadow-sm"
                    >
                        <span className="text-lg">✏️</span>
                        <span className="font-medium">새 글 작성</span>
                    </button>
                </div>
                <div className="flex gap-3">
                    <form onSubmit={handleSearch} className="relative flex items-center">
                        <div className="relative">
                            <input
                                type="text"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                placeholder="게시물 검색..."
                                className="pl-10 pr-8 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4D4B88] focus:border-transparent w-72"
                            />
                            <span className="absolute left-3 top-2.5">🔍</span>
                            {searchTerm && (
                                <button
                                    type="button"
                                    onClick={handleResetSearch}
                                    className="absolute right-2 top-2.5 text-gray-400 hover:text-gray-600"
                                >
                                    ✕
                                </button>
                            )}
                        </div>
                        <button
                            type="submit"
                            className="ml-2 px-4 py-2 bg-[#4D4B88] text-white rounded-lg hover:opacity-90"
                        >
                            검색
                        </button>
                    </form>
                </div>
            </div>

            {/* 검색 결과 표시 */}
            {isSearching && isShowingResults && (
                <div className="mb-4 flex justify-between items-center">
                    <p className="text-gray-600">
                        '{searchTerm}' 검색 결과: {posts.length}개
                    </p>
                    <button
                        onClick={handleResetSearch}
                        className="text-sm text-gray-500 hover:text-gray-700"
                    >
                        전체 목록 보기
                    </button>
                </div>
            )}

            {/* 게시글 목록 */}
            {posts.length > 0 ? (
                <div className="space-y-6">
                    {posts.map((post) => (
                        <Link
                            key={post.id}
                            to={`/post/${post.id}`}
                            className="block p-6 border border-gray-100 rounded-xl hover:border-[#4D4B88] transition-all hover:shadow-md"
                        >
                            <div className="flex items-center justify-between mb-4">
                                <div className="flex items-center gap-4">
                                    <img
                                        src={post.avatar || "/assets/images/default-avatar.png"}
                                        alt=""
                                        className="w-10 h-10 rounded-full border-2 border-gray-100"
                                    />
                                    <div className="flex items-center gap-2 text-sm">
                                        <span className="font-medium text-gray-900">
                                            {post.writer || "Unknown"}
                                        </span>
                                        <span className="text-gray-400">•</span>
                                        <span className="text-gray-500">{post.time || "Unknown time"}</span>
                                        <span className={`px-3 py-1 rounded-full text-sm ${getCategoryStyle(post.category)}`}>
                                            {getCategoryLabel(post.category)}
                                        </span>
                                    </div>
                                </div>
                                <div className="flex items-center gap-4 text-sm text-gray-500">
                                    <span className="flex items-center gap-1">
                                        <span>💬</span>
                                        {(post.replies || 0)} replies
                                    </span>
                                    <span className="flex items-center gap-1">
                                        <span>👁️</span>
                                        {post.views} views
                                    </span>
                                </div>
                            </div>
                            <h2 className="text-xl font-bold text-gray-900 mb-3 hover:text-[#4D4B88] transition-colors">
                                {post.title}
                            </h2>
                            <p className="text-gray-600 mb-4 line-clamp-2">
                                {post.content}
                            </p>
                            <div className="flex flex-wrap gap-2">
                                {(post.tags || []).map((tag) => (
                                    <span
                                        key={tag}
                                        className="px-3 py-1 bg-[#4D4B88] text-white rounded-full text-sm font-medium"
                                    >
                                        #{tag}
                                    </span>
                                ))}
                            </div>
                        </Link>
                    ))}
                </div>
            ) : (
                <div className="text-center py-12">
                    <h3 className="text-xl font-medium text-gray-600">
                        {isSearching ? '검색 결과가 없습니다.' : '게시글이 없습니다.'}
                    </h3>
                    <p className="text-gray-500 mt-2">
                        {isSearching ? '다른 검색어로 시도해보세요.' : '첫 번째 게시글의 작성자가 되어보세요!'}
                    </p>
                </div>
            )}

            {/* 페이지네이션 */}
            {totalPages > 1 && !isSearching && (
                <div className="mt-8">
                    <Pagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={handlePageChange}
                    />
                </div>
            )}
        </div>
    );
};

export default DiscussionList;