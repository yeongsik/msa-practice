import Avatar from '../common/Avatar';
import Button from '../common/Button';
import { BiMessageRounded } from 'react-icons/bi';
import { AiOutlineRetweet } from 'react-icons/ai';
import { FiHeart } from 'react-icons/fi';
import { RiShare2Line } from 'react-icons/ri';

function Tweet({ id, userId, username, handle, time, content, replies, retweets, likes }) {
  return (
    <div className="p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
      <div className="flex space-x-4">
        <Avatar userId={userId} />
        <div className="flex-1">
          <div className="flex items-center space-x-2">
            <h3 className="font-bold text-gray-900 dark:text-gray-100">{username}</h3>
            <span className="text-gray-500 dark:text-gray-400">@{handle}</span>
            <span className="text-gray-500 dark:text-gray-400">·</span>
            <span className="text-gray-500 dark:text-gray-400">{time}시간</span>
          </div>
          <p className="mt-2 text-gray-900 dark:text-gray-100">
            {content}
          </p>
          <div className="flex justify-between max-w-md mt-4">
            <Button variant="ghost" size="icon" className="flex items-center space-x-2 text-gray-500 hover:text-blue-500">
              <BiMessageRounded size={18} />
              <span>{replies}</span>
            </Button>
            <Button variant="ghost" size="icon" className="flex items-center space-x-2 text-gray-500 hover:text-green-500">
              <AiOutlineRetweet size={18} />
              <span>{retweets}</span>
            </Button>
            <Button variant="ghost" size="icon" className="flex items-center space-x-2 text-gray-500 hover:text-red-500">
              <FiHeart size={18} />
              <span>{likes}</span>
            </Button>
            <Button variant="ghost" size="icon" className="text-gray-500 hover:text-blue-500">
              <RiShare2Line size={18} />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Tweet;