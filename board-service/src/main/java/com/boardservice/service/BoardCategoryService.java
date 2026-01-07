package com.boardservice.service;

import com.boardservice.dto.board.BoardCategoryResponse;
import com.boardservice.dto.board.CreateBoardCategoryRequest;
import com.boardservice.entity.BoardCategory;
import com.boardservice.repository.BoardCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시판 카테고리 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardCategoryService {

    private final BoardCategoryRepository boardCategoryRepository;

    /**
     * 게시판 생성.
     */
    @Transactional
    public BoardCategoryResponse createBoardCategory(CreateBoardCategoryRequest request) {
        if (boardCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다: " + request.getName());
        }

        BoardCategory category = BoardCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        BoardCategory savedCategory = boardCategoryRepository.save(category);
        log.info("게시판 생성 완료: id={}, name={}", savedCategory.getId(), savedCategory.getName());

        return BoardCategoryResponse.from(savedCategory);
    }

    /**
     * 전체 게시판 목록 조회.
     */
    @Transactional(readOnly = true)
    public List<BoardCategoryResponse> getAllBoardCategories() {
        return boardCategoryRepository.findAll().stream()
                .map(BoardCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 활성화된 게시판 목록 조회.
     */
    @Transactional(readOnly = true)
    public List<BoardCategoryResponse> getActiveBoardCategories() {
        return boardCategoryRepository.findByIsActiveTrueOrderByCreatedAtAsc().stream()
                .map(BoardCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시판 단건 조회.
     */
    @Transactional(readOnly = true)
    public BoardCategoryResponse getBoardCategory(Long id) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. id=" + id));

        return BoardCategoryResponse.from(category);
    }

    /**
     * 게시판 수정.
     */
    @Transactional
    public BoardCategoryResponse updateBoardCategory(Long id, CreateBoardCategoryRequest request) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. id=" + id));

        if (!category.getName().equals(request.getName())
                && boardCategoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 게시판 이름입니다: " + request.getName());
        }

        category.update(request.getName(), request.getDescription());
        log.info("게시판 수정 완료: id={}, name={}", category.getId(), category.getName());

        return BoardCategoryResponse.from(category);
    }

    /**
     * 게시판 삭제.
     */
    @Transactional
    public void deleteBoardCategory(Long id) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. id=" + id));

        if (category.getPostCount() > 0) {
            throw new IllegalArgumentException("게시글이 존재하는 게시판은 삭제할 수 없습니다. postCount="
                    + category.getPostCount());
        }

        boardCategoryRepository.delete(category);
        log.info("게시판 삭제 완료: id={}, name={}", category.getId(), category.getName());
    }

    /**
     * 게시판 비활성화.
     */
    @Transactional
    public void deactivateBoardCategory(Long id) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. id=" + id));

        category.deactivate();
        log.info("게시판 비활성화: id={}, name={}", category.getId(), category.getName());
    }

    /**
     * 게시판 활성화.
     */
    @Transactional
    public void activateBoardCategory(Long id) {
        BoardCategory category = boardCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. id=" + id));

        category.activate();
        log.info("게시판 활성화: id={}, name={}", category.getId(), category.getName());
    }
}
