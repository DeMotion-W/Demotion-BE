package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.StepViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StepViewLogRepository extends JpaRepository<StepViewLog, Long> {

    @Query("SELECT s.stepId, COUNT(s) FROM StepViewLog s WHERE s.stepId IN :stepIds GROUP BY s.stepId")
    List<Object[]> countByStepIds(List<Long> stepIds);

    @Query("SELECT s.stepId, AVG(s.duration) FROM StepViewLog s WHERE s.stepId IN :stepIds GROUP BY s.stepId")
    List<Object[]> averageDurationByStepIds(List<Long> stepIds);

    // ✅ 데모 ID 기본의 스텝 보기 통계
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