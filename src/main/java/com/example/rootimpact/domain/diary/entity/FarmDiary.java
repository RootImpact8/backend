package com.example.rootimpact.domain.diary.entity;

import com.example.rootimpact.domain.user.entity.User;
import com.example.rootimpact.domain.userInfo.entity.UserCrop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "farmdiary")
public class FarmDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY) //다대일
    @JoinColumn(name = "user_id") //외래키
    private User user;

    @Column(nullable = false)
    private LocalDate writeDate = LocalDate.now(); //현재 날짜 자동 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_crop_id")
    UserCrop userCrop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    Task task;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder
    public FarmDiary(long id, User user, LocalDate writeDate, UserCrop userCrop, Task task, String content) {
        this.id = id;
        this.user = user;
        if (writeDate != null) {
            this.writeDate = writeDate;
        }
        this.userCrop = userCrop;
        this.task = task;
        this.content = content;
    }

    public void update(LocalDate writeDate, UserCrop userCrop, Task task, String content) {
        if (writeDate != null) this.writeDate = writeDate;
        if (userCrop != null) this.userCrop = userCrop;
        if (task != null) this.task = task;
        if (content != null) this.content = content;
    }
}
