package com.study.moya.token.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.Payment;
import com.study.moya.token.domain.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);

    Page<Payment> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    boolean existsByOrderIdAndPaymentStatus(String orderId, PaymentStatus status);
}