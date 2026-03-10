package dev.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// 장바구니 1행이 '메뉴 1개 + 선택 옵션 + 수량'을 의미
@Entity
@Table(name = "carts")
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    // 성능을 위해 fetch = FetchType.LAZY 적용 (연관된 객체를 바로 조회하지 말고 필요할 때까지 미루도록)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_option_id")
    private MenuOption menuOption;

    // 장바구니를 주문으로 넘기기 전에는 nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private int quantity;

    protected Cart() {
    }

    private Cart(Long userId, Long storeId, Menu menu, MenuOption menuOption, int quantity) {
        validateQuantity(quantity);
        this.userId = userId;
        this.storeId = storeId;
        this.menu = menu;
        this.menuOption = menuOption;
        this.quantity = quantity;
    }

    // 생성 시점에 메뉴-가게, 옵션-메뉴 관계가 맞는지 먼저 검증
    public static Cart create(Long userId, Long storeId, Menu menu, MenuOption menuOption, int quantity) {
        if (!menu.getStoreId().equals(storeId)) {
            throw new IllegalArgumentException("메뉴의 가게와 장바구니의 가게가 일치하지 않습니다");
        }
        if (menuOption != null && !menuOption.getMenu().equals(menu)) {
            throw new IllegalArgumentException("해당 메뉴의 옵션만 장바구니에 담을 수 있습니다");
        }
        return new Cart(userId, storeId, menu, menuOption, quantity);
    }

    // 장바구니 수량 변경 기능
    public void changeQuantity(int quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    // 주문 총액 계산 시 사용되는 장바구니 한 줄의 금액 -> (메뉴 가격 + 옵션 가격) * 수량
    public int calculateLinePrice() {
        int optionPrice = menuOption == null ? 0 : menuOption.getPrice();
        return (menu.getPrice() + optionPrice) * quantity;
    }

    void assignOrder(Order order) {
        this.order = order;
    }

    // quantity는 NOT NULL이며 기본값이 1이라서, 도메인 규칙도 1 이상의 값만 허용
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public Menu getMenu() {
        return menu;
    }

    public MenuOption getMenuOption() {
        return menuOption;
    }

    public Order getOrder() {
        return order;
    }

    public int getQuantity() {
        return quantity;
    }
}
