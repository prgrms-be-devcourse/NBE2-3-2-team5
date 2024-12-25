import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";

const PostWrite = () => {
    const navigate = useNavigate();
    const { postId } = useParams(); // URL에서 postId를 가져옴
    const isEditMode = !!postId; // postId가 있으면 수정 모드

    const [formData, setFormData] = useState({
        title: "",
        writer: "",
        mail: "",
        password: "",
        content: "",
        category: "GENERAL",
        tags: "",
    });
    const [image, setImage] = useState(null);

    // 수정 모드일 때 기존 데이터 불러오기
    useEffect(() => {
        if (isEditMode) {
            fetch(`/api/companions/${postId}`)
                .then(response => response.json())
                .then(data => {
                    setFormData({
                        title: data.title || "",
                        writer: data.writer || "",
                        mail: data.mail || "",
                        password: "", // 비밀번호는 빈 값으로 초기화
                        content: data.content || "",
                        category: data.category || "GENERAL",
                        tags: (data.tags || []).join(","),
                    });
                })
                .catch(error => console.error("Error:", error));
        }
    }, [postId, isEditMode]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({ ...formData, [name]: value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const formDataToSend = new FormData();
        formDataToSend.append("title", formData.title);
        formDataToSend.append("writer", formData.writer);
        formDataToSend.append("mail", formData.mail);
        formDataToSend.append("password", formData.password);
        formDataToSend.append("content", formData.content);
        formDataToSend.append("category", formData.category);
        formDataToSend.append("tags", formData.tags);
        if (image) {
            formDataToSend.append("image", image);
        }

        // 수정 모드와 등록 모드에 따라 다른 API 호출
        const url = isEditMode ? `/api/companions/${postId}` : "/api/companions";
        const method = isEditMode ? "PUT" : "POST";

        fetch(url, {
            method: method,
            body: isEditMode ? JSON.stringify(formData) : formDataToSend,
            headers: isEditMode ? {
                'Content-Type': 'application/json',
            } : undefined,
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(isEditMode ? "Failed to update post" : "Failed to create post");
                }
                return response.json();
            })
            .then(() => {
                alert(isEditMode ? "게시글이 수정되었습니다." : "게시글이 등록되었습니다.");
                navigate("/community");
            })
            .catch((error) => {
                console.error("Error:", error);
                alert("오류가 발생했습니다.");
            });
    };

    return (
        <div className="max-w-4xl mx-auto p-6 bg-white rounded-lg shadow-lg">
            <h1 className="text-2xl font-bold mb-4">
                {isEditMode ? "Edit Post" : "Write a New Post"}
            </h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Title</label>
                    <input
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Writer</label>
                    <input
                        type="text"
                        name="writer"
                        value={formData.writer}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        required
                        disabled={isEditMode} // 수정 모드에서는 작성자 변경 불가
                    />
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Email</label>
                    <input
                        type="email"
                        name="mail"
                        value={formData.mail}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        required
                        disabled={isEditMode} // 수정 모드에서는 이메일 변경 불가
                    />
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Password</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Content</label>
                    <textarea
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        rows="5"
                        required
                    ></textarea>
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Category</label>
                    <select
                        name="category"
                        value={formData.category}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                    >
                        <option value="GENERAL">동행자 모집</option>
                        <option value="QUESTION">후기</option>
                        <option value="DISCUSSION">Q&A</option>
                        <option value="DISCUSSION">기타</option>
                    </select>
                </div>
                <div className="mb-4">
                    <label className="block font-semibold mb-2">Tags</label>
                    <input
                        type="text"
                        name="tags"
                        value={formData.tags}
                        onChange={handleChange}
                        className="w-full p-2 border rounded-lg"
                        placeholder="Comma-separated tags"
                    />
                </div>
                {!isEditMode && ( // 수정 모드에서는 이미지 업로드 숨김
                    <div className="mb-4">
                        <label className="block font-semibold mb-2">Upload Image</label>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setImage(e.target.files[0])}
                            className="w-full p-2 border rounded-lg"
                        />
                    </div>
                )}
                <div className="text-right">
                    <button
                        type="button"
                        onClick={() => navigate("/community")}
                        className="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 mr-2"
                    >
                        Cancel
                    </button>
                    <button
                        type="submit"
                        className="px-4 py-2 bg-[#4D4B88] text-white rounded-lg hover:opacity-90"
                    >
                        {isEditMode ? "Update" : "Submit"}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default PostWrite;