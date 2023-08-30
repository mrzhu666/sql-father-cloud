package org.mrzhuyk.sqlfather.report.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mrzhuyk.sqlfather.report.po.Report;
import org.mrzhuyk.sqlfather.report.service.ReportService;
import org.mrzhuyk.sqlfather.report.mapper.ReportMapper;
import org.springframework.stereotype.Service;

/**
* @author mrzhu
* @description 针对表【report(举报)】的数据库操作Service实现
* @createDate 2023-08-30 22:25:07
*/
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>
    implements ReportService{

}




