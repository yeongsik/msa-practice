import { useState, useCallback, useEffect } from 'react';
import Tweet from './Tweet';
import TweetDetailModal from './TweetDetailModal';
import useInfiniteScroll from '../../hooks/useInfiniteScroll';
import { generateMockTweets, generateUserSpecificTweets, simulateApiDelay } from '../../utils/mockData';

function TweetList({ userId = null }) {
  const [tweets, setTweets] = useState([]);
  const [page, setPage] = useState(1);
  const [selectedTweet, setSelectedTweet] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const TWEETS_PER_PAGE = 10;

  const fetchMoreTweets = useCallback(async () => {
    await simulateApiDelay(800);
    
    const newTweets = userId 
      ? generateUserSpecificTweets(userId, TWEETS_PER_PAGE)
      : generateMockTweets(TWEETS_PER_PAGE);
    const hasMore = page < 10; // 최대 10페이지까지만 로드
    
    setTweets(prev => [...prev, ...newTweets]);
    setPage(prev => prev + 1);
    
    return { hasMore };
  }, [page, userId]);

  const { isFetching, hasMore, lastTweetElementRef } = useInfiniteScroll(fetchMoreTweets);

  useEffect(() => {
    const loadInitialTweets = async () => {
      const initialTweets = userId 
        ? generateUserSpecificTweets(userId, TWEETS_PER_PAGE)
        : generateMockTweets(TWEETS_PER_PAGE);
      setTweets(initialTweets);
      setPage(2);
    };
    
    loadInitialTweets();
  }, [userId]);

  const handleTweetClick = (tweetData) => {
    setSelectedTweet(tweetData);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedTweet(null);
  };

  return (
    <div className="divide-y divide-gray-200 dark:divide-gray-700">
      {tweets.map((tweet, index) => (
        <Tweet
          key={tweet.id}
          ref={tweets.length === index + 1 ? lastTweetElementRef : null}
          id={tweet.id}
          userId={tweet.user.username}
          username={tweet.user.name}
          handle={tweet.user.username}
          avatar={tweet.user.avatar}
          time={tweet.time}
          content={tweet.text}
          imageUrl={tweet.imageUrl}
          images={tweet.images}
          replies={tweet.replies}
          retweets={tweet.retweets}
          likes={tweet.likes}
          views={tweet.views}
          isLiked={tweet.isLiked}
          isRetweeted={tweet.isRetweeted}
          onTweetClick={handleTweetClick}
        />
      ))}
      
      {isFetching && (
        <div className="flex justify-center py-8">
          <div className="flex items-center space-x-3">
            <div className="animate-spin rounded-full h-6 w-6 border-2 border-blue-500 border-t-transparent"></div>
            <span className="text-gray-600 dark:text-gray-400">트윗을 불러오는 중...</span>
          </div>
        </div>
      )}
      
      {!hasMore && tweets.length > 0 && (
        <div className="text-center py-8 text-gray-500 dark:text-gray-400">
          더 이상 불러올 트윗이 없습니다.
        </div>
      )}
      
      {/* 트윗 상세 모달 */}
      <TweetDetailModal 
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        tweet={selectedTweet}
      />
    </div>
  );
}

export default TweetList;