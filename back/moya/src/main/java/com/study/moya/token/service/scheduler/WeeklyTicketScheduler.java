package com.study.moya.token.service.scheduler;

import com.study.moya.admin.service.AdminTicketService;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.enums.TicketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyTicketScheduler {

    private final MemberRepository memberRepository;
    private final AdminTicketService adminTicketService;
    
    private static final int WEEKLY_ROADMAP_TICKETS = 10;
    private static final int WEEKLY_WORKSHEET_TICKETS = 10;

    /**
     * 매일 오전 9시에 실행되어 가입일 기준 주간 티켓을 지급합니다.
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void distributeWeeklyTickets() {
        log.info("주간 티켓 지급 스케줄러 시작");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minus(7, ChronoUnit.DAYS);
        
        // 가입한 지 1주일 이상 된 모든 활성 회원 조회
        List<Member> eligibleMembers = memberRepository.findByStatus(MemberStatus.ACTIVE);
        
        int processedCount = 0;
        int skippedCount = 0;
        
        for (Member member : eligibleMembers) {
            try {
                if (shouldReceiveWeeklyTickets(member, now)) {
                    distributeTicketsToMember(member);
                    processedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                log.error("회원 ID: {}에게 주간 티켓 지급 중 오류 발생", member.getId(), e);
            }
        }
        
        log.info("주간 티켓 지급 완료. 지급: {}명, 건너뜀: {}명", processedCount, skippedCount);
    }

    /**
     * 회원이 주간 티켓을 받을 자격이 있는지 확인
     */
    private boolean shouldReceiveWeeklyTickets(Member member, LocalDateTime now) {
        LocalDateTime joinDate = member.getCreatedAt();
        
        // 가입한 지 1주일 미만이면 지급하지 않음
        if (ChronoUnit.DAYS.between(joinDate, now) < 7) {
            return false;
        }
        
        // 가입일로부터 경과된 주 수 계산
        long daysSinceJoin = ChronoUnit.DAYS.between(joinDate, now);
        long weeksSinceJoin = daysSinceJoin / 7;
        
        // 오늘이 해당 주차의 지급일인지 확인 (가입일 기준)
        long todaysSinceJoin = ChronoUnit.DAYS.between(joinDate, now.toLocalDate().atStartOfDay());
        return todaysSinceJoin % 7 == 0 && todaysSinceJoin >= 7;
    }

    /**
     * 특정 회원에게 주간 티켓 지급
     */
    private void distributeTicketsToMember(Member member) {
        Long memberId = member.getId();
        
        log.info("회원 ID: {}에게 주간 티켓 지급 시작", memberId);
        
        try {
            // 로드맵 티켓 지급
            adminTicketService.giveTicketsToMember(memberId, (long) WEEKLY_ROADMAP_TICKETS, TicketType.ROADMAP_TICKET, "주간 자동 지급");
            log.info("회원 ID: {}에게 로드맵 티켓 {}개 지급", memberId, WEEKLY_ROADMAP_TICKETS);
            
            // 워크시트 티켓 지급
            adminTicketService.giveTicketsToMember(memberId, (long) WEEKLY_WORKSHEET_TICKETS, TicketType.WORKSHEET_TICKET, "주간 자동 지급");
            log.info("회원 ID: {}에게 워크시트 티켓 {}개 지급", memberId, WEEKLY_WORKSHEET_TICKETS);
            
            log.info("회원 ID: {}에게 주간 티켓 지급 완료", memberId);
            
        } catch (Exception e) {
            log.error("회원 ID: {}에게 티켓 지급 중 오류 발생", memberId, e);
            throw e;
        }
    }

    /**
     * 수동으로 모든 자격있는 회원에게 티켓 지급 (관리자용)
     */
    @Transactional
    public void manualDistributeWeeklyTickets() {
        log.info("수동 주간 티켓 지급 시작");
        distributeWeeklyTickets();
    }
}