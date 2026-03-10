package dev.jpa.entity;

import dev.jpa.entity.Menu;
import dev.jpa.entity.MenuOption;
import dev.jpa.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("메뉴 및 옵션 도메인 테스트")
class MenuTest {

    static EntityManagerFactory emf;
    EntityManager em;
    EntityTransaction tx;

    @BeforeAll
    static void setup() {
        emf = JpaUtil.getEntityManagerFactory();
    }

    @BeforeEach
    void init() {
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void tearDown() {
        if (tx.isActive()) {
            tx.commit();
        }
        em.close();
    }

    @AfterAll
    static void close() {
        if (emf != null) emf.close();
    }

    @Test
    @DisplayName("메뉴와 옵션을 저장하고 연관관계를 조회한다")
    void saveAndFindTest() {
        // [Given] - Menu 생성자 인자 5개 (마지막 "jeyuk.jpg" 포함)
        // 주의: Menu 엔티티 생성자에 String menuPictureUrl이 정의되어 있어야 합니다.
        Menu menu = new Menu(10L, "식사", "제육덮밥", 8000);
        em.persist(menu);

        MenuOption option1 = new MenuOption(menu, "고기 추가", "고기 양 두배", 2000);
        MenuOption option2 = new MenuOption(menu, "곱빼기", "밥 많이", 1000);
        em.persist(option1);
        em.persist(option2);

        em.flush();
        em.clear();

        // [When]
        Menu findMenu = em.find(Menu.class, menu.getMenuId());
        List<MenuOption> options = em.createQuery(
                        "select mo from MenuOption mo where mo.menu.menuId = :menuId", MenuOption.class)
                .setParameter("menuId", findMenu.getMenuId())
                .getResultList();

        // [Then]
        assertEquals("제육덮밥", findMenu.getName());
        assertEquals(2, options.size());
    }

    @Test
    @DisplayName("하드 딜리트를 실행하면 DB에서 데이터가 완전히 삭제된다")
    void hardDeleteTest() {
        // [Given] - 여기도 인자 5개 유지
        Menu menu = new Menu(11L, "분식", "떡볶이", 5000);
        em.persist(menu);

        MenuOption option = new MenuOption(menu, "치즈추가", "체다치즈", 1000);
        em.persist(option);
        em.flush();

        // [When]
        MenuOption findOption = em.find(MenuOption.class, option.getMenuOptionId());
        assertNotNull(findOption);

        em.remove(findOption);
        em.flush();
        em.clear();

        // [Then]
        MenuOption deletedOption = em.find(MenuOption.class, option.getMenuOptionId());
        assertNull(deletedOption);
    }
}