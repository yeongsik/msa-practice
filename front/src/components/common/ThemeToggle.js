import { useTheme } from '../../context/ThemeContext';
import { HiOutlineSun, HiOutlineMoon } from 'react-icons/hi';
import Button from './Button';

function ThemeToggle() {
  const { isDark, toggleTheme } = useTheme();

  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={toggleTheme}
      className="text-gray-600 hover:text-gray-900 hover:bg-gray-100 dark:text-gray-300 dark:hover:text-yellow-400 dark:hover:bg-gray-700"
      title={isDark ? '라이트 모드로 전환' : '다크 모드로 전환'}
    >
      {isDark ? (
        <HiOutlineSun size={20} />
      ) : (
        <HiOutlineMoon size={20} />
      )}
    </Button>
  );
}

export default ThemeToggle;