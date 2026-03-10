package dev.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderJpaTest {

    EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello-jpa");
    EntityManager manager = factory.createEntityManager();
    EntityTransaction transaction = manager.getTransaction();

    @Test
    @DisplayName("주문을 생성하면 장바구니와 연결되고 총액이 계산된다")
    void testCreateOrderWithCarts() {
        try {
            transaction.begin();

            MenuOption option = requireAnyMenuOption();
            Menu menu = option.getMenu();

            Cart firstCart = Cart.create(5L, menu.getStoreId(), menu, option, 2);
            Cart secondCart = Cart.create(5L, menu.getStoreId(), menu, null, 1);
            manager.persist(firstCart);
            manager.persist(secondCart);

            Order order = Order.create(menu.getStoreId(), 5L, "CARD", "문 앞에 놓아주세요", List.of(firstCart, secondCart));
            manager.persist(order);

            manager.flush();
            manager.clear();

            Order savedOrder = manager.find(Order.class, order.getId());
            assertEquals(10000, savedOrder.getTotalPrice());
            assertEquals(2, savedOrder.getCarts().size());
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    @DisplayName("다른 가게 장바구니는 주문에 추가할 수 없다")
    void testRejectCartFromDifferentStore() {
        try {
            transaction.begin();

            Menu menu = requireAnyMenu();

            Cart cart = Cart.create(3L, menu.getStoreId(), menu, null, 1);

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> Order.create(menu.getStoreId() + 1L, 3L, "CARD", null, List.of(cart)));

            assertEquals("장바구니의 가게와 주문의 가게가 일치하지 않습니다", exception.getMessage());
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    @DisplayName("주문을 생성하면 장바구니에 order가 연결된다")
    void testAssignOrderToCart() {
        try {
            transaction.begin();

            Menu menu = requireAnyMenu();

            Cart cart = Cart.create(21L, menu.getStoreId(), menu, null, 1);
            manager.persist(cart);

            Order order = Order.create(menu.getStoreId(), 21L, "CARD", null, List.of(cart));
            manager.persist(order);

            manager.flush();
            manager.clear();

            Cart savedCart = manager.find(Cart.class, cart.getId());
            assertNotNull(savedCart.getOrder());
            assertEquals(order.getId(), savedCart.getOrder().getId());
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    private Menu requireAnyMenu() {
        List<Menu> menus = manager.createQuery("select m from Menu m order by m.menuId", Menu.class)
                .setMaxResults(1)
                .getResultList();
        if (menus.isEmpty()) {
            throw new IllegalStateException("이 테스트를 실행하기 전에 menus 테이블에 최소 1개 이상의 데이터가 있어야 합니다");
        }
        return menus.get(0);
    }

    private MenuOption requireAnyMenuOption() {
        List<MenuOption> options = manager.createQuery(
                        "select mo from MenuOption mo join fetch mo.menu order by mo.menuOptionId",
                        MenuOption.class)
                .setMaxResults(1)
                .getResultList();
        if (options.isEmpty()) {
            throw new IllegalStateException("이 테스트를 실행하기 전에 menu_options 테이블에 최소 1개 이상의 데이터가 있어야 합니다");
        }
        return options.get(0);
    }
}
