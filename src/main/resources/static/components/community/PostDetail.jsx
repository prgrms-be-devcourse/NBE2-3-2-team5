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
    const { postId } = useParams();
    const navigate = useNavigate();
    const [commentInput, setCommentInput] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [post, setPost] = useState(null);
    const [likes, setLikes] = useState(0);
    const [isLiked, setIsLiked] = useState(false);
    const isInitialMount = useRef(true);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [deletePassword, setDeletePassword] = useState('');
    const [deleteError, setDeleteError] = useState('');

    const getCategoryStyle = (category) => {
        return CATEGORY_STYLES[category] || CATEGORY_STYLES.DEFAULT;
    };

    const getCategoryLabel = (category) => {
        return CATEGORY_LABELS[category] || CATEGORY_LABELS.DEFAULT;
    };

    const checkAuthAndFetchPost = useCallback(async () => {
        const token = localStorage.getItem('accessToken');

        // 초기 마운트시에만 토큰 체크 및 알림 표시
        if (isInitialMount.current) {
            isInitialMount.current = false;
            if (!token) {
                alert('로그인이 필요합니다. [로그인] 또는 [회원가입] 후 다시 시도해주세요.');
                navigate('/html/login.html');
                return;
            }
        }

        try {
            const response = await fetch(`/api/companions/${postId}?view=true`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (response.status === 401) {
                localStorage.removeItem('accessToken');
                navigate('/html/login.html');
                return;
            }

            if (!response.ok) {
                throw new Error('게시글을 불러오는데 실패했습니다.');
            }

            const data = await response.json();
            setPost(data);
            setLikes(data.likes);
            setIsLiked(data.likedByUsers?.includes(data.currentUserId) || false);
            setIsLoading(false);
        } catch (error) {
            setIsLoading(false);
        }
    }, [postId, navigate]);

    // 게시글 데이터 가져오기
    useEffect(() => {
        checkAuthAndFetchPost();
    }, [checkAuthAndFetchPost]);

    // 댓글 데이터 가져오기
    useEffect(() => {
        const fetchComments = async () => {
            try {
                const token = localStorage.getItem('accessToken');
                const response = await fetch(`/api/companions/${postId}/comments`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error("댓글을 불러오는 데 실패했습니다.");
                }

                const data = await response.json();
                console.log("Comments fetched:", data);
                setPost((prev) => ({...prev, comments: data}));
            } catch (error) {
                console.error("Error fetching comments:", error);
            }
        };

        fetchComments();
    }, [postId]);

    useEffect(() => {
        const fetchPostDetails = async () => {
            try {
                const token = localStorage.getItem('accessToken');
                const response = await fetch(`/api/companions/${postId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                    },
                });

                if (!response.ok) {
                    throw new Error('게시글 정보를 불러오는 데 실패했습니다.');
                }

                const data = await response.json();
                setPost(data);
                setIsLiked(data.isLiked);
                setLikes(data.likes);
            } catch (error) {
                console.error("Error fetching post details:", error);
            }
        };

        fetchPostDetails();
    }, [postId]);

    const handleGoToList = () => {
        navigate('/community');
    };

    const toggleLike = async () => {
        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch(`/api/companions/${postId}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('좋아요 처리에 실패했습니다.');
            }

            const updatedPost = await response.json();
            setIsLiked(updatedPost.isLiked);
            setLikes(updatedPost.likes);
        } catch (error) {
            console.error('Error:', error);
            alert('좋아요 처리 중 오류가 발생했습니다.');
        }
    };

    const handleEdit = () => {
        navigate(`/post/edit/${postId}`);
    };

    const handleDelete = () => {
        if (window.confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            setShowDeleteModal(true);
        }
    };

    const handleDeleteConfirm = async () => {
        if (!deletePassword.trim()) {
            setDeleteError('비밀번호를 입력해주세요.');
            return;
        }

        try {
            const token = localStorage.getItem('accessToken');
            const requestData = { password: deletePassword };
            const response = await fetch(`/api/companions/${postId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(requestData)
            });

            if (!response.ok) {
                let errorMessage = `Status: ${response.status}, StatusText: ${response.statusText}`;
                try {
                    const errorBody = await response.text();
                    errorMessage += `, Body: ${errorBody}`;
                } catch (e) {
                    console.log('Error parsing response:', e);
                }
                throw new Error(errorMessage);
            }

            setShowDeleteModal(false);
            alert('게시글이 삭제되었습니다.');
            navigate('/community');
        } catch (error) {
            console.error('Delete Error Full Details:', {
                message: error.message,
                stack: error.stack
            });
            setDeleteError(error.message || '게시글 삭제 중 오류가 발생했습니다.');
        }
    };

    const handleCompanionRequest = async () => {
        if (!window.confirm('동행 신청을 하시겠습니까?')) return;

        try {
            const token = localStorage.getItem('accessToken');
            const response = await fetch('/api/applications', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    companionId: postId // 현재 게시글의 ID를 사용
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
        if (isSubmitting) return;
        setIsSubmitting(true);

        if (!commentInput.trim()) {
            alert('댓글을 입력해주세요.');
            setIsSubmitting(false);
            return;
        }

        try {
            const token = localStorage.getItem('accessToken');
            const userInfo = JSON.parse(localStorage.getItem('userInfo'));

            if (!userInfo || !userInfo.nickname) {
                throw new Error('닉네임 정보가 없습니다. 로그인을 다시 시도해주세요.');
            }

            const response = await fetch(`/api/companions/${postId}/comments`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    comment: commentInput,
                    nickname: userInfo.nickname,
                }),
            });

            if (!response.ok) {
                throw new Error('댓글 등록에 실패했습니다.');
            }

            const newComment = await response.json();
            setPost((prev) => ({
                ...prev,
                comments: [...prev.comments, {
                    ...newComment,
                    isOwner: true,
                    isAdmin: false,
                }],
            }));
            setCommentInput('');
        } catch (error) {
            console.error('Error during comment submission:', error);
            alert(error.message || '댓글 등록 중 오류가 발생했습니다.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleEditComment = (sequence) => {
        const newComment = prompt("댓글을 수정하세요:");
        if (!newComment) return;

        try {
            const token = localStorage.getItem("accessToken");
            fetch(`/api/companions/${postId}/comments/${sequence}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({comment: newComment}),
            })
                .then((response) => {
                    if (!response.ok) throw new Error("댓글 수정에 실패했습니다.");
                    return response.json();
                })
                .then((updatedComment) => {
                    setPost((prev) => ({
                        ...prev,
                        comments: prev.comments.map((c) =>
                            c.sequence === sequence ? {...c, comment: updatedComment.comment} : c
                        ),
                    }));
                })
                .catch((error) => alert(error.message));
        } catch (error) {
            console.error("Error editing comment:", error);
        }
    };

    const handleDeleteComment = (sequence) => {
        if (!window.confirm("정말로 이 댓글을 삭제하시겠습니까?")) return;

        try {
            const token = localStorage.getItem("accessToken");
            fetch(`/api/companions/${postId}/comments/${sequence}`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            })
                .then((response) => {
                    if (!response.ok) throw new Error("댓글 삭제에 실패했습니다.");
                    setPost((prev) => ({
                        ...prev,
                        comments: prev.comments.filter((c) => c.sequence !== sequence),
                    }));
                })
                .catch((error) => alert(error.message));
        } catch (error) {
            console.error("Error deleting comment:", error);
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
                            <p className="font-medium text-lg leading-tight">{post.nickname}</p>
                            <p className="text-gray-500 text-sm">{formatDate(post.createdAt)}</p>
                        </div>
                    </div>

                    {/* 작성자/일반 회원에 따른 버튼 표시 */}
                    <div className="flex gap-2">
                        {post.owner ? (
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
                                {/* 삭제 모달 */}
                                {showDeleteModal && (
                                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                                        <div className="bg-white p-6 rounded-lg shadow-xl w-96">
                                            <h3 className="text-lg font-semibold mb-4">비밀번호 확인</h3>
                                            {deleteError && (
                                                <div className="mb-4 text-red-500 text-sm">
                                                    {deleteError}
                                                </div>
                                            )}
                                            <input
                                                type="password"
                                                value={deletePassword}
                                                onChange={(e) => {
                                                    setDeletePassword(e.target.value);
                                                    setDeleteError('');
                                                }}
                                                className="w-full p-2 border rounded-lg mb-4"
                                                placeholder="비밀번호를 입력하세요"
                                                autoFocus
                                            />
                                            <div className="flex justify-end gap-2">
                                                <button
                                                    onClick={() => {
                                                        setShowDeleteModal(false);
                                                        setDeletePassword('');
                                                        setDeleteError('');
                                                    }}
                                                    className="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
                                                >
                                                    취소
                                                </button>
                                                <button
                                                    onClick={handleDeleteConfirm}
                                                    className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
                                                >
                                                    삭제
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                )}
                            </>
                        ) : (
                            post.category === 'COMPANION' && (
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
                            )
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
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                                </svg>
                                <span>{post.views}</span>
                            </span>
                            <span className="flex items-center gap-1">
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2"
                                          d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
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
                                key={comment.sequence}
                                className="flex items-start gap-4 p-4 border border-gray-200 rounded-lg hover:border-[#4D4B88] transition-colors"
                            >
                                <img
                                    src={comment.avatar || "/imgs/default-avatar.png"}
                                    alt="Avatar"
                                    className="w-10 h-10 rounded-full border border-gray-300"
                                />
                                <div className="flex-1">
                                    <div className="flex justify-between items-center">
                                        <div>
                                            <p className="font-medium">{comment.nickname}</p>
                                            <p className="text-gray-500 text-sm">{formatDate(comment.createdAt)}</p>
                                        </div>
                                        {/* 수정/삭제 버튼 */}
                                        {comment.owner && (
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => handleEditComment(comment.sequence)}
                                                    className="px-3 py-1 bg-[#4D4B88] text-white rounded-lg hover:opacity-90 text-sm"
                                                >
                                                    수정
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteComment(comment.sequence)}
                                                    className="px-3 py-1 bg-red-500 text-white rounded-lg hover:opacity-90 text-sm"
                                                >
                                                    삭제
                                                </button>
                                            </div>
                                        )}
                                    </div>
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