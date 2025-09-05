function Icon({ children, size = 20, className = '', ...props }) {
  return (
    <span
      className={`inline-flex items-center justify-center ${className}`}
      style={{ fontSize: size }}
      {...props}
    >
      {children}
    </span>
  );
}

export default Icon;