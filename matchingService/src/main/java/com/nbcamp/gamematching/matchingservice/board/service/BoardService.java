package com.nbcamp.gamematching.matchingservice.board.service;

import com.nbcamp.gamematching.matchingservice.board.dto.BoardAdminDto;
import com.nbcamp.gamematching.matchingservice.board.dto.BoardRequest;
import com.nbcamp.gamematching.matchingservice.board.dto.BoardResponse;
import com.nbcamp.gamematching.matchingservice.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    void createBoard(BoardRequest boardRequest);

    List<BoardResponse> getBoardList();

    void updateBoard(BoardRequest boardRequest);

    void deleteBoard(BoardRequest boardRequest);

    BoardResponse getBoard(Long boardId);

    List<BoardAdminDto> getBoardsByAdmin(Integer page);

    void deleteBoardByAdmin(Long boardId);

    Page<Board> findAllByMemberId(Long memberId, Pageable pageable);

    Board findBoard(Long boardId);
}
