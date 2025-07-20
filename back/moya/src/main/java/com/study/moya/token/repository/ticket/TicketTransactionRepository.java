package com.study.moya.token.repository.ticket;

import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.enums.TransactionType;
import com.study.moya.token.domain.ticket.TicketTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketTransactionRepository extends JpaRepository<TicketTransaction, Long> {

    List<TicketTransaction> findByTicketAccountIdOrderByCreatedAtDesc(Long ticketAccountId);

    List<TicketTransaction> findByTicketAccountIdAndTicketType(Long ticketAccountId, TicketType ticketType);

    List<TicketTransaction> findByTicketAccountIdAndTransactionType(Long ticketAccountId, TransactionType transactionType);

    Page<TicketTransaction> findByTicketAccountIdOrderByCreatedAtDesc(Long ticketAccountId, Pageable pageable);

    List<TicketTransaction> findByTicketAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long ticketAccountId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT tt FROM TicketTransaction tt " +
            "LEFT JOIN FETCH tt.ticketUsage tu " +
            "LEFT JOIN FETCH tu.member " +
            "WHERE tt.ticketAccount.id = :ticketAccountId " +
            "ORDER BY tt.createdAt DESC")
    List<TicketTransaction> findByTicketAccountIdWithUsageOrderByCreatedAtDesc(@Param("ticketAccountId") Long ticketAccountId);

}
