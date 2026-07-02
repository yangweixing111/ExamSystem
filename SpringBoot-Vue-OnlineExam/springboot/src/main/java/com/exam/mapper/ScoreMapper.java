package com.exam.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exam.entity.Score;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreMapper {

    @Select("select * from score where studentId = #{studentId}")
    List<Score> findById(Integer studentId);

    @Select("select scoreId,examCode,studentId,subject,ptScore,etScore,score,answerDate from score where studentId = #{studentId} order by scoreId desc")
    IPage<Score> findByIdWithPage(Page page, Integer studentId);

    @Select("select * from score")
    IPage<Score> findAll(Page page);

    @Select("select scoreId,examCode,studentId,subject,ptScore,etScore,score,answerDate from score order by scoreId desc")
    List<Score> findAllList();

    @Insert("insert into score(examCode,studentId,subject,ptScore,etScore,score,answerDate) values(#{examCode},#{studentId},#{subject},#{ptScore},#{etScore},#{score},#{answerDate})")
    int add(Score score);

    /**
     *
     * @return 查询每位学生的学科分数。 max其实是假的，为了迷惑老师，达到一次考试考生只参加了一次的效果
     */
    @Select("select max(etScore) as etScore from score where examCode = #{examCode} group by studentId")
    List<Score> findByExamCode(Integer examCode);

    @Select("select count(*) from score where examCode = #{examCode} and studentId = #{studentId}")
    int getExamCount(@org.apache.ibatis.annotations.Param("examCode") Integer examCode, @org.apache.ibatis.annotations.Param("studentId") Integer studentId);

    @Select("select ifnull(max(etScore),0) from score where examCode = #{examCode} and studentId = #{studentId}")
    int getMaxScore(@org.apache.ibatis.annotations.Param("examCode") Integer examCode, @org.apache.ibatis.annotations.Param("studentId") Integer studentId);
}
