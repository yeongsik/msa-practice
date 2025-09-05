import { useTheme } from '../../context/ThemeContext';

function Avatar({ size = 'md', src, alt = 'Avatar', className = '', userId }) {
  const { isDark } = useTheme();
  const getSizeClasses = () => {
    switch (size) {
      case 'sm':
        return 'w-8 h-8';
      case 'md':
        return 'w-12 h-12';
      case 'lg':
        return 'w-16 h-16';
      case 'xl':
        return 'w-20 h-20';
      default:
        return 'w-12 h-12';
    }
  };

  const getSizeNumber = () => {
    switch (size) {
      case 'sm':
        return 32;
      case 'md':
        return 48;
      case 'lg':
        return 64;
      case 'xl':
        return 80;
      default:
        return 48;
    }
  };

  const generateRandomAvatar = () => {
    const seed = userId || Math.random().toString(36).substring(7);
    const sizeNumber = getSizeNumber();
    
    // 테마에 따른 배경색 설정
    const lightBgColors = 'b6e3f4,c0aede,d1d4f9,ffd5dc,ffdfbf';
    const darkBgColors = '1f2937,374151,4b5563,6b7280,9ca3af';
    const bgColors = isDark ? darkBgColors : lightBgColors;
    
    // 통일된 스타일 사용 - thumbs (미니멀한 썸네일 스타일)
    return `https://api.dicebear.com/7.x/thumbs/svg?seed=${seed}&size=${sizeNumber}&backgroundColor=${bgColors}`;
  };

  const avatarSrc = src || generateRandomAvatar();

  return (
    <div
      className={`
        ${getSizeClasses()}
        bg-gray-300 rounded-full flex-shrink-0 overflow-hidden
        ${className}
      `}
    >
      <img
        src={avatarSrc}
        alt={alt}
        className={`${getSizeClasses()} rounded-full object-cover`}
        onError={(e) => {
          // 이미지 로드 실패 시 initials 스타일로 fallback (통일된 스타일 유지)
          if (!e.target.dataset.retry) {
            e.target.dataset.retry = 'true';
            e.target.src = `https://api.dicebear.com/7.x/initials/svg?seed=${userId || 'default'}&size=${getSizeNumber()}&backgroundColor=6366f1&textColor=ffffff`;
          }
        }}
      />
    </div>
  );
}

export default Avatar;