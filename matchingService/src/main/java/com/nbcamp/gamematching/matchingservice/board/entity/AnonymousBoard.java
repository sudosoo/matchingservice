package com.nbcamp.gamematching.matchingservice.board.entity;

import com.nbcamp.gamematching.matchingservice.board.dto.BoardRequest;
import com.nbcamp.gamematching.matchingservice.comment.entity.AnonymousComment;
import com.nbcamp.gamematching.matchingservice.common.entity.BaseEntity;
import com.nbcamp.gamematching.matchingservice.exception.NotFoundException;
import com.nbcamp.gamematching.matchingservice.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class AnonymousBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "anonymousBoard_id")
    private Long id;
    @Column(name = "anonymous_nickname")
    private String nickname;
    private String boardImage;
    @Column(nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @OneToMany(mappedBy = "anonymousBoard", cascade = CascadeType.REMOVE)
    private List<AnonymousComment> comments = new ArrayList<>();
    public AnonymousBoard(String boardImage, String content, Member member) {
        this.nickname = nNick();
        this.boardImage = boardImage;
        this.content = content;
        this.member = member;
    }


    public void updateAnonymousBoard(BoardRequest boardRequest,String image) {
        this.boardImage = image;
        this.content = boardRequest.getContent();
        this.member = boardRequest.getMember();
    }

    public void checkUser(AnonymousBoard anonymousBoard, Member member) {
        if (!anonymousBoard.getMember().getId().equals(member.getId()))  {
            throw new NotFoundException();
        }
    }

    public static String nNick() {
        List<String> nick = Arrays.asList("기분나쁜", "기분좋은", "신바람나는", "상쾌한", "짜릿한", "그리운", "자유로운", "서운한", "울적한", "비참한", "위축되는", "긴장되는", "두려운", "당당한", "배부른", "수줍은", "창피한", "멋있는",
                "열받은", "심심한", "잘생긴", "이쁜", "시끄러운");
        List<String> name = Arrays.asList("사자", "코끼리", "호랑이", "곰", "여우", "늑대", "너구리", "침팬치", "고릴라", "참새", "고슴도치", "강아지", "고양이", "거북이", "토끼", "앵무새", "하이에나", "돼지", "하마", "원숭이", "물소", "얼룩말", "치타",
                "악어", "기린", "수달", "염소", "다람쥐", "판다");
        Collections.shuffle(nick);
        Collections.shuffle(name);
        String nickname = nick.get(0) + name.get(0);
        return nickname;
    }

}