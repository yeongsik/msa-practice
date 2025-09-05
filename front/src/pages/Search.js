import { useState } from 'react';
import { FiSearch } from 'react-icons/fi';

function Search() {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <div className="p-4">
      <div className="mb-6">
        <div className="relative">
          <FiSearch className="absolute left-3 top-3 text-gray-400" />
          <input
            type="text"
            placeholder="검색어를 입력하세요"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 text-gray-900 dark:text-white"
          />
        </div>
      </div>
      
      {searchQuery ? (
        <div>
          <h2 className="text-lg font-semibold mb-4 text-gray-900 dark:text-white">
            "{searchQuery}" 검색 결과
          </h2>
          <div className="text-center text-gray-500 dark:text-gray-400 py-8">
            검색 결과가 없습니다.
          </div>
        </div>
      ) : (
        <div>
          <h2 className="text-lg font-semibold mb-4 text-gray-900 dark:text-white">트렌드</h2>
          <div className="space-y-3">
            <div className="p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded cursor-pointer">
              <p className="text-sm text-gray-500">#trending</p>
              <p className="font-medium text-gray-900 dark:text-white">React Router</p>
              <p className="text-sm text-gray-500">1,234 tweets</p>
            </div>
            <div className="p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded cursor-pointer">
              <p className="text-sm text-gray-500">#tech</p>
              <p className="font-medium text-gray-900 dark:text-white">JavaScript</p>
              <p className="text-sm text-gray-500">2,345 tweets</p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Search;