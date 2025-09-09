import { useState } from 'react';
import ImageViewer from './ImageViewer';

function ImageGallery({ images, onImageClick }) {
  const [showAll, setShowAll] = useState(false);
  const [isViewerOpen, setIsViewerOpen] = useState(false);
  const [viewerStartIndex, setViewerStartIndex] = useState(0);
  
  if (!images || images.length === 0) return null;

  // 최대 4장까지만 미리보기로 표시
  const displayImages = showAll ? images : images.slice(0, 4);
  const remainingCount = images.length - 4;

  const handleImageClick = (clickedIndex) => {
    // 실제 이미지 배열에서의 인덱스 계산
    const actualIndex = showAll ? clickedIndex : clickedIndex;
    setViewerStartIndex(actualIndex);
    setIsViewerOpen(true);
  };

  const handleCloseViewer = () => {
    setIsViewerOpen(false);
  };

  const getGridClass = (count) => {
    switch (count) {
      case 1:
        return 'grid-cols-1';
      case 2:
        return 'grid-cols-2';
      case 3:
        return 'grid-cols-2 [&>:first-child]:col-span-2 [&>:first-child]:row-span-2';
      default:
        return 'grid-cols-2';
    }
  };

  const getImageClass = (index, total) => {
    if (total === 1) {
      return 'aspect-video max-h-96';
    }
    if (total === 2) {
      return 'aspect-square';
    }
    if (total === 3) {
      if (index === 0) {
        return 'aspect-square row-span-2';
      }
      return 'aspect-square';
    }
    return 'aspect-square';
  };

  return (
    <div className="mt-3">
      <div className={`grid gap-1 ${getGridClass(Math.min(displayImages.length, 4))}`}>
        {displayImages.map((image, index) => (
          <div 
            key={index} 
            className={`relative overflow-hidden rounded-lg border border-gray-200 dark:border-gray-600 cursor-pointer ${getImageClass(index, displayImages.length)}`}
            onClick={(e) => {
              e.stopPropagation();
              // 5장 이상일 때 마지막 이미지의 오버레이를 클릭하면 더보기 대신 이미지 뷰어 열기
              if (!showAll && index === 3 && remainingCount > 0) {
                handleImageClick(index);
              } else {
                handleImageClick(index);
              }
              
              // 기존 onImageClick 콜백 유지
              if (onImageClick) {
                onImageClick(image, index, images);
              }
            }}
          >
            <img 
              src={image} 
              alt={`이미지 ${index + 1}`}
              className="w-full h-full object-cover hover:opacity-90 transition-opacity"
            />
            
            {/* 5장 이상일 때 마지막 이미지에 더보기 오버레이 */}
            {!showAll && index === 3 && remainingCount > 0 && (
              <div className="absolute inset-0 bg-black bg-opacity-60 flex items-center justify-center">
                <div className="text-white text-xl font-bold">
                  +{remainingCount}
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
      
      {/* 5장 이상일 때 더보기/접기 버튼 */}
      {images.length > 4 && (
        <button
          onClick={(e) => {
            e.stopPropagation();
            setShowAll(!showAll);
          }}
          className="mt-2 text-blue-500 hover:text-blue-600 text-sm font-medium"
        >
          {showAll ? '접기' : `${images.length}장 모두 보기`}
        </button>
      )}
      
      {/* 이미지 뷰어 모달 */}
      <ImageViewer
        isOpen={isViewerOpen}
        onClose={handleCloseViewer}
        images={images}
        initialIndex={viewerStartIndex}
      />
    </div>
  );
}

export default ImageGallery;