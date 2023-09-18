package org.mrzhuyk.sqlfather.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.report.enums.ReportStatusEnum;
import org.mrzhuyk.sqlfather.report.po.Report;
import org.mrzhuyk.sqlfather.report.service.ReportService;
import org.mrzhuyk.sqlfather.report.mapper.ReportMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author mrzhu
* @description 针对表【report(举报)】的数据库操作Service实现
* @createDate 2023-08-30 22:25:07
*/
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>
    implements ReportService{
    @Override
    public void validReport(Report report, boolean add) {
        if (report == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        String content = report.getContent();
        Long reportedId = report.getReportedId();
        Integer status = report.getStatus();
        if (StringUtils.isNotBlank(content) && content.length() > 1024) {
            throw new BizException(ErrorEnum.PARAMS_ERROR, "内容过长");
        }
        if (add) {
            if (reportedId == null || reportedId <= 0) {
                throw new BizException(ErrorEnum.PARAMS_ERROR);
            }
        }else{
            if (status != null && !ReportStatusEnum.valid(status)) {
                throw new BizException(ErrorEnum.PARAMS_ERROR);
            }
        }
        
    }
}




