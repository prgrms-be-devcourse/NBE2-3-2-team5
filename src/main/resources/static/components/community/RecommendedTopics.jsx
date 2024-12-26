import React from 'react';

const RecommendedTopics = () => {
    const topics = ['Programming', 'Copywriting', 'Product design', 'Machine learning', 'Productivity'];

    return (
        <div className="bg-white rounded-lg p-6 shadow-lg">
            <h2 className="text-xl font-bold mb-4">Recommended topics</h2>
            <div className="flex flex-wrap gap-2">
                {topics.map((topic) => (
                    <span
                        key={topic}
                        className="px-4 py-2 bg-gray-100 rounded-full text-gray-700 text-sm hover:bg-gray-200 transition"
                    >
                        {topic}
                    </span>
                ))}
            </div>
            <button className="text-[#FF6B6B] mt-4 font-semibold hover:underline">
                See more topics
            </button>
        </div>
    );
};

export default RecommendedTopics;
