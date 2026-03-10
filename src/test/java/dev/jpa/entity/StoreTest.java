package dev.jpa.entity;

import dev.jpa.entity.Dib;
import dev.jpa.entity.Store;
import dev.jpa.entity.User;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest{
    private static EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;

    @BeforeAll
    static void initFactory() {
        // "hello-jpa" 설정으로 EMF 생성
        emf = Persistence.createEntityManagerFactory("hello-jpa");
    }

    @AfterAll
    static void closeFactory() {
        // 모든 테스트 끝난 후 EMF 닫기
        emf.close();
    }

    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전 EM 생성, 트랜잭션 시작
        em = emf.createEntityManager();
        tx = em.getTransaction();
        tx.begin();
    }

    @AfterEach
    void tearDown() {
        // 각 테스트 후 롤백
        if (tx.isActive()) tx.rollback();
        em.close();
    }

    // 테스트용 가게 생성 헬퍼
    private Store createStore(String name, String category, Integer deliveryTip, BigDecimal rating, Integer reviewCount) {
        return Store.builder()
                .name(name)
                .type(Store.StoreType.DELIVERY)
                .category(category)
                .address("서울시 강남구")
                .phone("010-1234-5678")
                .minDeliveryPrice(15000)
                .deliveryTip(deliveryTip)
                .rating(rating)
                .reviewCount(reviewCount)
                .build();
    }

    // 테스트용 유저 생성 헬퍼
    private User createUser(String name, String email) {
        return User.builder()
                .password("1234")
                .name(name)
                .phone("01012345678")
                .email(email)
                .build();
    }

    // 1. 가게 등록
    @Test
    @DisplayName("가게 등록 - 저장 후 id로 조회하면 동일한 가게가 반환된다")
    void 가게_등록(){
        Store store = Store.builder()
                .name("맛있는 치킨집")
                .type(Store.StoreType.DELIVERY)
                .category("치킨")
                .address("서울시 강남구")
                .phone("010-1111-2222")
                .minDeliveryPrice(15000)
                .deliveryTip(2000)
                .build();

        em.persist(store);
        em.flush();
        em.clear();
        Store found = em.find(Store.class, store.getStoreId());
        assertNotNull(found); // 조회 결과가 null이 아닌지 확인
        assertEquals("맛있는 치킨집", found.getName()); // 이름이 일치하는지 확인
        assertEquals(Store.StoreType.DELIVERY, found.getType()); // 타입이 일치하는지 확인
        assertEquals("일반", found.getStatus()); // 기본 상태가 '일반'인지 확인
    }
}