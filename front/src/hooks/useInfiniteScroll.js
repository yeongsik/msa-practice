import { useState, useEffect, useCallback, useRef } from 'react';

const useInfiniteScroll = (fetchMore) => {
  const [isFetching, setIsFetching] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const observerRef = useRef();
  const lastElementRef = useRef();

  const lastTweetElementRef = useCallback(node => {
    if (isFetching) return;
    if (observerRef.current) observerRef.current.disconnect();
    
    observerRef.current = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting && hasMore) {
        setIsFetching(true);
      }
    }, {
      threshold: 1.0,
      rootMargin: '100px'
    });
    
    if (node) {
      observerRef.current.observe(node);
      lastElementRef.current = node;
    }
  }, [isFetching, hasMore]);

  useEffect(() => {
    if (!isFetching) return;
    
    const loadMore = async () => {
      try {
        const result = await fetchMore();
        if (result && result.hasMore !== undefined) {
          setHasMore(result.hasMore);
        }
      } catch (error) {
        console.error('Error fetching more data:', error);
      } finally {
        setIsFetching(false);
      }
    };

    loadMore();
  }, [isFetching, fetchMore]);

  return {
    isFetching,
    hasMore,
    lastTweetElementRef,
    setHasMore
  };
};

export default useInfiniteScroll;