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

class CartJpaTest {

    EntityManagerFactory factory = Persistence.createEntityManagerFactory("hello-jpa");
    EntityManager manager = factory.createEntityManager();
    EntityTransaction transaction = manager.getTransaction();

    @Test
    @DisplayName("장바구니 항목을 추가하고 합계를 계산한다")
    void testSaveCart() {
        try {
            transaction.begin();

            MenuOption option = requireAnyMenuOption();
            Menu menu = option.getMenu();

            Cart cart = Cart.create(10L, menu.getStoreId(), menu, option, 2);
            manager.persist(cart);

            manager.flush();
            manager.clear();

            Cart savedCart = manager.find(Cart.class, cart.getId());
            int expectedLinePrice = (menu.getPrice() + option.getPrice()) * 2;
            assertNotNull(savedCart.getId());
            assertEquals(2, savedCart.getQuantity());
            assertEquals(expectedLinePrice, savedCart.calculateLinePrice());
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    @DisplayName("장바구니 수량은 1 이상이어야 한다")
    void testQuantityMustBePositive() {
        try {
            transaction.begin();

            Menu menu = requireAnyMenu();

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> Cart.create(99L, menu.getStoreId(), menu, null, 0));

            assertEquals("수량은 1 이상이어야 합니다", exception.getMessage());
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    @DisplayName("장바구니 수량을 변경할 수 있다")
    void testChangeQuantity() {
        try {
            transaction.begin();

            Menu menu = requireAnyMenu();

            Cart cart = Cart.create(12L, menu.getStoreId(), menu, null, 1);
            manager.persist(cart);

            cart.changeQuantity(3);
            manager.flush();
            manager.clear();

            Cart savedCart = manager.find(Cart.class, cart.getId());
            int expectedLinePrice = menu.getPrice() * 3;
            assertEquals(3, savedCart.getQuantity());
            assertEquals(expectedLinePrice, savedCart.calculateLinePrice());
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    @Test
    @DisplayName("다른 메뉴의 옵션은 장바구니에 담을 수 없다")
    void testRejectOptionFromDifferentMenu() {
        try {
            transaction.begin();

            Menu firstMenu = requireAnyMenu();
            MenuOption option = requireOptionFromDifferentMenu(firstMenu.getMenuId());

            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> Cart.create(15L, firstMenu.getStoreId(), firstMenu, option, 1));

            assertEquals("해당 메뉴의 옵션만 장바구니에 담을 수 있습니다", exception.getMessage());
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

    private MenuOption requireOptionFromDifferentMenu(Long menuId) {
        List<MenuOption> options = manager.createQuery(
                        "select mo from MenuOption mo join fetch mo.menu m where m.menuId <> :menuId order by mo.menuOptionId",
                        MenuOption.class)
                .setParameter("menuId", menuId)
                .setMaxResults(1)
                .getResultList();
        if (options.isEmpty()) {
            throw new IllegalStateException("이 테스트를 실행하려면 다른 메뉴에 연결된 옵션 데이터가 menu_options 테이블에 있어야 합니다");
        }
        return options.get(0);
    }
}
