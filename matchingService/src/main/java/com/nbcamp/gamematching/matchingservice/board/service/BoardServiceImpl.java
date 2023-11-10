package com.nbcamp.gamematching.matchingservice.board.service;

import com.nbcamp.gamematching.matchingservice.board.dto.BoardAdminDto;
import com.nbcamp.gamematching.matchingservice.board.dto.BoardRequest;
import com.nbcamp.gamematching.matchingservice.board.dto.BoardResponse;
import com.nbcamp.gamematching.matchingservice.board.entity.Board;
import com.nbcamp.gamematching.matchingservice.board.repository.BoardRepository;
import com.nbcamp.gamematching.matchingservice.common.domain.CreatePageable;
import com.nbcamp.gamematching.matchingservice.exception.NotFoundException;
import com.nbcamp.gamematching.matchingservice.like.repository.LikeRepository;
import com.nbcamp.gamematching.matchingservice.member.domain.FileDetail;
import com.nbcamp.gamematching.matchingservice.member.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final LikeRepository likeRepository;
    private final FileUploadService fileUploadService;

    //게시글 작성
    public void createBoard(BoardRequest boardRequest) {

        //이미지파일이 없을 때 기본이미지 넣기
        if (boardRequest.getImage() == null) {
            String boardImage = "images/nav/logo.png";
            Board board = new Board(boardImage,
                    boardRequest);
            boardRepository.save(board);

        } else {
            FileDetail fileDetail = fileUploadService.save(boardRequest.getImage());
            Board board = new Board(fileDetail.getPath(),
                    boardRequest);
            boardRepository.save(board);
            System.out.println(fileDetail.getPath());
        }
    }

    //게시글 조회
    public List<BoardResponse> getBoardList() {
        Page<Board> boardPage = boardRepository.findAll(pageableSetting(1));
        List<BoardResponse> boardResponseList = new ArrayList<>();
        for (Board board : boardPage) {
            Long likeCount = likeRepository.countByBoardId(board.getId());
            boardResponseList.add(new BoardResponse(board, likeCount));
        }
        return boardResponseList;
    }

    //게시글 수정
    public void updateBoard(BoardRequest boardRequest) {
        Board board = boardRepository.findById(boardRequest.getId()).orElseThrow(NotFoundException::new);
        board.checkUser(board, boardRequest.getMember());
        if (boardRequest.getImage() == null) {
            String boardImage = "images/nav/logo.png";
            board.updateBoard(boardRequest, boardImage);
            boardRepository.save(board);
        } else {
            FileDetail fileDetail = fileUploadService.save(boardRequest.getImage());
            board.updateBoard(boardRequest, fileDetail.getPath());
            boardRepository.save(board);
        }
    }

    //게시글 삭제
    public void deleteBoard(BoardRequest boardRequest) {
        Board board = boardRepository.findById(boardRequest.getId()).orElseThrow(NotFoundException::new);
        board.checkUser(board, boardRequest.getMember());
        likeRepository.deleteAllByBoardId(board.getId());
        boardRepository.deleteById(board.getId());
    }

    //페이징
    public static Pageable pageableSetting(int page) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "modifiedAt");
        Pageable pageable = PageRequest.of(page - 1, 300, sort);
        return pageable;
    }


    public BoardResponse getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(NotFoundException::new);
        BoardResponse boardResponse = new BoardResponse(board);
        return boardResponse;
    }

    public List<BoardAdminDto> getBoardsByAdmin(Integer page) {
        Page<Board> boardPage = boardRepository.findAll(CreatePageable.createPageable(page));
        return BoardAdminDto.of(boardPage.getContent());
    }

    public void deleteBoardByAdmin(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    public Page<Board> findAllByMemberId(Long memberId, Pageable pageable) {
        return boardRepository.findAllByMemberId(memberId, pageable);
    }

    public Board findBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(NotFoundException::new);
        return board;
    }

}