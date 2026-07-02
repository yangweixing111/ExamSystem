<template>
  <div class="exam-questions">
    <el-page-header @back="goBack" :content="`${subject} - 试题预览`"></el-page-header>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="选择题" name="change">
        <el-table :data="changeQuestions" border stripe>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="question" label="题目" min-width="300"></el-table-column>
          <el-table-column label="选项" width="300">
            <template slot-scope="scope">
              <div>A: {{scope.row.answerA}}</div>
              <div>B: {{scope.row.answerB}}</div>
              <div>C: {{scope.row.answerC}}</div>
              <div>D: {{scope.row.answerD}}</div>
            </template>
          </el-table-column>
          <el-table-column prop="rightAnswer" label="正确答案" width="100"></el-table-column>
          <el-table-column prop="score" label="分值" width="80"></el-table-column>
          <el-table-column prop="section" label="章节" width="120"></el-table-column>
        </el-table>
        <div class="summary">
          选择题共 {{changeQuestions.length}} 题，总计 {{changeTotalScore}} 分
        </div>
      </el-tab-pane>

      <el-tab-pane label="填空题" name="fill">
        <el-table :data="fillQuestions" border stripe>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="question" label="题目" min-width="400"></el-table-column>
          <el-table-column prop="answer" label="正确答案" width="200"></el-table-column>
          <el-table-column prop="score" label="分值" width="80"></el-table-column>
          <el-table-column prop="section" label="章节" width="120"></el-table-column>
        </el-table>
        <div class="summary">
          填空题共 {{fillQuestions.length}} 题，总计 {{fillTotalScore}} 分
        </div>
      </el-tab-pane>

      <el-tab-pane label="判断题" name="judge">
        <el-table :data="judgeQuestions" border stripe>
          <el-table-column type="index" label="序号" width="60"></el-table-column>
          <el-table-column prop="question" label="题目" min-width="400"></el-table-column>
          <el-table-column label="正确答案" width="100">
            <template slot-scope="scope">
              {{scope.row.answer === 'T' ? '正确' : '错误'}}
            </template>
          </el-table-column>
          <el-table-column prop="score" label="分值" width="80"></el-table-column>
          <el-table-column prop="section" label="章节" width="120"></el-table-column>
        </el-table>
        <div class="summary">
          判断题共 {{judgeQuestions.length}} 题，总计 {{judgeTotalScore}} 分
        </div>
      </el-tab-pane>
    </el-tabs>

    <div class="total-summary">
      <el-alert
        title="试卷总分统计"
        type="success"
        :closable="false">
        <div>选择题：{{changeTotalScore}} 分 + 填空题：{{fillTotalScore}} 分 + 判断题：{{judgeTotalScore}} 分 = 总计：{{totalScore}} 分</div>
      </el-alert>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeTab: 'change',
      paperId: null,
      subject: '',
      changeQuestions: [],
      fillQuestions: [],
      judgeQuestions: []
    }
  },
  computed: {
    changeTotalScore() {
      return this.changeQuestions.reduce((sum, q) => sum + (q.score || 0), 0)
    },
    fillTotalScore() {
      return this.fillQuestions.reduce((sum, q) => sum + (q.score || 0), 0)
    },
    judgeTotalScore() {
      return this.judgeQuestions.reduce((sum, q) => sum + (q.score || 0), 0)
    },
    totalScore() {
      return this.changeTotalScore + this.fillTotalScore + this.judgeTotalScore
    }
  },
  created() {
    this.getPaperId()
  },
  methods: {
    getPaperId() {
      this.paperId = this.$route.query.paperId
      this.subject = this.$route.query.subject
      if (this.paperId) {
        this.loadQuestions()
      }
    },
    loadQuestions() {
      this.$axios(`/api/paper/${this.paperId}`).then(res => {
        if (res.data.code === 200) {
          const questions = res.data.data
          this.changeQuestions = questions['1'] || []
          this.fillQuestions = questions['2'] || []
          this.judgeQuestions = questions['3'] || []

          console.log('试卷题目加载成功:', {
            change: this.changeQuestions.length,
            fill: this.fillQuestions.length,
            judge: this.judgeQuestions.length
          })
        } else {
          this.$message({
            message: res.data.message || '加载失败',
            type: 'error'
          })
        }
      }).catch(err => {
        console.error('加载试卷题目失败:', err)
        this.$message({
          message: '加载失败，请稍后重试',
          type: 'error'
        })
      })
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="less" scoped>
.exam-questions {
  padding: 20px 40px;

  .el-page-header {
    margin-bottom: 20px;
  }

  .summary {
    margin-top: 15px;
    padding: 10px;
    background-color: #f0f9ff;
    border-left: 4px solid #409eff;
    font-weight: bold;
  }

  .total-summary {
    margin-top: 20px;
  }
}
</style>
