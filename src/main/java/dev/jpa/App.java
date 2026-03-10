package dev.jpa;

import dev.jpa.entity.Menu;
import dev.jpa.entity.MenuOption;
import dev.jpa.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class App {

    public static void main(String[] args) {

        EntityManagerFactory emf = JpaUtil.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // 1️⃣ 메뉴 저장
            Menu menu = new Menu(
                    1L,
                    "커피",
                    "아메리카노",
                    3000,
                    "americano.jpg"
            );

            em.persist(menu);

            // 2️⃣ 메뉴 옵션 저장
            MenuOption option1 = new MenuOption(
                    menu,
                    "샷 추가",
                    "에스프레소 1샷 추가",
                    500
            );

            MenuOption option2 = new MenuOption(
                    menu,
                    "얼음 적게",
                    "얼음을 적게 넣어드립니다",
                    0
            );

            em.persist(option1);
            em.persist(option2);

            tx.commit();

            System.out.println("===== 저장 완료 =====");
            System.out.println("menuId = " + menu.getMenuId());

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        try {
            tx.begin();

            // 3️⃣ 메뉴 조회
            Menu findMenu = em.find(Menu.class, 1L);

            System.out.println("\n===== 메뉴 조회 =====");
            System.out.println("이름 = " + findMenu.getName());
            System.out.println("가격 = " + findMenu.getPrice());

            // 4️⃣ 옵션 조회
            List<MenuOption> options = em.createQuery(
                            "select mo from MenuOption mo where mo.menu.menuId = :menuId",
                            MenuOption.class)
                    .setParameter("menuId", findMenu.getMenuId())
                    .getResultList();

            System.out.println("\n===== 옵션 목록 =====");

            for (MenuOption o : options) {
                System.out.println(o.getOption() + " / " + o.getPrice());
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        try {
            tx.begin();

            // 5️⃣ 메뉴 수정
            Menu findMenu = em.find(Menu.class, 1L);
            findMenu.update("커피", "아이스 아메리카노", 3500, "ice_americano.jpg");
            findMenu.changePopularity(true);

            // 6️⃣ 옵션 수정
            MenuOption findOption = em.find(MenuOption.class, 1L);
            findOption.update("샷 2번 추가", "에스프레소 2샷", 1000);

            tx.commit();

            System.out.println("\n===== 수정 완료 =====");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        try {
            tx.begin();

            // 7️⃣ 옵션 삭제 (soft delete)
            MenuOption option = em.find(MenuOption.class, 2L);
            option.delete();

            tx.commit();

            System.out.println("\n===== 옵션 삭제 완료 =====");

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }

        em.close();
        emf.close();
    }
}