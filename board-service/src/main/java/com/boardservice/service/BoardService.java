package com.boardservice.service;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.BoardResponse;
import com.boardservice.dto.CreateBoardRequest;
import com.boardservice.dto.UpdateBoardRequest;
import com.boardservice.entity.Board;
import com.boardservice.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 게시글 생성
     */
    @Transactional
    public BoardResponse createBoard(Long userId, CreateBoardRequest request) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(userId)
                .build();

        Board savedBoard = boardRepository.save(board);
        String username = getUsername(userId);
        
        return BoardResponse.from(savedBoard, username);
    }

    /**
     * 게시글 전체 조회
     */
    @Transactional(readOnly = true)
    public List<BoardResponse> getAllBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(board -> {
                    String username = getUsername(board.getUserId());
                    return BoardResponse.from(board, username);
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 단건 조회
     */
    @Transactional(readOnly = true)
    public BoardResponse getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + boardId));
        
        String username = getUsername(board.getUserId());
        return BoardResponse.from(board, username);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public BoardResponse updateBoard(Long userId, Long boardId, UpdateBoardRequest request) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + boardId));

        if (!board.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        board.update(request.getTitle(), request.getContent());
        String username = getUsername(userId);
        
        return BoardResponse.from(board, username);
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public void deleteBoard(Long userId, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + boardId));

        if (!board.getUserId().equals(userId)) {
            throw new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }

    /**
     * User Service로부터 username 조회 (Feign Client)
     */
    private String getUsername(Long userId) {
        try {
            return userServiceClient.getUser(userId).getData().getUsername();
        } catch (Exception e) {
            log.error("Failed to fetch username for userId: {}", userId, e);
            return "Unknown"; // 통신 실패 시 기본값
        }
    }
}
