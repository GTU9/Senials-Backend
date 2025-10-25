package com.senials.common.mapper;

import com.senials.report.dto.ReportDTO;
import com.senials.report.entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {

    /* ReportDTO -> Report */
    Report toReport(ReportDTO reportDTO);

    /* Report -> ReportDTO */
    ReportDTO toReportDTO(Report report);
}
