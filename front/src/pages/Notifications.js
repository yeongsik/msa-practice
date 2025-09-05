import Avatar from '../components/common/Avatar';
import { AiOutlineHeart, AiOutlineRetweet, AiOutlineUserAdd } from 'react-icons/ai';

function Notifications() {
  const notifications = [
    {
      id: 1,
      type: 'like',
      user: { name: '김철수', username: 'kimcs', avatar: 'https://picsum.photos/40/40?random=1' },
      message: '님이 회원님의 게시물을 좋아합니다.',
      time: '2시간 전',
      post: '오늘 날씨가 정말 좋네요! 🌞'
    },
    {
      id: 2,
      type: 'retweet',
      user: { name: '박영희', username: 'parkyh', avatar: 'https://picsum.photos/40/40?random=2' },
      message: '님이 회원님의 게시물을 리트윗했습니다.',
      time: '4시간 전',
      post: 'React Router 설정 완료!'
    },
    {
      id: 3,
      type: 'follow',
      user: { name: '이민수', username: 'leems', avatar: 'https://picsum.photos/40/40?random=3' },
      message: '님이 회원님을 팔로우하기 시작했습니다.',
      time: '1일 전'
    },
    {
      id: 4,
      type: 'like',
      user: { name: '최수진', username: 'choisj', avatar: 'https://picsum.photos/40/40?random=4' },
      message: '님이 회원님의 게시물을 좋아합니다.',
      time: '2일 전',
      post: '새로운 프로젝트 시작!'
    }
  ];

  const getIcon = (type) => {
    switch (type) {
      case 'like': return <AiOutlineHeart className="text-red-500" size={20} />;
      case 'retweet': return <AiOutlineRetweet className="text-green-500" size={20} />;
      case 'follow': return <AiOutlineUserAdd className="text-blue-500" size={20} />;
      default: return null;
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-xl font-bold text-gray-900 dark:text-white mb-6 border-b border-gray-200 dark:border-gray-700 pb-3">
        알림
      </h1>
      
      <div className="space-y-4">
        {notifications.map((notification) => (
          <div 
            key={notification.id}
            className="flex items-start space-x-3 p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded-lg cursor-pointer transition-colors"
          >
            <div className="flex-shrink-0">
              {getIcon(notification.type)}
            </div>
            <Avatar 
              src={notification.user.avatar} 
              alt={notification.user.name}
              size="sm"
            />
            <div className="flex-1 min-w-0">
              <p className="text-sm text-gray-900 dark:text-white">
                <span className="font-semibold">{notification.user.name}</span>
                {notification.message}
              </p>
              {notification.post && (
                <p className="text-sm text-gray-600 dark:text-gray-400 mt-1 truncate">
                  "{notification.post}"
                </p>
              )}
              <p className="text-xs text-gray-500 mt-1">{notification.time}</p>
            </div>
          </div>
        ))}
      </div>
      
      {notifications.length === 0 && (
        <div className="text-center py-12">
          <p className="text-gray-500 dark:text-gray-400">새로운 알림이 없습니다.</p>
        </div>
      )}
    </div>
  );
}

export default Notifications;