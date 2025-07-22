package com.study.moya.token.repository.ticket;

import com.study.moya.token.domain.ticket.TicketAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketAccountRepository extends JpaRepository<TicketAccount, Long> {

    Optional<TicketAccount> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

    @Query("SELECT ta FROM TicketAccount ta JOIN FETCH ta.member WHERE ta.member.id = :memberId")
    Optional<TicketAccount> findByMemberIdWithMember(@Param("memberId") Long memberId);

    @Query("SELECT ta FROM TicketAccount ta JOIN FETCH ta.member")
    Page<TicketAccount> findAllWithMember(Pageable pageable);
}
