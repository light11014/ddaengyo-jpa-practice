package dev.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "menu_options")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name="option_name", nullable = false, length = 255)
    private String option;

    @Column(length = 255)
    private String content;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @Column(nullable = false)
    private String status;

    public MenuOption(Menu menu, String option, String content, Integer price) {
        this.menu = menu;
        this.option = option;
        this.content = content;
        this.price = price;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public void update(String option, String content, Integer price) {
        this.option = option;
        this.content = content;
        this.price = price;
        this.modifiedDate = LocalDateTime.now();
    }

    public void delete() {
        this.status = "DELETED";
        this.modifiedDate = LocalDateTime.now();
    }
}