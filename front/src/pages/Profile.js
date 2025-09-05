import { useParams } from 'react-router-dom';
import Avatar from '../components/common/Avatar';
import Button from '../components/common/Button';
import TweetList from '../components/tweet/TweetList';
import { getUserByUsername } from '../utils/mockData';

function Profile() {
  const { username } = useParams();
  const user = getUserByUsername(username);

  return (
    <div>
      <div className="p-4 border-b border-gray-200 dark:border-gray-700">
        <div className="flex flex-col sm:flex-row sm:items-center space-y-4 sm:space-y-0 sm:space-x-4 mb-6">
          <Avatar src={user.avatar} alt="Profile" size="lg" />
          <div className="flex-1">
            <h1 className="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white">{user.name}</h1>
            <p className="text-gray-600 dark:text-gray-400">@{username || 'user'}</p>
            <p className="text-gray-600 dark:text-gray-400 mt-1">Software Developer</p>
            <div className="flex space-x-4 mt-2 text-sm text-gray-500">
              <span><strong>123</strong> Following</span>
              <span><strong>456</strong> Followers</span>
            </div>
          </div>
          <div className="sm:ml-auto">
            <Button variant="outline" size="sm" className="w-full sm:w-auto">팔로우</Button>
          </div>
        </div>
        
        <div className="border-b border-gray-200 dark:border-gray-700">
          <nav className="flex space-x-4 sm:space-x-8 overflow-x-auto">
            <button className="py-3 px-1 border-b-2 border-blue-500 text-blue-500 font-medium text-sm whitespace-nowrap">
              트윗
            </button>
            <button className="py-3 px-1 border-b-2 border-transparent text-gray-500 hover:text-gray-700 dark:hover:text-gray-300 font-medium text-sm whitespace-nowrap">
              답글
            </button>
            <button className="py-3 px-1 border-b-2 border-transparent text-gray-500 hover:text-gray-700 dark:hover:text-gray-300 font-medium text-sm whitespace-nowrap">
              미디어
            </button>
            <button className="py-3 px-1 border-b-2 border-transparent text-gray-500 hover:text-gray-700 dark:hover:text-gray-300 font-medium text-sm whitespace-nowrap">
              좋아요
            </button>
          </nav>
        </div>
      </div>
      
      <TweetList userId={username} />
    </div>
  );
}

export default Profile;