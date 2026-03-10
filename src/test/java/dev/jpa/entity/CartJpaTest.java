package dev.jpa.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CartJpaTest {

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
    void 장바구니_항목을_추가하고_합계를_계산한다() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Menu menu = new Menu(1L, "치킨", "후라이드", 18000);
        em.persist(menu);

        MenuOption option = new MenuOption(menu, "사이드", "치즈볼", 3000);
        em.persist(option);

        Cart cart = Cart.create(10L, 1L, menu, option, 2);
        em.persist(cart);

        em.flush();
        em.clear();

        Cart savedCart = em.find(Cart.class, cart.getId());
        assertNotNull(savedCart.getId());
        assertEquals(2, savedCart.getQuantity());
        assertEquals(42000, savedCart.calculateLinePrice());

        em.getTransaction().rollback();
        em.close();
    }

    @Test
    void 장바구니_수량은_1이상이어야_한다() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Menu menu = new Menu(1L, "분식", "떡볶이", 5000);
        em.persist(menu);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> Cart.create(99L, 1L, menu, null, 0));

        assertEquals("quantity must be greater than zero", exception.getMessage());

        em.getTransaction().rollback();
        em.close();
    }
}
