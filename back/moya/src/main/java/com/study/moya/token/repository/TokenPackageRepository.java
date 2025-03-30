package com.study.moya.token.repository;

import com.study.moya.token.domain.TokenPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenPackageRepository extends JpaRepository<TokenPackage, Long> {
    List<TokenPackage> findByIsActiveTrue();

    List<TokenPackage> findByIsActiveTrueOrderByPriceAsc();
}