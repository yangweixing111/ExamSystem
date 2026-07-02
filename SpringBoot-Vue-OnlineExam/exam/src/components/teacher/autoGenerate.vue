<template>
  <div class="auto-generate">
    <el-page-header @back="goBack" content="智能组卷"></el-page-header>

    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>组卷配置 - {{subject}}</span>
      </div>

      <el-form :model="form" label-width="150px">
        <el-form-item label="选择题数量">
          <el-input-number v-model="form.changeCount" :min="0" :max="100"></el-input-number>
          <span class="tip">每题 {{form.changeScore}} 分，小计：{{form.changeCount * form.changeScore}} 分</span>
        </el-form-item>

        <el-form-item label="填空题数量">
          <el-input-number v-model="form.fillCount" :min="0" :max="100"></el-input-number>
          <span class="tip">每题 {{form.fillScore}} 分，小计：{{form.fillCount * form.fillScore}} 分</span>
        </el-form-item>

        <el-form-item label="判断题数量">
          <el-input-number v-model="form.judgeCount" :min="0" :max="100"></el-input-number>
          <span class="tip">每题 {{form.judgeScore}} 分，小计：{{form.judgeCount * form.judgeScore}} 分</span>
        </el-form-item>

        <el-divider></el-divider>

        <el-form-item label="试卷总分">
          <el-tag size="medium" type="success">{{totalScore}} 分</el-tag>
          <span v-if="targetScore > 0" :class="totalScore === targetScore ? 'success' : 'warning'">
            （目标：{{targetScore}} 分）
          </span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="generatePaper" :loading="generating">立即组卷</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      paperId: null,
      subject: '',
      targetScore: 100,
      generating: false,
      form: {
        changeCount: 10,
        changeScore: 2,
        fillCount: 10,
        fillScore: 2,
        judgeCount: 10,
        judgeScore: 2
      }
    }
  },
  computed: {
    totalScore() {
      return this.form.changeCount * this.form.changeScore +
             this.form.fillCount * this.form.fillScore +
             this.form.judgeCount * this.form.judgeScore
    }
  },
  created() {
    this.getParams()
  },
  methods: {
    getParams() {
      this.paperId = this.$route.query.paperId
      this.subject = this.$route.query.subject
    },
    generatePaper() {
      if (this.form.changeCount === 0 && this.form.fillCount === 0 && this.form.judgeCount === 0) {
        this.$message({
          message: '请至少设置一种题型的数量',
          type: 'warning'
        })
        return
      }

      this.generating = true
      this.$axios({
        url: '/api/item',
        method: 'post',
        data: {
          changeNumber: this.form.changeCount,
          fillNumber: this.form.fillCount,
          judgeNumber: this.form.judgeCount,
          paperId: this.paperId,
          subject: this.subject
        }
      }).then(res => {
        this.generating = false
        if (res.data.code === 200) {
          this.$message({
            message: '组卷成功',
            type: 'success'
          })
          setTimeout(() => {
            this.$router.push({path: '/viewQuestions', query: {paperId: this.paperId, subject: this.subject}})
          }, 1000)
        } else {
          this.$message({
            message: res.data.message || '组卷失败',
            type: 'error'
          })
        }
      }).catch(() => {
        this.generating = false
        this.$message({
          message: '组卷失败',
          type: 'error'
        })
      })
    },
    resetForm() {
      this.form = {
        changeCount: 10,
        changeScore: 2,
        fillCount: 10,
        fillScore: 2,
        judgeCount: 10,
        judgeScore: 2
      }
    },
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style lang="less" scoped>
.auto-generate {
  padding: 20px 40px;

  .el-page-header {
    margin-bottom: 20px;
  }

  .box-card {
    max-width: 800px;
    margin: 0 auto;
  }

  .tip {
    margin-left: 10px;
    color: #909399;
    font-size: 12px;
  }

  .success {
    color: #67c23a;
    font-weight: bold;
  }

  .warning {
    color: #e6a23c;
    font-weight: bold;
  }
}
</style>
