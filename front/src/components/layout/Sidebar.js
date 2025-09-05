import { Link, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import NavItem from '../common/NavItem';
import Button from '../common/Button';
import { AiOutlineHome, AiFillHome } from 'react-icons/ai';
import { BiSearch } from 'react-icons/bi';
import { IoNotificationsOutline } from 'react-icons/io5';
import { HiOutlineMail, HiOutlineX } from 'react-icons/hi';
import { CgProfile } from 'react-icons/cg';

function Sidebar({ onTweetClick, isMobileOpen, onMobileClose }) {
  const location = useLocation();

  useEffect(() => {
    if (isMobileOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isMobileOpen]);

  const handleLinkClick = () => {
    if (onMobileClose) {
      onMobileClose();
    }
  };

  return (
    <>
      {isMobileOpen && (
        <div 
          className="fixed inset-0 bg-black bg-opacity-50 z-40 md:hidden"
          onClick={onMobileClose}
        />
      )}
      
      <aside className={`
        w-64 p-4 dark:text-white transition-transform duration-300 ease-in-out
        md:block md:relative md:transform-none
        ${isMobileOpen 
          ? 'fixed left-0 top-0 h-full bg-white dark:bg-gray-800 z-50 transform translate-x-0' 
          : 'fixed left-0 top-0 h-full bg-white dark:bg-gray-800 z-50 transform -translate-x-full md:translate-x-0'
        }
      `}>
        <div className="md:hidden flex justify-between items-center mb-6 pb-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-xl font-bold text-gray-900 dark:text-white">메뉴</h2>
          <button
            onClick={onMobileClose}
            className="p-2 hover:bg-gray-100 dark:hover:bg-gray-700 rounded-full transition-colors"
          >
            <HiOutlineX size={24} className="text-gray-600 dark:text-gray-400" />
          </button>
        </div>
        
        <nav className="space-y-2">
          <Link to="/" onClick={handleLinkClick}>
            <NavItem icon={<AiFillHome />} label="홈" active={location.pathname === '/'} />
          </Link>
          <Link to="/search" onClick={handleLinkClick}>
            <NavItem icon={<BiSearch />} label="탐색" active={location.pathname === '/search'} />
          </Link>
          <Link to="/notifications" onClick={handleLinkClick}>
            <NavItem icon={<IoNotificationsOutline />} label="알림" active={location.pathname === '/notifications'} />
          </Link>
          <Link to="/messages" onClick={handleLinkClick}>
            <NavItem icon={<HiOutlineMail />} label="메시지" active={location.pathname === '/messages'} />
          </Link>
          <Link to="/profile/user" onClick={handleLinkClick}>
            <NavItem icon={<CgProfile />} label="프로필" active={location.pathname.startsWith('/profile')} />
          </Link>
          <Button 
            variant="primary" 
            className="w-full mt-4 font-bold"
            onClick={() => {
              onTweetClick();
              handleLinkClick();
            }}
          >
            트윗하기
          </Button>
        </nav>
      </aside>
    </>
  );
}

export default Sidebar;