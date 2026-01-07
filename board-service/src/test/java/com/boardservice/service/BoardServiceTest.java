package com.boardservice.service;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.BoardResponse;
import com.boardservice.dto.CreateBoardRequest;
import com.boardservice.dto.UpdateBoardRequest;
import com.boardservice.entity.Board;
import com.boardservice.repository.BoardRepository;
import com.common.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BoardService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private BoardService boardService;

    private Board board;
    private CreateBoardRequest createRequest;
    private UpdateBoardRequest updateRequest;
    private UserServiceClient.UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createRequest = CreateBoardRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        updateRequest = UpdateBoardRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        board = Board.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .userId(1L)
                .build();

        userResponse = new UserServiceClient.UserResponse();
        // UserResponse는 setter가 없으므로 reflection이나 @AllArgsConstructor 사용 필요
        // 현재는 Mock으로 ApiResponse를 반환하도록 설정
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoard_Success() {
        // given
        given(boardRepository.save(any(Board.class))).willReturn(board);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        BoardResponse response = boardService.createBoard(1L, createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");

        verify(boardRepository).save(any(Board.class));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    @DisplayName("전체 게시글 조회")
    void getAllBoards_Success() {
        // given
        Board board2 = Board.builder()
                .id(2L)
                .title("두번째 게시글")
                .content("두번째 내용")
                .userId(1L)
                .build();

        given(boardRepository.findAllByOrderByCreatedAtDesc()).willReturn(Arrays.asList(board, board2));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        List<BoardResponse> responses = boardService.getAllBoards();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("테스트 제목");
        assertThat(responses.get(1).getTitle()).isEqualTo("두번째 게시글");

        verify(boardRepository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void getBoard_Success() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        BoardResponse response = boardService.getBoard(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");

        verify(boardRepository).findById(1L);
        verify(userServiceClient).getUser(1L);
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 존재하지 않는 게시글")
    void getBoard_Fail_NotFound() {
        // given
        given(boardRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoard(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(boardRepository).findById(999L);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateBoard_Success() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        BoardResponse response = boardService.updateBoard(1L, 1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("수정된 내용");

        verify(boardRepository).findById(1L);
        verify(userServiceClient).getUser(1L);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    void updateBoard_Fail_NotFound() {
        // given
        given(boardRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.updateBoard(1L, 999L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(boardRepository).findById(999L);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자가 아님")
    void updateBoard_Fail_Unauthorized() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        // when & then - userId가 다른 경우 (board.userId=1L, 요청 userId=2L)
        assertThatThrownBy(() -> boardService.updateBoard(2L, 1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 작성자만 수정할 수 있습니다");

        verify(boardRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoard_Success() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        // when
        boardService.deleteBoard(1L, 1L);

        // then
        verify(boardRepository).findById(1L);
        verify(boardRepository).delete(board);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    void deleteBoard_Fail_NotFound() {
        // given
        given(boardRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.deleteBoard(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(boardRepository).findById(999L);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자가 아님")
    void deleteBoard_Fail_Unauthorized() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));

        // when & then - userId가 다른 경우
        assertThatThrownBy(() -> boardService.deleteBoard(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 작성자만 삭제할 수 있습니다");

        verify(boardRepository).findById(1L);
    }

    @Test
    @DisplayName("UserService 호출 실패 시 Unknown 반환")
    void getUsername_Fail_ReturnsUnknown() {
        // given
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(userServiceClient.getUser(anyLong())).willThrow(new RuntimeException("UserService unavailable"));

        // when
        BoardResponse response = boardService.getBoard(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("Unknown");

        verify(userServiceClient).getUser(1L);
    }
}
