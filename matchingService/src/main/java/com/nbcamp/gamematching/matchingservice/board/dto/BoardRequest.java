package com.nbcamp.gamematching.matchingservice.board.dto;

import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {

    private Long id;
    private Member member;
    private MultipartFile image;
    private String content;

    public BoardRequest(String content, Member member, MultipartFile image) {
        this.member = member;
        this.image = image;
        this.content = content;
    }
    public BoardRequest(Long id, Member member) {
        this.id = id;
        this.member = member;
    }

    public void UpdateBoardRequest(String content) {
        this.content = content;
    }

    public void convertToDto(Long boardId, Member member, MultipartFile image) {
        this.id = boardId;
        this.member = member;
        this.image = image;
    }
    public void convertToDto(Member member, MultipartFile image) {
        this.member = member;
        this.image = image;
    }

}
