package com.example.Demotion.Domain.Insight.Repository;

import com.example.Demotion.Domain.Insight.Entity.PageViewLog;
import com.example.Demotion.Domain.Insight.Entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PageViewLogRepository extends JpaRepository<PageViewLog, Long> {

    // 데모 ID별 페이지별 조회수 집계
    @Query("SELECT p.pageNumber, COUNT(p) FROM PageViewLog p WHERE p.demoId = :demoId GROUP BY p.pageNumber")
    List<Object[]> countPageViewsByDemoId(@Param("demoId") Long demoId);
}

