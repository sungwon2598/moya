package com.study.moya.token.repository;

import com.study.moya.token.domain.TokenAccount;
import com.study.moya.token.domain.TokenTransaction;
import com.study.moya.token.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TokenTransactionRepository extends JpaRepository<TokenTransaction, Long> {
    Page<TokenTransaction> findByTokenAccountOrderByCreatedAtDesc(TokenAccount tokenAccount, Pageable pageable);

    Page<TokenTransaction> findByTokenAccountIdOrderByCreatedAtDesc(Long tokenAccountId, Pageable pageable);

    List<TokenTransaction> findByTokenAccountAndTransactionTypeAndCreatedAtBetween(
            TokenAccount tokenAccount,
            TransactionType transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<TokenTransaction> findByTokenAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long tokenAccountId,
            LocalDateTime startDate,
            LocalDateTime endDate);
}