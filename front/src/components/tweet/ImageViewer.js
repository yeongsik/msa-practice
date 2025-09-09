import { useState, useEffect } from 'react';
import { HiOutlineX, HiChevronLeft, HiChevronRight } from 'react-icons/hi';

function ImageViewer({ isOpen, onClose, images, initialIndex = 0 }) {
  const [currentIndex, setCurrentIndex] = useState(initialIndex);

  useEffect(() => {
    setCurrentIndex(initialIndex);
  }, [initialIndex]);

  useEffect(() => {
    const handleKeyDown = (e) => {
      if (e.key === 'Escape') {
        onClose();
      } else if (e.key === 'ArrowLeft') {
        goToPrevious();
      } else if (e.key === 'ArrowRight') {
        goToNext();
      }
    };

    if (isOpen) {
      document.addEventListener('keydown', handleKeyDown);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, currentIndex]);

  const goToPrevious = () => {
    setCurrentIndex((prev) => 
      prev === 0 ? images.length - 1 : prev - 1
    );
  };

  const goToNext = () => {
    setCurrentIndex((prev) => 
      prev === images.length - 1 ? 0 : prev + 1
    );
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget) {
      onClose();
    }
  };

  if (!isOpen || !images || images.length === 0) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black bg-opacity-90 flex items-center justify-center">
      {/* 닫기 버튼 */}
      <button
        onClick={onClose}
        className="absolute top-4 right-4 z-10 p-2 bg-black bg-opacity-50 hover:bg-opacity-70 rounded-full text-white transition-all"
      >
        <HiOutlineX size={24} />
      </button>

      {/* 이미지 카운터 */}
      {images.length > 1 && (
        <div className="absolute top-4 left-1/2 transform -translate-x-1/2 z-10 px-3 py-1 bg-black bg-opacity-50 rounded-full text-white text-sm">
          {currentIndex + 1} / {images.length}
        </div>
      )}

      {/* 왼쪽 네비게이션 버튼 */}
      {images.length > 1 && (
        <button
          onClick={goToPrevious}
          className="absolute left-4 z-10 p-3 bg-black bg-opacity-50 hover:bg-opacity-70 rounded-full text-white transition-all"
        >
          <HiChevronLeft size={24} />
        </button>
      )}

      {/* 오른쪽 네비게이션 버튼 */}
      {images.length > 1 && (
        <button
          onClick={goToNext}
          className="absolute right-4 z-10 p-3 bg-black bg-opacity-50 hover:bg-opacity-70 rounded-full text-white transition-all"
        >
          <HiChevronRight size={24} />
        </button>
      )}

      {/* 메인 이미지 영역 */}
      <div 
        className="w-full h-full flex items-center justify-center p-4"
        onClick={handleBackdropClick}
      >
        <div className="relative max-w-full max-h-full">
          <img
            src={images[currentIndex]}
            alt={`이미지 ${currentIndex + 1}`}
            className="max-w-full max-h-full object-contain"
          />
        </div>
      </div>

      {/* 하단 썸네일 (이미지가 2장 이상일 때) */}
      {images.length > 1 && (
        <div className="absolute bottom-4 left-1/2 transform -translate-x-1/2 z-10">
          <div className="flex space-x-2 bg-black bg-opacity-50 rounded-lg p-2">
            {images.map((image, index) => (
              <button
                key={index}
                onClick={() => setCurrentIndex(index)}
                className={`w-12 h-12 rounded overflow-hidden border-2 transition-all ${
                  index === currentIndex 
                    ? 'border-white scale-110' 
                    : 'border-transparent opacity-70 hover:opacity-100'
                }`}
              >
                <img
                  src={image}
                  alt={`썸네일 ${index + 1}`}
                  className="w-full h-full object-cover"
                />
              </button>
            ))}
          </div>
        </div>
      )}

      {/* 스와이프 제스처 영역 (모바일용) */}
      {images.length > 1 && (
        <div
          className="absolute inset-0 z-0"
          onTouchStart={(e) => {
            const touch = e.touches[0];
            e.currentTarget.startX = touch.clientX;
          }}
          onTouchEnd={(e) => {
            const touch = e.changedTouches[0];
            const startX = e.currentTarget.startX;
            const endX = touch.clientX;
            const diff = startX - endX;
            
            if (Math.abs(diff) > 50) { // 50px 이상 스와이프
              if (diff > 0) {
                goToNext(); // 왼쪽으로 스와이프 -> 다음 이미지
              } else {
                goToPrevious(); // 오른쪽으로 스와이프 -> 이전 이미지
              }
            }
          }}
        />
      )}
    </div>
  );
}

export default ImageViewer;