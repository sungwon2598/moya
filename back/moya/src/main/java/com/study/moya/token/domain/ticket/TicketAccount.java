package com.study.moya.token.domain.ticket;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.exception.ticket.TicketErrorCode;
import com.study.moya.token.exception.ticket.TicketException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket_accounts")
public class TicketAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false)
    private Long roadmapTicketBalance;     // 로드맵 티켓 잔액

    @Column(nullable = false)
    private Long worksheetTicketBalance;   // 워크시트 티켓 잔액

    @Version
    private Long version;  // 동시성 제어

    /**
     * 특정 타입의 티켓 보유 여부 확인
     */
    public boolean hasTicket(TicketType ticketType) {
        return switch (ticketType) {
            case ROADMAP_TICKET -> roadmapTicketBalance > 0;
            case WORKSHEET_TICKET -> worksheetTicketBalance > 0;
        };
    }

    /**
     * 티켓 사용 (잔액 차감)
     */
    public void useTicket(TicketType ticketType) {
        switch (ticketType) {
            case ROADMAP_TICKET -> {
                if (roadmapTicketBalance <= 0)
                    throw TicketException.of(TicketErrorCode.INSUFFICIENT_ROADMAP_TICKET);
                roadmapTicketBalance--;
            }
            case WORKSHEET_TICKET -> {
                if (worksheetTicketBalance <= 0)
                    throw TicketException.of(TicketErrorCode.INSUFFICIENT_WORKSHEET_TICKET);
                worksheetTicketBalance--;
            }
        }
    }

    /**
     * 티켓 추가 (충전/환불 시 사용)
     */
    public void addTicket(TicketType ticketType, Long amount) {
        switch (ticketType) {
            case ROADMAP_TICKET -> roadmapTicketBalance += amount;
            case WORKSHEET_TICKET -> worksheetTicketBalance += amount;
        }
    }
}
