package com.boardservice.service;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.post.CreatePostRequest;
import com.boardservice.dto.post.PostDetailResponse;
import com.boardservice.dto.post.PostResponse;
import com.boardservice.dto.post.UpdatePostRequest;
import com.boardservice.entity.BoardCategory;
import com.boardservice.entity.Post;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.BoardCategoryRepository;
import com.boardservice.repository.BookmarkRepository;
import com.boardservice.repository.PostLikeRepository;
import com.boardservice.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 게시글 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final PostLikeRepository postLikeRepository;
    private final BookmarkRepository bookmarkRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 게시글 생성.
     */
    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        BoardCategory board = boardCategoryRepository.findById(request.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "게시판을 찾을 수 없습니다. id=" + request.getBoardId()));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(userId)
                .board(board)
                .build();

        Post savedPost = postRepository.save(post);

        board.incrementPostCount();

        String username = getUsername(userId);
        log.info("게시글 생성 완료: postId={}, userId={}, boardId={}",
                savedPost.getId(), userId, board.getId());

        return PostResponse.from(savedPost, username);
    }

    /**
     * 전체 게시글 목록 조회 (페이징).
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * 게시판별 게시글 목록 조회.
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByBoard(Long boardId, Pageable pageable) {
        Page<Post> posts = postRepository.findByBoardIdOrderByCreatedAtDesc(boardId, pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * 사용자별 게시글 목록 조회.
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUser(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * 게시글 상세 조회.
     */
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdWithBoard(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        String username = getUsername(post.getUserId());

        boolean isLiked = false;
        boolean isBookmarked = false;

        if (currentUserId != null) {
            isLiked = postLikeRepository.existsByPostIdAndUserId(postId, currentUserId);
            isBookmarked = bookmarkRepository.existsByPostIdAndUserId(postId, currentUserId);
        }

        return PostDetailResponse.from(post, username, isLiked, isBookmarked);
    }

    /**
     * 게시글 수정.
     */
    @Transactional
    public PostResponse updatePost(Long userId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        post.update(request.getTitle(), request.getContent());
        String username = getUsername(userId);

        log.info("게시글 수정 완료: postId={}, userId={}", postId, userId);

        return PostResponse.from(post, username);
    }

    /**
     * 게시글 삭제.
     */
    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findByIdWithBoard(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        post.getBoard().decrementPostCount();

        postRepository.delete(post);

        log.info("게시글 삭제 완료: postId={}, userId={}", postId, userId);
    }

    /**
     * 인기 게시글 조회 (좋아요 기준).
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPopularPostsByLikes(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByLikeCountDescCreatedAtDesc(pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * 인기 게시글 조회 (조회수 기준).
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> getPopularPostsByViews(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByOrderByViewCountDescCreatedAtDesc(pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * 게시글 검색.
     */
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);

        return posts.map(post -> {
            String username = getUsername(post.getUserId());
            return PostResponse.from(post, username);
        });
    }

    /**
     * User Service로부터 username 조회 (Feign Client).
     */
    private String getUsername(Long userId) {
        try {
            return userServiceClient.getUser(userId).getData().getUsername();
        } catch (Exception e) {
            log.error("Failed to fetch username for userId: {}", userId, e);
            return "Unknown";
        }
    }
}
