package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.StepViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * StepViewLogRepository
 * - stepId 기준 조회수 / 평균 체류 시간 통계
 * - demoId 기준으로 스텝별 통계 조회
 */
public interface StepViewLogRepository extends JpaRepository<StepViewLog, Long> {

    // ✅ 1. stepId 목록 기반 조회수
    @Query("SELECT s.stepId, COUNT(s) FROM StepViewLog s WHERE s.stepId IN :stepIds GROUP BY s.stepId")
    List<Object[]> countByStepIds(@Param("stepIds") List<Long> stepIds);

    // ✅ 2. stepId 목록 기반 평균 체류 시간
    @Query("SELECT s.stepId, AVG(s.duration) FROM StepViewLog s WHERE s.stepId IN :stepIds GROUP BY s.stepId")
    List<Object[]> averageDurationByStepIds(@Param("stepIds") List<Long> stepIds);

    // ✅ 3. demoId 기반 통합 통계 (스텝별 조회수 + 평균 체류 시간)
    @Query("""
        SELECT s.stepId, COUNT(s), AVG(s.duration)
        FROM StepViewLog s
        WHERE s.stepId IN (
            SELECT st.id FROM Step st WHERE st.demoId = :demoId
        )
        GROUP BY s.stepId
    """)
    List<Object[]> findStatsByDemoId(@Param("demoId") Long demoId);
}
