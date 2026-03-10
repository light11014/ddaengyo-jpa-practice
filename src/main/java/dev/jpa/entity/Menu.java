package dev.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "menus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long menuId;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(columnDefinition = "TEXT")
    private String menuPictureUrl;

    @Column(nullable = false)
    private Boolean popularity;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime modifiedDate;

    @Column(nullable = false, length = 50)
    private String status;

    public Menu(Long storeId, String category, String name, Integer price, String menuPictureUrl) {
        this.storeId = storeId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.menuPictureUrl = menuPictureUrl;
        this.popularity = false;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
        this.status = "ACTIVE";
    }

    public void update(String category, String name, Integer price, String menuPictureUrl) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.menuPictureUrl = menuPictureUrl;
        this.modifiedDate = LocalDateTime.now();
    }

    public void changePopularity(Boolean popularity) {
        this.popularity = popularity;
        this.modifiedDate = LocalDateTime.now();
    }

    public void delete() {
        this.status = "DELETED";
        this.modifiedDate = LocalDateTime.now();
    }
}