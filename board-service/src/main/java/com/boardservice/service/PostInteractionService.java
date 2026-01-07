package com.boardservice.service;

import com.boardservice.dto.interaction.BookmarkResponse;
import com.boardservice.dto.interaction.LikeResponse;
import com.boardservice.dto.interaction.PostStatsResponse;
import com.boardservice.entity.Bookmark;
import com.boardservice.entity.Post;
import com.boardservice.entity.PostLike;
import com.boardservice.entity.PostShare;
import com.boardservice.entity.ShareType;
import com.boardservice.exception.DuplicateBookmarkException;
import com.boardservice.exception.DuplicateLikeException;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.BookmarkRepository;
import com.boardservice.repository.PostLikeRepository;
import com.boardservice.repository.PostRepository;
import com.boardservice.repository.PostShareRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 인터랙션 서비스 (좋아요, 북마크, 공유).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostInteractionService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final PostShareRepository postShareRepository;

    /**
     * 좋아요 추가.
     */
    @Transactional
    public LikeResponse likePost(Long userId, Long postId) {
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new DuplicateLikeException("이미 좋아요한 게시글입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        PostLike like = PostLike.builder()
                .post(post)
                .userId(userId)
                .build();

        PostLike savedLike = postLikeRepository.save(like);
        post.incrementLikeCount();

        log.info("좋아요 추가: postId={}, userId={}", postId, userId);

        return LikeResponse.from(savedLike);
    }

    /**
     * 좋아요 취소.
     */
    @Transactional
    public void unlikePost(Long userId, Long postId) {
        PostLike like = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("좋아요하지 않은 게시글입니다."));

        Post post = like.getPost();
        postLikeRepository.delete(like);
        post.decrementLikeCount();

        log.info("좋아요 취소: postId={}, userId={}", postId, userId);
    }

    /**
     * 좋아요 상태 조회.
     */
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long userId, Long postId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    /**
     * 사용자의 좋아요한 게시글 목록.
     */
    @Transactional(readOnly = true)
    public Page<LikeResponse> getUserLikes(Long userId, Pageable pageable) {
        Page<PostLike> likes = postLikeRepository.findByUserIdWithPost(userId, pageable);
        return likes.map(LikeResponse::from);
    }

    /**
     * 북마크 추가.
     */
    @Transactional
    public BookmarkResponse bookmarkPost(Long userId, Long postId) {
        if (bookmarkRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new DuplicateBookmarkException("이미 북마크한 게시글입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        Bookmark bookmark = Bookmark.builder()
                .post(post)
                .userId(userId)
                .build();

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        post.incrementBookmarkCount();

        log.info("북마크 추가: postId={}, userId={}", postId, userId);

        return BookmarkResponse.from(savedBookmark);
    }

    /**
     * 북마크 취소.
     */
    @Transactional
    public void unbookmarkPost(Long userId, Long postId) {
        Bookmark bookmark = bookmarkRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("북마크하지 않은 게시글입니다."));

        Post post = bookmark.getPost();
        bookmarkRepository.delete(bookmark);
        post.decrementBookmarkCount();

        log.info("북마크 취소: postId={}, userId={}", postId, userId);
    }

    /**
     * 북마크 상태 조회.
     */
    @Transactional(readOnly = true)
    public boolean isBookmarkedByUser(Long userId, Long postId) {
        return bookmarkRepository.existsByPostIdAndUserId(postId, userId);
    }

    /**
     * 사용자의 북마크한 게시글 목록.
     */
    @Transactional(readOnly = true)
    public Page<BookmarkResponse> getUserBookmarks(Long userId, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserIdWithPost(userId, pageable);
        return bookmarks.map(BookmarkResponse::from);
    }

    /**
     * 게시글 공유.
     */
    @Transactional
    public void sharePost(Long userId, Long postId, ShareType shareType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        PostShare share = PostShare.builder()
                .post(post)
                .userId(userId)
                .shareType(shareType)
                .build();

        postShareRepository.save(share);
        post.incrementShareCount();

        log.info("게시글 공유: postId={}, userId={}, shareType={}", postId, userId, shareType);
    }

    /**
     * 게시글 통계 조회.
     */
    @Transactional(readOnly = true)
    public PostStatsResponse getPostStats(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (currentUserId != null) {
            isLiked = postLikeRepository.existsByPostIdAndUserId(postId, currentUserId);
            isBookmarked = bookmarkRepository.existsByPostIdAndUserId(postId, currentUserId);
        }

        return PostStatsResponse.from(post, isLiked, isBookmarked);
    }
}
