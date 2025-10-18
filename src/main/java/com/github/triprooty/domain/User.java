package com.github.triprooty.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="users")
@SQLDelete(sql = "UPDATE users SET deleted_at = now() WHERE user_id = ?")
@Where(clause = "deleted_at IS NULL")  // 모든 기본 조회에서 자동 제외
public class User {
    @Id
    @GeneratedValue(generator="UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String provider; // ex: "local", "google"

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime signDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean locationTracing = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean alarm = false;

    /** 소프트 삭제 시각: null이면 활성 사용자 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateProfileImageUrl(String profileImage) {
        this.profileImage = profileImage;
    }

    /** 소프트 삭제 수행 */
    public void softDelete() { this.deletedAt = LocalDateTime.now(); }

    /** 복구가 필요하면 */
    public void restore() { this.deletedAt = null; }

}
