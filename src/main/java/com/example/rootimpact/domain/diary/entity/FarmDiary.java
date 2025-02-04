package com.example.rootimpact.domain.diary.entity;

import com.example.rootimpact.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FarmDiary {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne //다대일
    @JoinColumn(name = "user_id") //외래키
    private User user;

    @Column(nullable = false)
    private LocalDate writeDate = LocalDate.now(); //현재 날짜 자동 설정

    //작물 선택

    //작업 선택

    @Column(columnDefinition = "TEXT")
    private String content;

    //이미지

    @Builder
    public FarmDiary(long id, User user, String content, LocalDate writeDate) {
        this.id = id;
        this.user = user;
        this.content = content;
        if (writeDate != null) {
            this.writeDate = writeDate;
        }
    }

    public void update(String content, LocalDate writeDate) {
        if (content != null) this.content = content;
        if (writeDate != null) this.writeDate = writeDate;
    }
}
