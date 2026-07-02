package com.exam.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Score;
import com.exam.mapper.ScoreMapper;
import com.exam.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;
    @Override
    public int add(Score score) {
        return scoreMapper.add(score);
    }

    @Override
    public IPage<Score> findAll(Page page) {
        return scoreMapper.findAll(page);
    }

    @Override
    public List<Score> findAllList() {
        return scoreMapper.findAll(new Page<>(1, Integer.MAX_VALUE)).getRecords();
    }

    @Override
    public IPage<Score> findById(Page page, Integer studentId) {
        return scoreMapper.findByIdWithPage(page, studentId);
    }

    @Override
    public List<Score> findById(Integer studentId) {
        return scoreMapper.findById(studentId);
    }

    @Override
    public List<Score> findByExamCode(Integer examCode) {
        return scoreMapper.findByExamCode(examCode);
    }

    @Override
    public int getExamCount(Integer examCode, Integer studentId) {
        return scoreMapper.getExamCount(examCode, studentId);
    }

    @Override
    public int getMaxScore(Integer examCode, Integer studentId) {
        return scoreMapper.getMaxScore(examCode, studentId);
    }
}
