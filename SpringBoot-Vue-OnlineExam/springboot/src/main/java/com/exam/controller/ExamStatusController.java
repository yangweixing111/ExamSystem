package com.exam.controller;

import com.exam.entity.ApiResult;
import com.exam.entity.ExamManage;
import com.exam.serviceimpl.ExamManageServiceImpl;
import com.exam.serviceimpl.ScoreServiceImpl;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ExamStatusController {

    @Autowired
    private ExamManageServiceImpl examManageService;

    @Autowired
    private ScoreServiceImpl scoreService;

    @GetMapping("/exam/status/{examCode}/{studentId}")
    public ApiResult checkExamStatus(@PathVariable("examCode") Integer examCode,
                                      @PathVariable("studentId") Integer studentId) {
        try {
            ExamManage exam = examManageService.findById(examCode);
            if (exam == null) {
                return ApiResultHandler.buildApiResult(404, "考试不存在", null);
            }

            Map<String, Object> result = new HashMap<>();
            String status = getExamStatus(exam);
            int examCount = scoreService.getExamCount(examCode, studentId);

            result.put("status", status);
            result.put("examCount", examCount);
            result.put("maxAttempts", 3);
            result.put("canEnter", canEnterExam(status, examCount));
            result.put("remainingAttempts", 3 - examCount);

            return ApiResultHandler.buildApiResult(200, "请求成功", result);
        } catch (Exception e) {
            return ApiResultHandler.buildApiResult(500, "系统异常：" + e.getMessage(), null);
        }
    }

    private String getExamStatus(ExamManage exam) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String examDateStr = exam.getExamDate();
            Date examDate = sdf.parse(examDateStr);
            Date today = new Date();

            String examDateOnly = sdf.format(examDate);
            String todayOnly = sdf.format(today);

            if (examDateOnly.compareTo(todayOnly) > 0) {
                return "notStarted";
            } else if (examDateOnly.compareTo(todayOnly) < 0) {
                return "expired";
            } else {
                return "ongoing";
            }
        } catch (Exception e) {
            return "expired";
        }
    }

    private boolean canEnterExam(String status, int examCount) {
        if (!"ongoing".equals(status)) {
            return false;
        }
        if (examCount >= 3) {
            return false;
        }
        return true;
    }
}