package com.exam.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Score;

import java.util.List;

public interface ScoreService {
    int add(Score score);

    IPage<Score> findAll(Page page);

    List<Score> findAllList();

    IPage<Score> findById(Page page, Integer studentId);

    List<Score> findById(Integer studentId);

    List<Score> findByExamCode(Integer examCode);

    int getExamCount(Integer examCode, Integer studentId);

    int getMaxScore(Integer examCode, Integer studentId);
}
