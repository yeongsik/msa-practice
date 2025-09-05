import { useState, useEffect } from 'react';
import Avatar from '../common/Avatar';
import Button from '../common/Button';
import { HiOutlinePhotograph, HiOutlineX } from 'react-icons/hi';
import { BiPoll } from 'react-icons/bi';
import { HiOutlineEmojiHappy } from 'react-icons/hi';

function TweetModal({ isOpen, onClose }) {
  const [tweetText, setTweetText] = useState('');
  const maxLength = 280;

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

  const handleSubmit = () => {
    if (tweetText.trim()) {
      console.log('Tweet submitted:', tweetText);
      setTweetText('');
      onClose();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div 
        className="fixed inset-0 bg-black bg-opacity-50"
        onClick={onClose}
      />
      
      <div className="relative bg-white dark:bg-gray-800 rounded-2xl shadow-xl w-full max-w-lg mx-4 max-h-[80vh] overflow-y-auto">
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors"
          >
            <HiOutlineX size={20} className="text-gray-600 dark:text-gray-400" />
          </button>
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">
            새 트윗
          </h2>
          <div className="w-9" />
        </div>

        <div className="p-4">
          <div className="flex space-x-3">
            <Avatar userId="current-user" size="sm" />
            <div className="flex-1">
              <textarea
                value={tweetText}
                onChange={(e) => setTweetText(e.target.value)}
                className="w-full resize-none text-xl placeholder-gray-500 dark:placeholder-gray-400 border-none outline-none bg-transparent text-gray-900 dark:text-white"
                placeholder="지금 어떤 일이 일어나고 있나요?"
                rows={4}
                maxLength={maxLength}
                autoFocus
              />
              
              <div className="flex justify-between items-center mt-4">
                <div className="flex space-x-3">
                  <Button variant="ghost" size="icon" className="text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20">
                    <HiOutlinePhotograph size={20} />
                  </Button>
                  <Button variant="ghost" size="icon" className="text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20">
                    <BiPoll size={20} />
                  </Button>
                  <Button variant="ghost" size="icon" className="text-blue-500 hover:bg-blue-50 dark:hover:bg-blue-900/20">
                    <HiOutlineEmojiHappy size={20} />
                  </Button>
                </div>
                
                <div className="flex items-center space-x-3">
                  <div className="flex items-center space-x-2">
                    <div className="relative w-8 h-8">
                      <svg className="w-8 h-8 transform -rotate-90" viewBox="0 0 32 32">
                        <circle
                          cx="16"
                          cy="16"
                          r="14"
                          stroke="currentColor"
                          strokeWidth="4"
                          fill="none"
                          className="text-gray-200 dark:text-gray-600"
                        />
                        <circle
                          cx="16"
                          cy="16"
                          r="14"
                          stroke="currentColor"
                          strokeWidth="4"
                          fill="none"
                          strokeDasharray={`${(tweetText.length / maxLength) * 87.96} 87.96`}
                          className={`transition-colors ${
                            tweetText.length > maxLength * 0.8
                              ? tweetText.length > maxLength
                                ? 'text-red-500'
                                : 'text-yellow-500'
                              : 'text-blue-500'
                          }`}
                        />
                      </svg>
                      <div className="absolute inset-0 flex items-center justify-center">
                        <span className={`text-xs font-medium ${
                          tweetText.length > maxLength * 0.8
                            ? tweetText.length > maxLength
                              ? 'text-red-500'
                              : 'text-yellow-500'
                            : 'text-gray-500'
                        }`}>
                          {tweetText.length > maxLength * 0.8 ? maxLength - tweetText.length : ''}
                        </span>
                      </div>
                    </div>
                  </div>
                  
                  <Button
                    variant="primary"
                    size="sm"
                    onClick={handleSubmit}
                    disabled={!tweetText.trim() || tweetText.length > maxLength}
                    className="font-bold px-6"
                  >
                    트윗하기
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TweetModal;