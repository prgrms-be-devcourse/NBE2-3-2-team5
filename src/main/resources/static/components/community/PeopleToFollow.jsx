import React from 'react';

const PeopleToFollow = () => {
    const people = [
        // 더미 데이터
        {
            id: 1,
            name: 'Mikey Jonah',
            description: 'Talks about design & productivity',
            avatar: '/assets/images/avatar.png',
        },
        {
            id: 2,
            name: 'Sarah Connor',
            description: 'Machine learning enthusiast',
            avatar: '/assets/images/avatar2.png',
        },
        {
            id: 3,
            name: 'John Doe',
            description: 'Creative Copywriter',
            avatar: '/assets/images/avatar3.png',
        },
    ];

    return (
        <div className="bg-white rounded-lg p-6 shadow-lg">
            <h2 className="text-xl font-bold mb-4">People to follow</h2>
            <div className="space-y-4">
                {people.map((person) => (
                    <div
                        key={person.id}
                        className="flex items-center justify-between border border-gray-200 rounded-lg p-4 shadow-sm hover:shadow-md transition-shadow"
                    >
                        <div className="flex items-center gap-4">
                            <img
                                src={person.avatar}
                                alt=""
                                className="w-10 h-10 rounded-full border border-gray-300"
                            />
                            <div>
                                <p className="font-medium text-gray-800">{person.name}</p>
                                <p className="text-gray-500 text-sm">{person.description}</p>
                            </div>
                        </div>
                        <button className="px-4 py-1 border border-gray-300 rounded-full hover:bg-gray-50">
                            Follow
                        </button>
                    </div>
                ))}
            </div>
            <button className="text-[#FF6B6B] mt-4 hover:underline">
                See more suggestions
            </button>
        </div>
    );
};

export default PeopleToFollow;
