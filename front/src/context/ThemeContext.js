import { createContext, useContext, useState, useEffect } from 'react';

const ThemeContext = createContext();

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export const ThemeProvider = ({ children }) => {
  const [isDark, setIsDark] = useState(() => {
    // 로컬 스토리지에서 테마 설정 불러오기
    const saved = localStorage.getItem('theme');
    return saved ? saved === 'dark' : false;
  });

  const toggleTheme = () => {
    setIsDark(prev => !prev);
  };

  useEffect(() => {
    // 테마 변경 시 로컬 스토리지에 저장
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    
    // HTML 클래스 토글
    if (isDark) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [isDark]);

  return (
    <ThemeContext.Provider value={{ isDark, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};