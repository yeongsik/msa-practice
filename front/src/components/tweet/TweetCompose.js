import Avatar from '../common/Avatar';
import Button from '../common/Button';
import { HiOutlinePhotograph } from 'react-icons/hi';
import { BiPoll } from 'react-icons/bi';
import { HiOutlineEmojiHappy } from 'react-icons/hi';

function TweetCompose() {
  return (
    <div className="border-b border-gray-200 dark:border-gray-700 p-4">
      <div className="flex space-x-4">
        <Avatar userId="current-user" />
        <div className="flex-1">
          <textarea 
            className="w-full resize-none text-xl placeholder-gray-500 dark:placeholder-gray-400 border-none outline-none bg-transparent text-gray-900 dark:text-gray-100"
            placeholder="지금 어떤 일이 일어나고 있나요?"
            rows={3}
          />
          <div className="flex justify-between items-center mt-4">
            <div className="flex space-x-4">
              <Button variant="ghost" size="icon" className="text-blue-500">
                <HiOutlinePhotograph size={20} />
              </Button>
              <Button variant="ghost" size="icon" className="text-blue-500">
                <BiPoll size={20} />
              </Button>
              <Button variant="ghost" size="icon" className="text-blue-500">
                <HiOutlineEmojiHappy size={20} />
              </Button>
            </div>
            <Button variant="primary" size="lg" className="font-bold">
              트윗하기
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default TweetCompose;