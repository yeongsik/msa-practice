import { useState } from 'react';
import Avatar from '../components/common/Avatar';
import { HiOutlinePaperAirplane } from 'react-icons/hi';

function Messages() {
  const [selectedChat, setSelectedChat] = useState(null);
  const [newMessage, setNewMessage] = useState('');

  const conversations = [
    {
      id: 1,
      user: { name: '김철수', username: 'kimcs', avatar: 'https://picsum.photos/40/40?random=1' },
      lastMessage: '안녕하세요! 잘 지내고 계신가요?',
      time: '오후 2:30',
      unread: true,
      messages: [
        { id: 1, text: '안녕하세요!', sender: 'other', time: '오후 2:28' },
        { id: 2, text: '안녕하세요! 잘 지내고 계신가요?', sender: 'other', time: '오후 2:30' }
      ]
    },
    {
      id: 2,
      user: { name: '박영희', username: 'parkyh', avatar: 'https://picsum.photos/40/40?random=2' },
      lastMessage: '프로젝트 진행은 어떻게 되고 있나요?',
      time: '오전 11:45',
      unread: false,
      messages: [
        { id: 1, text: '프로젝트 진행은 어떻게 되고 있나요?', sender: 'other', time: '오전 11:45' },
        { id: 2, text: '순조롭게 진행되고 있습니다!', sender: 'me', time: '오전 11:47' }
      ]
    },
    {
      id: 3,
      user: { name: '이민수', username: 'leems', avatar: 'https://picsum.photos/40/40?random=3' },
      lastMessage: '내일 미팅 시간 괜찮으신가요?',
      time: '어제',
      unread: true,
      messages: [
        { id: 1, text: '내일 미팅 시간 괜찮으신가요?', sender: 'other', time: '어제 오후 5:20' }
      ]
    }
  ];

  const handleSendMessage = () => {
    if (newMessage.trim() && selectedChat) {
      setNewMessage('');
    }
  };

  return (
    <div className="h-screen flex">
      <div className="w-1/3 border-r border-gray-200 dark:border-gray-700 flex flex-col">
        <div className="p-4 border-b border-gray-200 dark:border-gray-700">
          <h1 className="text-xl font-bold text-gray-900 dark:text-white">메시지</h1>
        </div>
        
        <div className="flex-1 overflow-y-auto divide-y divide-gray-200 dark:divide-gray-700">
          {conversations.map((conversation) => (
            <div
              key={conversation.id}
              onClick={() => setSelectedChat(conversation)}
              className={`p-4 hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer transition-colors ${
                selectedChat?.id === conversation.id ? 'bg-blue-50 dark:bg-blue-900/20' : ''
              }`}
            >
              <div className="flex items-center space-x-3">
                <Avatar 
                  src={conversation.user.avatar} 
                  alt={conversation.user.name}
                  size="sm"
                />
                <div className="flex-1 min-w-0">
                  <div className="flex justify-between items-center">
                    <p className="text-sm font-medium text-gray-900 dark:text-white truncate">
                      {conversation.user.name}
                    </p>
                    <p className="text-xs text-gray-500">{conversation.time}</p>
                  </div>
                  <div className="flex justify-between items-center">
                    <p className="text-sm text-gray-600 dark:text-gray-400 truncate">
                      {conversation.lastMessage}
                    </p>
                    {conversation.unread && (
                      <div className="w-2 h-2 bg-blue-500 rounded-full"></div>
                    )}
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="flex-1 flex flex-col">
        {selectedChat ? (
          <>
            <div className="p-4 border-b border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
              <div className="flex items-center space-x-3">
                <Avatar 
                  src={selectedChat.user.avatar} 
                  alt={selectedChat.user.name}
                  size="sm"
                />
                <div>
                  <p className="font-medium text-gray-900 dark:text-white">
                    {selectedChat.user.name}
                  </p>
                  <p className="text-sm text-gray-500">@{selectedChat.user.username}</p>
                </div>
              </div>
            </div>

            <div className="flex-1 overflow-y-auto p-4 space-y-4">
              {selectedChat.messages.map((message) => (
                <div
                  key={message.id}
                  className={`flex ${message.sender === 'me' ? 'justify-end' : 'justify-start'}`}
                >
                  <div className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${
                    message.sender === 'me'
                      ? 'bg-blue-500 text-white'
                      : 'bg-gray-100 dark:bg-gray-700 text-gray-900 dark:text-white'
                  }`}>
                    <p className="text-sm">{message.text}</p>
                    <p className={`text-xs mt-1 ${
                      message.sender === 'me' ? 'text-blue-100' : 'text-gray-500'
                    }`}>
                      {message.time}
                    </p>
                  </div>
                </div>
              ))}
            </div>

            <div className="p-4 border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800">
              <div className="flex space-x-2">
                <input
                  type="text"
                  placeholder="메시지를 입력하세요..."
                  value={newMessage}
                  onChange={(e) => setNewMessage(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                  className="flex-1 px-4 py-2 border border-gray-200 dark:border-gray-600 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                />
                <button
                  onClick={handleSendMessage}
                  className="px-4 py-2 bg-blue-500 text-white rounded-full hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
                >
                  <HiOutlinePaperAirplane size={16} />
                </button>
              </div>
            </div>
          </>
        ) : (
          <div className="flex-1 flex items-center justify-center bg-gray-50 dark:bg-gray-800">
            <div className="text-center">
              <p className="text-xl font-medium text-gray-900 dark:text-white mb-2">
                메시지를 선택하세요
              </p>
              <p className="text-gray-500 dark:text-gray-400">
                대화를 시작하려면 왼쪽에서 메시지를 선택하세요.
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Messages;