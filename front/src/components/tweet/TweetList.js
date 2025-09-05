import Tweet from './Tweet';

function TweetList() {
  const mockTweets = [
    {
      id: 1,
      userId: 'user1',
      username: '사용자 1',
      handle: 'user1',
      time: 1,
      content: '이것은 샘플 트윗입니다. 실제로는 백엔드에서 데이터를 가져와야 합니다.',
      replies: 3,
      retweets: 2,
      likes: 5
    },
    {
      id: 2,
      userId: 'user2',
      username: '사용자 2',
      handle: 'user2',
      time: 2,
      content: 'MSA 구조로 X 클론을 만들고 있습니다. React + Spring Boot 조합이네요!',
      replies: 6,
      retweets: 4,
      likes: 10
    },
    {
      id: 3,
      userId: 'user3',
      username: '사용자 3',
      handle: 'user3',
      time: 3,
      content: 'Tailwind CSS로 스타일링하니까 정말 편하네요. 컴포넌트도 깔끔하게 분리되고!',
      replies: 9,
      retweets: 6,
      likes: 15
    },
    {
      id: 4,
      userId: 'user4',
      username: '사용자 4',
      handle: 'user4',
      time: 4,
      content: '프론트엔드와 백엔드를 분리해서 개발하니까 협업하기 좋을 것 같아요.',
      replies: 12,
      retweets: 8,
      likes: 20
    },
    {
      id: 5,
      userId: 'user5',
      username: '사용자 5',
      handle: 'user5',
      time: 5,
      content: '다음에는 실시간 알림 기능도 추가해보고 싶네요. WebSocket 사용해서!',
      replies: 15,
      retweets: 10,
      likes: 25
    }
  ];

  return (
    <div className="divide-y divide-gray-200 dark:divide-gray-700">
      {mockTweets.map((tweet) => (
        <Tweet
          key={tweet.id}
          id={tweet.id}
          userId={tweet.userId}
          username={tweet.username}
          handle={tweet.handle}
          time={tweet.time}
          content={tweet.content}
          replies={tweet.replies}
          retweets={tweet.retweets}
          likes={tweet.likes}
        />
      ))}
    </div>
  );
}

export default TweetList;