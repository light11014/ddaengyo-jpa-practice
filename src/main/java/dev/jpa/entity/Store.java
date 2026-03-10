package dev.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {

    public enum StoreType {
        DELIVERY,   // 배달
        TAKEOUT     // 포장
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private StoreType type = StoreType.DELIVERY;

    @Column(name = "category", nullable = false, length = 20)
    private String category;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "content", length = 255)
    private String content;

    @Column(name = "min_delivery_price", nullable = false)
    private Integer minDeliveryPrice;

    @Column(name = "delivery_tip", nullable = false)
    @Builder.Default
    private Integer deliveryTip = 0;

    @Column(name = "min_delivery_time")
    private Integer minDeliveryTime;

    @Column(name = "max_delivery_time")
    private Integer maxDeliveryTime;

    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "dibs_count", nullable = false)
    @Builder.Default
    private Integer dibsCount = 0;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "operation_hours", length = 255)
    private String operationHours;

    @Column(name = "closed_days", length = 255)
    private String closedDays;

    @Column(name = "delivery_address", length = 255)
    private String deliveryAddress;

    // 연관관계
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Dib> dib = new ArrayList<>();
}