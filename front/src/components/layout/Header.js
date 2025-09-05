import { Link } from 'react-router-dom';
import Button from '../common/Button';
import ThemeToggle from '../common/ThemeToggle';
import { HiOutlineMenu } from 'react-icons/hi';

function Header({ onMenuClick }) {
  return (
    <header className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50 transition-colors">
      <div className="max-w-6xl mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-4">
            <button
              onClick={onMenuClick}
              className="md:hidden p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors"
            >
              <HiOutlineMenu size={24} className="text-gray-600 dark:text-gray-400" />
            </button>
            <Link to="/">
              <h1 className="text-xl font-bold text-blue-500 dark:text-blue-400">X Clone</h1>
            </Link>
          </div>
          <nav className="flex items-center space-x-4">
            <ThemeToggle />
            <Link to="/login">
              <Button variant="primary">
                로그인
              </Button>
            </Link>
          </nav>
        </div>
      </div>
    </header>
  );
}

export default Header;