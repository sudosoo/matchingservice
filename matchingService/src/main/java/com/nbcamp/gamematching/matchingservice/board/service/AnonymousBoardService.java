package com.nbcamp.gamematching.matchingservice.board.service;

import com.nbcamp.gamematching.matchingservice.board.dto.AnonymousBoardAdminDto;
import com.nbcamp.gamematching.matchingservice.board.dto.AnonymousBoardResponse;
import com.nbcamp.gamematching.matchingservice.board.dto.BoardRequest;
import com.nbcamp.gamematching.matchingservice.board.entity.AnonymousBoard;

import java.util.List;

public interface AnonymousBoardService {

    void createAnonymousBoard(BoardRequest boardRequest);

    List<AnonymousBoardResponse> getAnonymousBoardList();

    void updateAnonymousBoard(BoardRequest boardRequest);

    void deleteAnonymousBoard(BoardRequest boardRequest);

    AnonymousBoardResponse getAnonymousBoard(Long boardId);

    List<AnonymousBoardAdminDto> getAnonymousBoardsByAdmin(Integer page);

    void deleteAnonymousBoardByAdmin(Long boardId);

    AnonymousBoard findBoard(Long boardId);
}
