import { forwardRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Avatar from '../common/Avatar';
import Button from '../common/Button';
import { BiMessageRounded } from 'react-icons/bi';
import { AiOutlineRetweet } from 'react-icons/ai';
import { FiHeart, FiEye } from 'react-icons/fi';
import { AiFillHeart } from 'react-icons/ai';
import { RiShare2Line } from 'react-icons/ri';

const Tweet = forwardRef(({ 
  id, 
  userId, 
  username, 
  handle, 
  avatar,
  time, 
  content, 
  imageUrl,
  replies, 
  retweets, 
  likes, 
  views,
  isLiked: initialIsLiked = false,
  isRetweeted: initialIsRetweeted = false
}, ref) => {
  const [isLiked, setIsLiked] = useState(initialIsLiked);
  const [isRetweeted, setIsRetweeted] = useState(initialIsRetweeted);
  const [likeCount, setLikeCount] = useState(likes);
  const [retweetCount, setRetweetCount] = useState(retweets);
  
  const navigate = useNavigate();

  const handleLike = (e) => {
    e.stopPropagation();
    setIsLiked(!isLiked);
    setLikeCount(prev => isLiked ? prev - 1 : prev + 1);
  };

  const handleRetweet = (e) => {
    e.stopPropagation();
    setIsRetweeted(!isRetweeted);
    setRetweetCount(prev => isRetweeted ? prev - 1 : prev + 1);
  };

  const handleProfileClick = (e) => {
    e.stopPropagation();
    navigate(`/profile/${handle}`);
  };

  const formatNumber = (num) => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
  };

  return (
    <div ref={ref} className="p-3 sm:p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors cursor-pointer">
      <div className="flex space-x-3">
        <div onClick={handleProfileClick}>
          <Avatar src={avatar} alt={username} className="cursor-pointer hover:opacity-90 transition-opacity" />
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center space-x-2">
            <h3 
              className="font-bold text-gray-900 dark:text-gray-100 hover:underline cursor-pointer"
              onClick={handleProfileClick}
            >
              {username}
            </h3>
            <span 
              className="text-gray-500 dark:text-gray-400 hover:underline cursor-pointer"
              onClick={handleProfileClick}
            >
              @{handle}
            </span>
            <span className="text-gray-500 dark:text-gray-400">·</span>
            <span className="text-gray-500 dark:text-gray-400">{time}</span>
          </div>
          
          <p className="mt-2 text-gray-900 dark:text-gray-100 whitespace-pre-wrap">
            {content}
          </p>
          
          {imageUrl && (
            <div className="mt-3">
              <img 
                src={imageUrl} 
                alt="Tweet image" 
                className="rounded-2xl max-w-full h-auto border border-gray-200 dark:border-gray-600"
              />
            </div>
          )}
          
          <div className="flex justify-between max-w-full sm:max-w-md mt-3 text-sm">
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={(e) => e.stopPropagation()}
              className="flex items-center space-x-1 sm:space-x-2 text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full px-2 sm:px-3 py-1.5"
            >
              <BiMessageRounded size={18} />
              <span className="hidden sm:inline">{formatNumber(replies)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={handleRetweet}
              className={`flex items-center space-x-1 sm:space-x-2 rounded-full px-2 sm:px-3 py-1.5 ${
                isRetweeted 
                  ? 'text-green-500 hover:text-green-600 hover:bg-green-50 dark:hover:bg-green-900/20' 
                  : 'text-gray-500 hover:text-green-500 hover:bg-green-50 dark:hover:bg-green-900/20'
              }`}
            >
              <AiOutlineRetweet size={18} />
              <span className="hidden sm:inline">{formatNumber(retweetCount)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={handleLike}
              className={`flex items-center space-x-1 sm:space-x-2 rounded-full px-2 sm:px-3 py-1.5 ${
                isLiked 
                  ? 'text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20' 
                  : 'text-gray-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20'
              }`}
            >
              {isLiked ? <AiFillHeart size={18} /> : <FiHeart size={18} />}
              <span className="hidden sm:inline">{formatNumber(likeCount)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={(e) => e.stopPropagation()}
              className="flex items-center space-x-1 sm:space-x-2 text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full px-2 sm:px-3 py-1.5"
            >
              <FiEye size={18} />
              <span className="hidden sm:inline">{formatNumber(views)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="sm" 
              onClick={(e) => e.stopPropagation()}
              className="text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full p-1.5"
            >
              <RiShare2Line size={18} />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
});

Tweet.displayName = 'Tweet';

export default Tweet;