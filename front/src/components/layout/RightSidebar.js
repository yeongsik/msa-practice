import Avatar from '../common/Avatar';
import Button from '../common/Button';

function RightSidebar() {
  const trends = ['React', 'JavaScript', 'MSA', 'Spring Boot', 'Tailwind'];
  const suggestedUsers = ['개발자A', '개발자B', '개발자C'];

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
          {suggestedUsers.map((user, index) => (
            <div key={user} className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <Avatar size="sm" userId={`suggested-${user.toLowerCase()}`} />
                <div>
                  <p className="font-bold text-gray-900 dark:text-gray-100">{user}</p>
                  <p className="text-sm text-gray-500 dark:text-gray-400">@{user.toLowerCase()}</p>
                </div>
              </div>
              <Button variant="black" size="sm">
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