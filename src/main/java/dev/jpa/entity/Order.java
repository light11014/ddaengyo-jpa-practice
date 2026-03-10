package dev.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 실제 주문 헤더 정보 -> Cart가 order_id로 연결
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String paymentMethod;

    @Column(nullable = false)
    private int totalPrice;

    @Column(length = 255)
    private String requests;

    // 이 주문에 포함된 장바구니 목록
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private final List<Cart> carts = new ArrayList<>();

    protected Order() {
    }

    private Order(Long storeId, Long userId, String paymentMethod, String requests) {
        this.storeId = storeId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.requests = requests;
        this.totalPrice = 0;
    }

    public static Order create(Long storeId, Long userId, String paymentMethod, String requests, List<Cart> carts) {
        if (carts == null || carts.isEmpty()) {
            throw new IllegalArgumentException("주문에는 최소 한 개 이상의 장바구니 항목이 필요합니다");
        }
        Order order = new Order(storeId, userId, paymentMethod, requests);
        for (Cart cart : carts) {
            order.addCart(cart);
        }
        order.totalPrice = order.carts.stream()
                .mapToInt(Cart::calculateLinePrice)
                .sum();
        return order;
    }

    private void addCart(Cart cart) {
        if (!cart.getUserId().equals(userId)) {
            throw new IllegalArgumentException("장바구니 사용자와 주문 사용자가 일치하지 않습니다");
        }
        if (!cart.getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("장바구니의 가게와 주문의 가게가 일치하지 않습니다");
        }
        carts.add(cart);
        cart.assignOrder(this);
    }

    public Long getId() {
        return id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getRequests() {
        return requests;
    }

    public List<Cart> getCarts() {
        return Collections.unmodifiableList(carts);
    }
}
