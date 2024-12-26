import React, { useState, useEffect } from 'react';
import DiscussionList from './DiscussionList';
import TopDiscussions from './TopDiscussions';
import RecommendedTopics from './RecommendedTopics';
import PeopleToFollow from './PeopleToFollow';

const CommunityPage = () => {
    const [posts, setPosts] = useState([
        {
            id: 1,
            author: 'Mikey Jonah',
            avatar: '/assets/images/avatar.png',
            time: '2d ago',
            title: 'Title of the discussion will be placed here',
            content: 'That ipo will be a game-changer land it in region keep it lean this proposal is a win-win situation which will cause a stellar paradigm shift and produce a multi-fold increase in deliverables',
            tags: ['study-group', 'share-insight', 'help-question'],
            replies: 28,
            views: 875
        },
    ]);

    useEffect(() => {  // useEffect import 필요
        fetch('/api/companions')
            .then(response => response.json())
            .then(data => setPosts(data))
            .catch(error => console.error('Error:', error));
    }, []);

    return (
        <div className="container mx-auto px-4 py-8 flex gap-8">
            {/* 왼쪽 메인 컨텐츠 */}
            <div className="flex-1">
                <DiscussionList posts={posts} />
            </div>

            {/* 오른쪽 사이드바 */}
            <div className="w-96">
                <TopDiscussions />
                <RecommendedTopics />
                <PeopleToFollow />
            </div>
        </div>
    );
};

export default CommunityPage;