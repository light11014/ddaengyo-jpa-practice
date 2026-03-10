package dev.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrderJpaTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    static void setUp() {
        emf = Persistence.createEntityManagerFactory("test-jpa");
    }

    @AfterAll
    static void tearDown() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void 주문을_생성하면_장바구니와_연결되고_총액이_계산된다() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Menu menu = new Menu(7L, "커피", "아메리카노", 3000, null); // menuPictureUrl 추가
        em.persist(menu);

        MenuOption option = new MenuOption(menu, "사이즈", "라지", 500);
        em.persist(option);

        Cart firstCart = Cart.create(5L, 7L, menu, option, 2);
        Cart secondCart = Cart.create(5L, 7L, menu, null, 1);
        em.persist(firstCart);
        em.persist(secondCart);

        Order order = Order.create(7L, 5L, "CARD", "문 앞에 놓아주세요", List.of(firstCart, secondCart));
        em.persist(order);

        em.flush();
        em.clear();

        Order savedOrder = em.find(Order.class, order.getId());
        assertEquals(10000, savedOrder.getTotalPrice());
        assertEquals(2, savedOrder.getCarts().size());

        em.getTransaction().rollback();
        em.close();
    }

    @Test
    void 다른_가게_장바구니는_주문에_추가할_수_없다() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Menu menu = new Menu(1L, "한식", "비빔밥", 9000, null); // menuPictureUrl 추가
        em.persist(menu);

        Cart cart = Cart.create(3L, 1L, menu, null, 1);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> Order.create(2L, 3L, "CARD", null, List.of(cart)));

        assertEquals("cart store does not match order store", exception.getMessage());

        em.getTransaction().rollback();
        em.close();
    }
}