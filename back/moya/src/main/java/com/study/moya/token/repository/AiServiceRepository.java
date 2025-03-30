package com.study.moya.token.repository;

import com.study.moya.token.domain.AiService;
import com.study.moya.token.domain.enums.AiServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiServiceRepository extends JpaRepository<AiService, Long> {
    Optional<AiService> findByServiceNameAndIsActiveTrue(String serviceName);

    List<AiService> findByIsActiveTrueOrderByTokenCostAsc();

    List<AiService> findByServiceTypeAndIsActiveTrue(AiServiceType serviceType);
}