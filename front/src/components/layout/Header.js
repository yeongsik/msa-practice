import Button from '../common/Button';
import ThemeToggle from '../common/ThemeToggle';

function Header() {
  return (
    <header className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50 transition-colors">
      <div className="max-w-6xl mx-auto px-4">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center space-x-4">
            <h1 className="text-xl font-bold text-blue-500 dark:text-blue-400">X Clone</h1>
          </div>
          <nav className="flex items-center space-x-4">
            <ThemeToggle />
            <Button variant="secondary">
              로그인
            </Button>
            <Button variant="primary">
              회원가입
            </Button>
          </nav>
        </div>
      </div>
    </header>
  );
}

export default Header;