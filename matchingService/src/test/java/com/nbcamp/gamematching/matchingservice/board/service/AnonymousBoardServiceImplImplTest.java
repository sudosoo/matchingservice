package com.nbcamp.gamematching.matchingservice.board.service;

import com.nbcamp.gamematching.matchingservice.board.dto.AnonymousBoardResponse;
import com.nbcamp.gamematching.matchingservice.board.dto.CreateBoardRequest;
import com.nbcamp.gamematching.matchingservice.board.dto.UpdateBoardRequest;
import com.nbcamp.gamematching.matchingservice.board.entity.AnonymousBoard;
import com.nbcamp.gamematching.matchingservice.board.repository.AnonymousBoardRepository;
import com.nbcamp.gamematching.matchingservice.comment.dto.CreateCommentRequest;
import com.nbcamp.gamematching.matchingservice.comment.repository.AnonymousCommentRepository;
import com.nbcamp.gamematching.matchingservice.comment.service.CommentServiceImpl;
import com.nbcamp.gamematching.matchingservice.like.repository.AnonymousLikeRepository;
import com.nbcamp.gamematching.matchingservice.member.domain.GameType;
import com.nbcamp.gamematching.matchingservice.member.domain.MemberRoleEnum;
import com.nbcamp.gamematching.matchingservice.member.domain.Tier;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import com.nbcamp.gamematching.matchingservice.member.entity.Profile;
import com.nbcamp.gamematching.matchingservice.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.nbcamp.gamematching.matchingservice.board.service.BoardServiceImplTest.multipartFile;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@Transactional
class AnonymousBoardServiceImplImplTest {
    @Autowired
    private AnonymousBoardService boardService;

    @Autowired
    private CommentServiceImpl commentServiceImpl;

    @Autowired
    private AnonymousBoardRepository boardRepository;


    @Autowired
    private AnonymousCommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AnonymousLikeRepository likeRepository;

    @BeforeEach
    public void beforeEach() {
        Member member = new Member("dddd1@ddd.com", "123456789", new Profile("www11", "sdadad.jpg", Tier.BRONZE, GameType.LOL), MemberRoleEnum.USER,null,null);
        memberRepository.save(member);
        Member member1 = new Member("dddd2@ddd.com", "123456789", new Profile("www12", "sdadad2.jpg", Tier.BRONZE, GameType.LOL), MemberRoleEnum.USER,null,null);
        memberRepository.save(member1);

    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????????")
    void createAnonymousBoard() throws IOException {
        Member findMember = memberRepository.findById(3L).orElseThrow();

        CreateBoardRequest createBoardRequest = new CreateBoardRequest("??????");

        boardService.createAnonymousBoard(createBoardRequest,findMember,multipartFile);

        AnonymousBoard board = boardRepository.findById(1L).orElseThrow(()-> new IllegalArgumentException(""));

        assertEquals("??????",board.getContent());
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????????")
    void getAnonymousBoardList() {
        Member findMember = memberRepository.findById(3L).orElseThrow();

        AnonymousBoard boards = new AnonymousBoard("ddddd.jpg","???????????????",findMember);

        boardRepository.save(boards);

        CreateCommentRequest createCommentRequest = new CreateCommentRequest("??????");

        commentServiceImpl.createAnonymousComment(1L,createCommentRequest.getContent(),findMember);

        List<AnonymousBoardResponse> anonymousBoardList = boardService.getAnonymousBoardList();

        assertEquals(0,anonymousBoardList.get(0).getLikeCount());
        assertEquals("???????????????",anonymousBoardList.get(0).getContent());
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????????")
    void updateAnonymousBoard() throws IOException {
        Member findMember = memberRepository.findById(3L).orElseThrow();

        CreateBoardRequest createBoardRequest = new CreateBoardRequest("??????");

        boardService.createAnonymousBoard(createBoardRequest,findMember,multipartFile);

        UpdateBoardRequest updateBoardRequest = new UpdateBoardRequest("??????");

        boardService.updateAnonymousBoard(1L,updateBoardRequest,findMember, multipartFile);

        AnonymousBoard board = boardRepository.findById(1L).orElseThrow();

        System.out.println(board.getId());

        assertEquals("??????",board.getContent());
    }

    @Test
    @DisplayName("?????? ????????? ?????? ?????????")
    void deleteAnonymousBoard() {
        Member findMember = memberRepository.findById(3L).orElseThrow();

        CreateBoardRequest createBoardRequest = new CreateBoardRequest("??????");

        boardService.createAnonymousBoard(createBoardRequest,findMember,multipartFile);

        boardService.deleteAnonymousBoard(1L,findMember);

        AnonymousBoard board = boardRepository.findById(1L).orElseThrow(()-> new IllegalArgumentException("???????????? ?????? ??? ????????????."));

        assertEquals(null,board);
    }
}