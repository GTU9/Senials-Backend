package com.senials.report.repository;

import com.senials.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    List<Report> findAllByReportTargetType(int reportTargetType, Sort sort);
}
