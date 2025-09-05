import NavItem from '../common/NavItem';
import Button from '../common/Button';
import { AiOutlineHome, AiFillHome } from 'react-icons/ai';
import { BiSearch } from 'react-icons/bi';
import { IoNotificationsOutline } from 'react-icons/io5';
import { HiOutlineMail } from 'react-icons/hi';
import { CgProfile } from 'react-icons/cg';

function Sidebar() {
  return (
    <aside className="w-64 p-4 hidden md:block dark:text-white">
      <nav className="space-y-2">
        <NavItem icon={<AiFillHome />} label="홈" active={true} />
        <NavItem icon={<BiSearch />} label="탐색" />
        <NavItem icon={<IoNotificationsOutline />} label="알림" />
        <NavItem icon={<HiOutlineMail />} label="메시지" />
        <NavItem icon={<CgProfile />} label="프로필" />
        <Button variant="primary" className="w-full mt-4 font-bold">
          트윗하기
        </Button>
      </nav>
    </aside>
  );
}

export default Sidebar;