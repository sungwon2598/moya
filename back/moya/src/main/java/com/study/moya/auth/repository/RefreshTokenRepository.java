package com.study.moya.auth.repository;

import com.study.moya.auth.domain.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByMemberEmail(String memberEmail);
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.memberEmail = :memberEmail")
    void deleteByMemberEmail(@Param("memberEmail") String memberEmail);
}
