package com.dobongsoon.BuyDobong.domain.user.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class UserCascadeDeleteRepository {

    @PersistenceContext
    private EntityManager em;

    // 상점
    public Long findStoreIdByUserId(Long userId) {
        var ids = em.createQuery("select s.id from Store s where s.user.id = :uid", Long.class)
                .setParameter("uid", userId)
                .setMaxResults(1)
                .getResultList();
        return ids.isEmpty() ? null : ids.get(0);
    }

    // 상점 상품
    public int deleteProductsByStoreId(Long storeId) {
        if (storeId == null) return 0;
        return em.createQuery("delete from Product p where p.store.id = :sid")
                .setParameter("sid", storeId)
                .executeUpdate();
    }

    // 사용자 - 최근 본 상점
    public int deleteRecentStoresByStoreId(Long storeId) {
        if (storeId == null) return 0;
        return em.createQuery("delete from RecentStore r where r.store.id = :sid")
                .setParameter("sid", storeId)
                .executeUpdate();
    }

    // 사용자 관련
    public int deletePushSubscriptionsByUserId(Long userId) {
        return em.createQuery("delete from PushSubscription ps where ps.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int deleteNotificationsByUserId(Long userId) {
        return em.createQuery("delete from Notification n where n.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int deleteUserKeywordsByUserId(Long userId) {
        return em.createQuery("delete from UserKeyword uk where uk.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int deleteFavoriteStoresByUserId(Long userId) {
        return em.createQuery("delete from FavoriteStore f where f.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int deleteRecentStoresByUserId(Long userId) {
        return em.createQuery("delete from RecentStore r where r.user.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }

    // Store 삭제
    public int deleteStoreById(Long storeId) {
        if (storeId == null) return 0;
        return em.createQuery("delete from Store s where s.id = :sid")
                .setParameter("sid", storeId)
                .executeUpdate();
    }

    // User 삭제
    public int deleteUserById(Long userId) {
        return em.createQuery("delete from User u where u.id = :uid")
                .setParameter("uid", userId)
                .executeUpdate();
    }
}
