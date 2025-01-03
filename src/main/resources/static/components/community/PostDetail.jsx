import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

const CATEGORY_STYLES = {
    COMPANION: 'bg-blue-100 text-blue-600',
    REVIEW: 'bg-green-100 text-green-600',
    QNA: 'bg-purple-100 text-purple-600',
    DEFAULT: 'bg-gray-100 text-gray-600'
};

const CATEGORY_LABELS = {
    COMPANION: '동행자 모집',
    REVIEW: '후기',
    QNA: 'Q&A',
    DEFAULT: '기타'
};

// 날짜 포맷팅
const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
};

const PostDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [commentInput, setCommentInput] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [post, setPost] = useState(null);
    const [likes, setLikes] = useState(0);
    const [isLiked, setIsLiked] = useState(false);
    const isInitialMount = useRef(true);

    const getCategoryStyle = (category) => {
        return CATEGORY_STYLES[category] || CATEGORY_STYLES.DEFAULT;
    };

    const getCategoryLabel = (category) => {
        return CATEGORY_LABELS[category] || CATEGORY_LABELS.DEFAULT;
    };

    const checkAuthAndFetchPost = useCallback(async () => {
        const token = localStorage.getItem('token');

        // 초기 마운트시에만 토큰 체크 및 알림 표시
        if (isInitialMount.current) {
            isInitialMount.current = false;
            if (!token) {
                alert('로그인이 필요합니다. [로그인] 또는 [회원가입] 후 다시 시도해주세요.');
                navigate('/login');
                return;
            }
        }

        try {
            const response = await fetch(`/api/companions/${id}?view=true`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Cache-Control': 'no-cache',
                    'Authorization': `Bearer ${token}`
                },
            });

            if (response.status === 401) {
                localStorage.removeItem('token');
                navigate('/api/login');
                return;
            }

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Server error response:', errorText);
                throw new Error('게시글을 불러오는데 실패했습니다.');
            }

            const data = await response.json();
            console.log('Fetched post data:', data);
            setPost(data);
            setLikes(data.likes);
            setIsLiked(data.likedByUsers?.includes(data.currentUserId) || false);
            setIsLoading(false);
        } catch (error) {
            console.error('Error:', error);
            setIsLoading(false);
        }
    }, [id, navigate]);

    useEffect(() => {
        checkAuthAndFetchPost();
    }, [checkAuthAndFetchPost]);

    const handleGoToList = () => {
        navigate('/community');
    };

    const toggleLike = async () => {
        try {
            const response = await fetch(`/api/companions/${id}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!response.ok) {
                throw new Error('좋아요 처리에 실패했습니다.');
            }

            const wasLiked = isLiked;
            setIsLiked(!wasLiked);
            setLikes(currentLikes => wasLiked ? currentLikes - 1 : currentLikes + 1);
        } catch (error) {
            console.error('Error:', error);
            alert('좋아요 처리 중 오류가 발생했습니다.');
        }
    };

    const handleEdit = () => {
        navigate(`/post/edit/${id}`);
    };

    const handleDelete = async () => {
        if (!window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) return;

        try {
            const response = await fetch(`/api/companions/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!response.ok) throw new Error('게시글 삭제에 실패했습니다.');
            navigate('/community');
        } catch (error) {
            console.error('Error:', error);
            alert('게시글 삭제 중 오류가 발생했습니다.');
        }
    };

    const handleCompanionRequest = async () => {
        if (!window.confirm('동행 신청을 하시겠습니까?')) return;

        try {
            const token = localStorage.getItem('token');
            const response = await fetch('/api/applications', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    companionId: id // 현재 게시글의 ID를 사용
                })
            });

            if (response.status === 409) {
                alert('이미 신청한 동행입니다.');
                return;
            }

            if (!response.ok) {
                throw new Error('동행 신청에 실패했습니다.');
            }

            const data = await response.json();
            alert('동행 신청이 완료되었습니다.');

        } catch (error) {
            console.error('Error:', error);
            alert('동행 신청 중 오류가 발생했습니다.');
        }
    };

    const handleCommentSubmit = async () => {
        if (!commentInput.trim()) {
            alert('댓글을 입력해주세요.');
            return;
        }

        try {
            const response = await fetch(`/api/companions/${id}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ comment: commentInput }),
            });

            if (!response.ok) throw new Error('댓글 등록에 실패했습니다.');

            const newComment = await response.json();
            setPost(prev => ({
                ...prev,
                comments: [...prev.comments, newComment],
            }));
            setCommentInput('');
        } catch (error) {
            console.error('Error:', error);
            alert('댓글 등록 중 오류가 발생했습니다.');
        }
    };

    if (isLoading) {
        return <div>Loading...</div>;
    }

    if (!post) {
        return <div>Post not found</div>;
    }

    return (
        <div className="max-w-4xl mx-auto p-6 bg-white rounded-lg shadow-lg">
            {/* 상단 섹션 */}
            <div className="border-b border-gray-200 pb-3">
                <div className="flex items-center justify-between mb-3">
                    <div className="flex items-center gap-3">
                        <img
                            src={post.avatar || "/imgs/default-avatar.png"}
                            alt="Avatar"
                            className="w-10 h-10 rounded-full border border-gray-200"
                        />
                        <div>
                            <p className="font-medium text-lg leading-tight">{post.writer}</p>
                            <p className="text-gray-500 text-sm">{formatDate(post.createdAt)}</p>
                        </div>
                    </div>

                    {/* 작성자/일반 회원에 따른 버튼 표시 */}
                    <div className="flex gap-2">
                        {post.isOwner ? (
                            <>
                                <button
                                    onClick={handleEdit}
                                    className="px-3 py-1.5 bg-[#4D4B88] text-white rounded-lg hover:opacity-90 text-sm"
                                >
                                    수정
                                </button>
                                <button
                                    onClick={handleDelete}
                                    className="px-3 py-1.5 bg-red-500 text-white rounded-lg hover:opacity-90 text-sm"
                                >
                                    삭제
                                </button>
                            </>
                        ) : (
                            <button
                                onClick={handleCompanionRequest}
                                className="px-4 py-2 bg-[#5c5d8d] text-white rounded-lg hover:shadow-md hover:shadow-black/20 transition-all flex items-center gap-2 group text-sm font-medium"
                            >
                                <span>동행 신청</span>
                                <svg
                                    className="w-4 h-4 group-hover:translate-x-0.5 transition-transform"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                >
                                    <path
                                        strokeLinecap="round"
                                        strokeLinejoin="round"
                                        strokeWidth={2}
                                        d="M17 8l4 4m0 0l-4 4m4-4H3"
                                    />
                                </svg>
                            </button>
                        )}
                    </div>
                </div>

                <div className="space-y-2">
                    <div className="flex items-center gap-2">
                        <span className={`px-2.5 py-1 rounded-full text-sm ${getCategoryStyle(post.category)}`}>
                            {getCategoryLabel(post.category)}
                        </span>
                        <h1 className="text-xl font-bold">{post.title}</h1>
                    </div>

                    <div className="flex items-center text-gray-500 text-sm">
                    <div className="flex items-center gap-3">
                            <span className="flex items-center gap-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                </svg>
                                <span>{post.views}</span>
                            </span>
                            <span className="flex items-center gap-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />
                                </svg>
                                <span>{post.replies}</span>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            {/* 본문 섹션 */}
            <div className="mb-6">
                <p className="text-gray-700 leading-relaxed">{post.content}</p>
            </div>

            {/* 태그 섹션 */}
            <div className="mb-6">
                <h2 className="font-semibold mb-2">Tags</h2>
                <div className="flex flex-wrap gap-2">
                    {post.tags && post.tags.length > 0 ? (
                        post.tags.map((tag) => (
                            <span
                                key={tag}
                                className="px-3 py-1 bg-[#4D4B88] text-white rounded-full text-sm font-medium"
                            >
                                #{tag}
                            </span>
                        ))
                    ) : (
                        <p className="text-gray-500 text-sm">등록된 태그가 없습니다.</p>
                    )}
                </div>
            </div>

            {/* 좋아요 버튼 */}
            <div className="flex justify-end gap-4 mb-6">
                <button
                    onClick={toggleLike}
                    className={`px-4 py-2 text-white rounded-lg hover:opacity-90 ${
                        isLiked ? 'bg-red-500' : 'bg-gray-300'
                    }`}
                >
                    {isLiked ? '❤️' : '🤍'} {likes}
                </button>
            </div>

            {/* 댓글 섹션 */}
            <div className="mb-6">
                <h2 className="font-semibold mb-4">Comments</h2>
                <div className="space-y-4">
                    {post.comments && post.comments.length > 0 ? (
                        post.comments.map((comment) => (
                            <div
                                key={comment.id}
                                className="flex items-start gap-4 p-4 border border-gray-200 rounded-lg hover:border-[#4D4B88] transition-colors"
                            >
                                <img
                                    src={comment.avatar || "/imgs/default-avatar.png"}
                                    alt="Avatar"
                                    className="w-10 h-10 rounded-full border border-gray-300"
                                />
                                <div>
                                    <p className="font-medium">{comment.nickname}</p>
                                    <p className="text-gray-500 text-sm">{comment.time}</p>
                                    <p className="text-gray-700 mt-2">{comment.comment}</p>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className="text-center py-8 text-gray-500">
                            아직 작성된 댓글이 없습니다. 첫 댓글을 작성해보세요!
                        </div>
                    )}
                </div>
            </div>

            {/* 댓글 입력창 */}
            <div className="mb-6">
                <h2 className="font-semibold mb-2">Leave a Comment</h2>
                <textarea
                    className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#4D4B88]"
                    placeholder="Write your comment here..."
                    rows="4"
                    value={commentInput}
                    onChange={(e) => setCommentInput(e.target.value)}
                />
                <button
                    onClick={handleCommentSubmit}
                    className="mt-4 px-6 py-2 bg-[#4D4B88] text-white rounded-lg hover:opacity-90"
                >
                    Submit
                </button>
            </div>

            {/* 버튼 섹션 */}
            <div className="text-right">
                <button
                    className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300"
                    onClick={handleGoToList}
                >
                    목록으로
                </button>
            </div>
        </div>
    );
};

export default PostDetail;