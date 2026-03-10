package dev.jpa.entity;

import jakarta.persistence.*;
import org.junit.jupiter.api.*;

public class UserTest {

    EntityManagerFactory factory
            = Persistence.createEntityManagerFactory("hello-jpa");
    EntityManager manager = factory.createEntityManager();
    EntityTransaction transaction = manager.getTransaction();

    @Test
    @DisplayName("회원가입")
    void testSignUp() {
        transaction.begin();

        User 홍길동 = User.builder()
                .password("1234")
                .name("홍길동")
                .phone("01012345678")
                .email("hong@test.com")
                .build();

        manager.persist(홍길동);
        transaction.commit();

        manager.clear();

        User 조회결과 = manager.find(User.class, 홍길동.getUserId());

        System.out.println(조회결과);
    }

    @Test
    @DisplayName("주소 등록")
    void testRegisterAddress() {
        transaction.begin();

        User 홍길동 = User.builder()
                .password("1234")
                .name("홍길동")
                .phone("01012345678")
                .email("hong@test.com")
                .build();
        manager.persist(홍길동);

        Address 우리fisa = Address.builder()
                .address("서울특별시 마포구 월드컵북로 434")
                .build();

        우리fisa.setUser(홍길동); // 주인쪽에서 세팅 → 양쪽 동기화

        manager.persist(우리fisa);
        transaction.commit();

        manager.clear();

        User 조회결과 = manager.find(User.class, 홍길동.getUserId());
        System.out.println("조회결과 = " + 조회결과);
    }

    @Test
    @DisplayName("주소 조회")
    void testFindAddresses() {
        transaction.begin();

        User 홍길동 = User.builder()
                .password("1234")
                .name("홍길동")
                .phone("01012345678")
                .email("hong@test.com")
                .build();
        manager.persist(홍길동);

        Address 서울집 = Address.builder()
                .address("서울시 강남구 테헤란로 1")
                .build();

        Address 우리fisa = Address.builder()
                .address("서울특별시 마포구 월드컵북로 434")
                .build();


        서울집.setUser(홍길동);
        우리fisa.setUser(홍길동);

        manager.persist(서울집);
        manager.persist(우리fisa);

        transaction.commit();

        User user = manager.find(User.class, 홍길동.getUserId());
        System.out.println("주소 목록 = " + user.getAddresses());
    }
}
