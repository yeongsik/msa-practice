function Button({ 
  children, 
  variant = 'primary', 
  size = 'md', 
  className = '', 
  onClick,
  ...props 
}) {
  const getVariantClasses = () => {
    switch (variant) {
      case 'primary':
        return 'bg-blue-500 text-white hover:bg-blue-600';
      case 'secondary':
        return 'text-blue-500 hover:bg-blue-50';
      case 'black':
        return 'bg-black text-white hover:bg-gray-800';
      case 'ghost':
        return 'text-gray-500 hover:bg-gray-100';
      default:
        return 'bg-blue-500 text-white hover:bg-blue-600';
    }
  };

  const getSizeClasses = () => {
    switch (size) {
      case 'sm':
        return 'px-3 py-1 text-sm';
      case 'md':
        return 'px-4 py-2';
      case 'lg':
        return 'px-6 py-3';
      case 'icon':
        return 'p-2';
      default:
        return 'px-4 py-2';
    }
  };

  return (
    <button
      className={`
        rounded-full font-medium transition-colors duration-200
        ${getVariantClasses()}
        ${getSizeClasses()}
        ${className}
      `}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
}

export default Button;