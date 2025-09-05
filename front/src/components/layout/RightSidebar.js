import { useNavigate } from 'react-router-dom';
import Avatar from '../common/Avatar';
import Button from '../common/Button';

function RightSidebar() {
  const navigate = useNavigate();
  const trends = ['React', 'JavaScript', 'MSA', 'Spring Boot', 'Tailwind'];
  const suggestedUsers = [
    { name: '개발자A', username: 'devA', avatar: 'https://picsum.photos/40/40?random=11' },
    { name: '개발자B', username: 'devB', avatar: 'https://picsum.photos/40/40?random=12' },
    { name: '개발자C', username: 'devC', avatar: 'https://picsum.photos/40/40?random=13' }
  ];

  const handleUserClick = (username) => {
    navigate(`/profile/${username}`);
  };

  const handleFollowClick = (e, username) => {
    e.stopPropagation();
    console.log(`Following ${username}`);
  };

  return (
    <aside className="w-80 p-4 hidden lg:block dark:text-white">
      <div className="bg-gray-50 dark:bg-gray-800 rounded-xl p-4 mb-4">
        <h2 className="text-xl font-bold mb-4 text-gray-900 dark:text-gray-100">트렌드</h2>
        <div className="space-y-3">
          {trends.map((trend, index) => (
            <div key={trend} className="hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded cursor-pointer transition-colors">
              <p className="text-sm text-gray-500 dark:text-gray-400">#{index + 1} 트렌딩</p>
              <p className="font-bold text-gray-900 dark:text-gray-100">{trend}</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">{(index + 1) * 1000}개의 트윗</p>
            </div>
          ))}
        </div>
      </div>
      
      <div className="bg-gray-50 dark:bg-gray-800 rounded-xl p-4">
        <h2 className="text-xl font-bold mb-4 text-gray-900 dark:text-gray-100">팔로우 추천</h2>
        <div className="space-y-3">
          {suggestedUsers.map((user) => (
            <div 
              key={user.username} 
              className="flex items-center justify-between hover:bg-gray-100 dark:hover:bg-gray-700 p-2 rounded-lg cursor-pointer transition-colors"
              onClick={() => handleUserClick(user.username)}
            >
              <div className="flex items-center space-x-3">
                <Avatar src={user.avatar} alt={user.name} size="sm" />
                <div>
                  <p className="font-bold text-gray-900 dark:text-gray-100 hover:underline">{user.name}</p>
                  <p className="text-sm text-gray-500 dark:text-gray-400 hover:underline">@{user.username}</p>
                </div>
              </div>
              <Button 
                variant="black" 
                size="sm"
                onClick={(e) => handleFollowClick(e, user.username)}
                className="hover:bg-gray-800 dark:hover:bg-gray-600"
              >
                팔로우
              </Button>
            </div>
          ))}
        </div>
      </div>
    </aside>
  );
}

export default RightSidebar;