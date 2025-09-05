function NavItem({ icon, label, onClick, active = false, className = '' }) {
  return (
    <button
      className={`
        flex items-center px-4 py-3 rounded-full w-full text-left
        transition-colors duration-200
        ${active 
          ? 'bg-blue-50 dark:bg-blue-900/50 text-blue-500 dark:text-blue-400 font-medium' 
          : 'hover:bg-gray-100 dark:hover:bg-gray-700 text-gray-900 dark:text-gray-100'
        }
        ${className}
      `}
      onClick={onClick}
    >
      <span className="text-2xl">{icon}</span>
      <span className="ml-4 text-xl">{label}</span>
    </button>
  );
}

export default NavItem;