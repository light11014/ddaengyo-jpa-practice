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
public class Menu extends BaseEntity{

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





    public Menu(Long storeId, String category, String name, Integer price, String menuPictureUrl) {
        this.storeId = storeId;
        this.category = category;
        this.name = name;
        this.price = price;
        this.menuPictureUrl = menuPictureUrl;
        this.popularity = false;


    }

    public void update(String category, String name, Integer price, String menuPictureUrl) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.menuPictureUrl = menuPictureUrl;

    }

    public void changePopularity(Boolean popularity) {
        this.popularity = popularity;

    }

    public void delete() {
    }
}