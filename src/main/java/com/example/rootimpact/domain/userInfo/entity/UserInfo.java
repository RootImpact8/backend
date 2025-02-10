package com.example.rootimpact.domain.userInfo.entity;

import com.example.rootimpact.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // ✅ 이름은 UserInfo에서만 관리

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


}
