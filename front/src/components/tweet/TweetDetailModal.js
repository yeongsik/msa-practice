import { useEffect } from 'react';
import { HiOutlineX } from 'react-icons/hi';
import { BiMessageRounded } from 'react-icons/bi';
import { AiOutlineRetweet, AiFillHeart } from 'react-icons/ai';
import { FiHeart, FiEye } from 'react-icons/fi';
import { RiShare2Line } from 'react-icons/ri';
import Avatar from '../common/Avatar';
import Button from '../common/Button';
import ImageGallery from './ImageGallery';

function TweetDetailModal({ isOpen, onClose, tweet }) {
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === 'Escape') {
        onClose();
      }
    };
    
    if (isOpen) {
      document.addEventListener('keydown', handleEsc);
      document.body.style.overflow = 'hidden';
    }
    
    return () => {
      document.removeEventListener('keydown', handleEsc);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  const formatNumber = (num) => {
    if (num >= 1000000) {
      return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
      return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
  };

  const formatTime = (timeString) => {
    const date = new Date(timeString);
    const now = new Date();
    const diffInMinutes = Math.floor((now - date) / (1000 * 60));
    
    if (diffInMinutes < 60) {
      return `${diffInMinutes}분`;
    } else if (diffInMinutes < 1440) {
      return `${Math.floor(diffInMinutes / 60)}시간`;
    } else {
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  };

  if (!isOpen || !tweet) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center pt-8 sm:pt-16">
      <div 
        className="fixed inset-0 bg-black bg-opacity-50"
        onClick={onClose}
      />
      
      <div className="relative bg-white dark:bg-gray-800 rounded-2xl shadow-xl w-full max-w-2xl mx-4 max-h-[85vh] overflow-y-auto">
        {/* 헤더 */}
        <div className="sticky top-0 flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 rounded-t-2xl">
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors"
          >
            <HiOutlineX size={20} className="text-gray-600 dark:text-gray-400" />
          </button>
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
            트윗
          </h2>
          <div className="w-9" />
        </div>

        {/* 트윗 상세 내용 */}
        <div className="p-4 sm:p-6">
          {/* 사용자 정보 */}
          <div className="flex items-center space-x-3 mb-4">
            <Avatar src={tweet.avatar} alt={tweet.username} size="md" />
            <div>
              <div className="flex items-center space-x-2">
                <h3 className="font-bold text-gray-900 dark:text-gray-100">
                  {tweet.username}
                </h3>
                <span className="text-gray-500 dark:text-gray-400">
                  @{tweet.handle}
                </span>
              </div>
            </div>
          </div>

          {/* 트윗 내용 */}
          <div className="mb-4">
            <p className="text-xl text-gray-900 dark:text-gray-100 whitespace-pre-wrap leading-relaxed">
              {tweet.content}
            </p>
          </div>

          {/* 이미지 갤러리 */}
          {tweet.images && tweet.images.length > 0 && (
            <div className="mb-4">
              <ImageGallery images={tweet.images} />
            </div>
          )}

          {/* 시간 정보 */}
          <div className="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
            <span className="text-gray-500 dark:text-gray-400">
              {formatTime(tweet.time)}
            </span>
          </div>

          {/* 상호작용 통계 */}
          <div className="mb-4 pb-4 border-b border-gray-200 dark:border-gray-700">
            <div className="flex space-x-6">
              {tweet.retweets > 0 && (
                <div className="flex items-center space-x-1">
                  <span className="font-bold text-gray-900 dark:text-gray-100">
                    {formatNumber(tweet.retweets)}
                  </span>
                  <span className="text-gray-500 dark:text-gray-400">
                    리트윗
                  </span>
                </div>
              )}
              {tweet.likes > 0 && (
                <div className="flex items-center space-x-1">
                  <span className="font-bold text-gray-900 dark:text-gray-100">
                    {formatNumber(tweet.likes)}
                  </span>
                  <span className="text-gray-500 dark:text-gray-400">
                    마음에 들어요
                  </span>
                </div>
              )}
              {tweet.views > 0 && (
                <div className="flex items-center space-x-1">
                  <span className="font-bold text-gray-900 dark:text-gray-100">
                    {formatNumber(tweet.views)}
                  </span>
                  <span className="text-gray-500 dark:text-gray-400">
                    조회
                  </span>
                </div>
              )}
            </div>
          </div>

          {/* 액션 버튼 */}
          <div className="flex justify-around py-2">
            <Button 
              variant="ghost" 
              size="md" 
              className="flex items-center space-x-2 text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full px-4 py-2"
            >
              <BiMessageRounded size={20} />
              <span>{formatNumber(tweet.replies)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="md" 
              className={`flex items-center space-x-2 rounded-full px-4 py-2 ${
                tweet.isRetweeted 
                  ? 'text-green-500 hover:text-green-600 hover:bg-green-50 dark:hover:bg-green-900/20' 
                  : 'text-gray-500 hover:text-green-500 hover:bg-green-50 dark:hover:bg-green-900/20'
              }`}
            >
              <AiOutlineRetweet size={20} />
              <span>{formatNumber(tweet.retweets)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="md" 
              className={`flex items-center space-x-2 rounded-full px-4 py-2 ${
                tweet.isLiked 
                  ? 'text-red-500 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20' 
                  : 'text-gray-500 hover:text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20'
              }`}
            >
              {tweet.isLiked ? <AiFillHeart size={20} /> : <FiHeart size={20} />}
              <span>{formatNumber(tweet.likes)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="md" 
              className="flex items-center space-x-2 text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full px-4 py-2"
            >
              <FiEye size={20} />
              <span>{formatNumber(tweet.views)}</span>
            </Button>
            
            <Button 
              variant="ghost" 
              size="md" 
              className="text-gray-500 hover:text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20 rounded-full p-2"
            >
              <RiShare2Line size={20} />
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TweetDetailModal;