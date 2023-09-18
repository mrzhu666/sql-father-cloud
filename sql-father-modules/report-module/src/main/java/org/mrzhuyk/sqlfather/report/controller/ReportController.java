package org.mrzhuyk.sqlfather.report.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mrzhuyk.sqlfather.core.annotation.AuthCheck;
import org.mrzhuyk.sqlfather.core.constant.CommonConstant;
import org.mrzhuyk.sqlfather.core.dto.DeleteRequest;
import org.mrzhuyk.sqlfather.core.entity.Result;
import org.mrzhuyk.sqlfather.core.exception.BizException;
import org.mrzhuyk.sqlfather.core.exception.ErrorEnum;
import org.mrzhuyk.sqlfather.dict.feign.DictClient;
import org.mrzhuyk.sqlfather.dict.po.Dict;
import org.mrzhuyk.sqlfather.report.dto.ReportAddRequest;
import org.mrzhuyk.sqlfather.report.dto.ReportQueryRequest;
import org.mrzhuyk.sqlfather.report.dto.ReportUpdateRequest;
import org.mrzhuyk.sqlfather.report.enums.ReportStatusEnum;
import org.mrzhuyk.sqlfather.report.po.Report;
import org.mrzhuyk.sqlfather.report.service.ReportService;
import org.mrzhuyk.sqlfather.sql.constant.UserConstant;
import org.mrzhuyk.sqlfather.sql.vo.UserVO;
import org.mrzhuyk.sqlfather.user.feign.UserClient;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@Api("举报服务")
@RestController
@RequestMapping("/report")
public class ReportController {
    @Resource
    private ReportService reportService;
    
    @Resource
    private DictClient dictClient;
    
    @Resource
    private UserClient userClient;
    
    // region 增删改查
    /**
     * 添加
     * @param reportAddRequest
     * @return
     */
    @ApiOperation("添加举报")
    @PostMapping("/add")
    public Result<Long> addReport(@RequestBody ReportAddRequest reportAddRequest) {
        if (reportAddRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        Dict dictById = dictClient.getDictById(reportAddRequest.getReportedId());
        if (dictById == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR,"举报对象不存在");
        }
        Report report = new Report();
        BeanUtils.copyProperties(reportAddRequest,report);
        reportService.validReport(report, true);
        report.setUserId(loginUser.getId());
        report.setReportedUserId(dictById.getUserId());
        report.setStatus(ReportStatusEnum.DEFAULT.getValue());
        boolean save = reportService.save(report);
        if (!save) {
            throw new BizException(ErrorEnum.OPERATION_ERROR);
        }
        Long id = report.getId();
        return Result.success(id);
    }
    
    /**
     * 删除举报
     * @param deleteRequest
     * @return
     */
    @ApiOperation("删除举报")
    @PostMapping("/delete")
    public Result<Boolean> deleteReport(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        UserVO loginUser = userClient.getLoginUser();
        if (loginUser == null) {
            throw new BizException(ErrorEnum.NOT_LOGIN_ERROR);
        }
        Long id = deleteRequest.getId();
        Report report = reportService.getById(id);
        if (report == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        // 本人或管理员可删除
        if (!report.getId().equals(loginUser.getId()) && !loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BizException(ErrorEnum.NO_AUTH_ERROR);
        }
        boolean b = reportService.removeById(id);
        return Result.success(b);
    }
    
    /**
     * 更新（仅管理员）
     *
     * @param reportUpdateRequest
     * @return
     */
    @ApiOperation("更新举报(仅管理员)")
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public Result<Boolean> updateReport(@RequestBody ReportUpdateRequest reportUpdateRequest) {
        if (reportUpdateRequest == null || reportUpdateRequest.getId() <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Report report = new Report();
        BeanUtils.copyProperties(reportUpdateRequest, report);
        reportService.validReport(report,false);
        
        Report oldReport = reportService.getById(report.getId());
        if (oldReport == null) {
            throw new BizException(ErrorEnum.NOT_FOUND_ERROR);
        }
        boolean b = reportService.updateById(report);
        return Result.success(b);
    }
    
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id获取")
    @GetMapping("/get")
    public Result<Report> getReportById(long id) {
        if (id <= 0) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Report report = reportService.getById(id);
        return Result.success(report);
    }
    
    /**
     * 获取列表（仅管理员可使用）
     *
     * @param reportQueryRequest
     * @return
     */
    @ApiOperation("获取列表（仅管理员可使用）")
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public Result<List<Report>> listReport(ReportQueryRequest reportQueryRequest) {
        Report reportQuery = new Report();
        if (reportQueryRequest != null) {
            BeanUtils.copyProperties(reportQueryRequest, reportQuery);
        }
        QueryWrapper<Report> queryWrapper = new QueryWrapper<>(reportQuery);
        List<Report> reportList = reportService.list(queryWrapper);
        return Result.success(reportList);
    }
    /**
     * 分页获取列表
     *  获取用户的举报
     * @param reportQueryRequest
     * @return
     */
    @ApiOperation("获取用户举报")
    @GetMapping("/list/page")
    public Result<Page<Report>> listReportByPage(ReportQueryRequest reportQueryRequest) {
        if (reportQueryRequest == null) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        Report report = new Report();
        BeanUtils.copyProperties(reportQueryRequest,report);
        long current = reportQueryRequest.getCurrent();
        long pageSize = reportQueryRequest.getPageSize();
        String sortField = reportQueryRequest.getSortField();
        String sortOrder = reportQueryRequest.getSortOrder();
        // content 需支持模糊搜索
        String content = report.getContent();
        report.setContent(null);
        // 限制爬虫
        if (pageSize > 50) {
            throw new BizException(ErrorEnum.PARAMS_ERROR);
        }
        
        QueryWrapper<Report> reportQueryWrapper = new QueryWrapper<>(report);
        reportQueryWrapper.like(StringUtils.isNotBlank(content),"content",content);  // 模糊查询
        reportQueryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        Page<Report> page = reportService.page(new Page<>(current, pageSize), reportQueryWrapper);
        return Result.success(page);
    }
    
    //endregion
    
}
