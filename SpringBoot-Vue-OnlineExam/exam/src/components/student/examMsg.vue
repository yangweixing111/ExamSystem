// 点击试卷后的缩略信息
<template>
  <div id="msg">
    <div class="title">
      <span>试卷列表</span>
      <span>/  {{examData.source}}</span>
    </div>
    <div class="wrapper">
      <ul class="top">
        <li class="example">{{examData.source}}</li>
        <li><i class="iconfont icon-pen-"></i></li>
        <li><i class="iconfont icon-share"></i></li>
        <li class="right">
          <div>
            <span class="count">总分</span>
            <span class="score">{{score[0]+score[1]+score[2]}}</span>
          </div>
        </li>
      </ul>
      <ul class="bottom">
        <li>更新于{{examData.examDate}}</li>
        <li>来自 {{examData.institute}}</li>
        <li class="btn">{{examData.type}}</li>
        <li class="right">
          <el-button
            @click="toAnswer(examData.examCode)"
            :disabled="!canEnterExam"
            :type="canEnterExam ? 'primary' : 'info'">
            {{ buttonText }}
          </el-button>
        </li>
      </ul>
      <ul class="info">
        <li @click="dialogVisible = true"><a href="javascript:;"><i class="iconfont icon-info"></i>考生须知</a></li>
      </ul>
    </div>
    <div class="content">
      <el-collapse v-model="activeName" >
        <el-collapse-item class="header" name="0">
          <template slot="title" class="stitle" >
            <div class="title">
              <span>{{examData.source}}</span><i class="header-icon el-icon-info"></i>
              <span class="time">{{examData.totalScore}}分 / {{examData.totalTime}}分钟</span>
              <el-button type="primary" size="small">点击查看试题详情</el-button>
            </div>
          </template>
          <el-collapse class="inner">
            <el-collapse-item>
              <template slot="title" name="1">
                <div class="titlei">选择题 (共{{topicCount[0]}}题 共计{{score[0]}}分)</div>
              </template>
              <div class="contenti">
                <ul class="question" v-for="(list, index) in topic[1]" :key="index">
                  <li>{{index+1}}. {{list.question}} {{list.score}}分</li>
                </ul>
              </div>
            </el-collapse-item>
            <el-collapse-item>
              <template slot="title" name="2">
                <div class="titlei">填空题 (共{{topicCount[1]}}题  共计{{score[1]}}分)</div>
              </template>
              <div class="contenti">
                <ul class="question" v-for="(list, index) in topic[2]" :key="index">
                  <li>{{topicCount[0]+index+1}}.{{list.question}}  {{list.score}}分</li>
                </ul>
              </div>
            </el-collapse-item>
            <el-collapse-item>
              <template slot="title" name="3">
                <div class="titlei">判断题 (共{{topicCount[2]}}题 共计{{score[2]}}分)</div>
              </template>
              <div class="contenti">
                <ul class="question" v-for="(list, index) in topic[3]" :key="index">
                  <li>{{topicCount[0]+topicCount[1]+index+1}}. {{list.question}} {{list.score}}分</li>
                </ul>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-collapse-item>

      </el-collapse>
    </div>
    <!--考生须知对话框-->
    <el-dialog
      title="考生须知"
      :visible.sync="dialogVisible"
      width="30%">
      <span>{{examData.tips}}</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">知道了</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {mapState} from 'vuex'
export default {
  data() {
    return {
      dialogVisible: false,
      activeName: '0',
      topicCount: [],
      score: [],
      examData: {
      },
      topic: {
      },
      examStatus: null,
      examCount: 0,
      maxAttempts: 3,
      canEnterExam: false,
      buttonText: '开始答题',
      isPracticeMode: false
    }
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapState(['isPractice'])
  },
  methods: {
    init() {
      let examCode = this.$route.query.examCode
      let studentId = this.$cookies.get("cid")

      this.isPracticeMode = this.$store.state.isPractice

      this.$axios(`/api/exam/${examCode}`).then(res => {
        res.data.data.examDate = res.data.data.examDate.substr(0,10)
        this.examData = { ...res.data.data}
        let paperId = this.examData.paperId
        this.$axios(`/api/paper/${paperId}`).then(res => {
          if (res.data.code === 200) {
            const data = res.data.data
            this.topic = {...data}

            // 清空之前的数据
            this.topicCount = []
            this.score = []

            let keys = Object.keys(this.topic)
            keys.forEach(e => {
              let questionList = this.topic[e]
              this.topicCount.push(questionList.length)
              let currentScore = 0
              for(let i = 0; i< questionList.length; i++) {
                currentScore += questionList[i].score
              }
              this.score.push(currentScore)
            })
          } else {
            this.$message({
              message: res.data.message || '获取题目失败',
              type: 'error'
            })
          }
        }).catch(err => {
          console.error('请求题目接口失败:', err)
          this.$message({
            message: '获取题目失败，请检查网络连接',
            type: 'error'
          })
        })
      }).catch(err => {
        console.error('请求考试信息接口失败:', err)
        this.$message({
          message: '获取考试信息失败',
          type: 'error'
        })
      })

      if (!this.isPracticeMode) {
        this.checkExamStatus(examCode, studentId)
      } else {
        this.canEnterExam = true
        this.buttonText = '开始练习'
      }
    },
    checkExamStatus(examCode, studentId) {
      this.$axios(`/api/exam/status/${examCode}/${studentId}`).then(res => {
        if (res.data.code === 200) {
          const data = res.data.data
          this.examStatus = data.status
          this.examCount = data.examCount
          this.maxAttempts = data.maxAttempts
          this.canEnterExam = data.canEnter

          if (!this.canEnterExam) {
            if (this.examStatus === 'notStarted') {
              this.buttonText = '考试未开始'
            } else if (this.examStatus === 'expired') {
              this.buttonText = '考试已过期'
            } else if (this.examCount >= this.maxAttempts) {
              this.buttonText = '考试次数已用完'
            }
          } else {
            this.buttonText = `开始答题（剩余${data.remainingAttempts}次机会）`
          }
        }
      }).catch(err => {
        console.log(err)
        this.buttonText = '开始答题'
      })
    },
    toAnswer(id) {
      if (!this.canEnterExam) {
        if (this.examStatus === 'notStarted') {
          this.$message({
            showClose: true,
            type: 'warning',
            message: '考试还未开始，请在考试日期当天再来'
          })
        } else if (this.examStatus === 'expired') {
          this.$message({
            showClose: true,
            type: 'warning',
            message: '考试已过期，无法参加'
          })
        } else if (this.examCount >= this.maxAttempts) {
          this.$message({
            showClose: true,
            type: 'warning',
            message: '您的考试次数已用完（共3次机会）'
          })
        }
        return
      }

      this.$router.push({path:"/answer",query:{examCode: id}})
    },
  }
}
</script>

<style lang="less" scoped>
.bottom {
  .right{
    .el-button{
      color: #409EFF;
      border-color: #c6e2ff;
      background-color: #ecf5ff;
    }
  }
}
.right {
  margin-left: auto;
}
.inner .contenti .question {
  margin-left: 40px;
  color: #9a9a9a;
  font-size: 14px;
}
.content .inner .titlei {
  margin-left: 20px;
  font-size: 16px;
  color: #88949b;
  font-weight: bold;
}
.content .title .time {
  font-size: 16px;
  margin-left: 420px;
  color: #999;
}
.content .stitle {
  background-color: #0195ff;
}
.content .title span {
  margin-right: 10px;
}
#msg .content .title {
  font-size: 20px;
  margin: 0px;
  display: flex;
  align-items: center;
}
.content {
  margin-top: 20px;
  background-color: #fff;
}
.content .header {
  padding: 10px 30px;
}
.wrapper .info {
  margin: 20px 0px 0px 20px;
  border-top: 1px solid #eee;
  padding: 20px 0px 10px 0px;
}
.wrapper .info a {
  color: #88949b;
  font-size: 14px;
}
.wrapper .info a:hover {
  color: #0195ff;
}
.wrapper .bottom .btn {
  cursor: pointer;
  padding: 5px 10px;
  border: 1px solid #88949b;
  border-radius: 4px;
}
.wrapper .bottom {
  display: flex;
  margin-left: 20px;
  color: #999;
  font-size: 14px;
  align-items: center;
}
.wrapper .bottom li {
  margin-right: 14px;
}
#msg {
  background-color: #eee;
  width: 980px;
  margin: 0 auto;
}
#msg .title {
  margin: 20px;
}
#msg .wrapper {
  background-color: #fff;
  padding: 10px;
}
.wrapper .top {
  display: flex;
  margin: 20px;
  align-items: center;
}
.wrapper .top .right {
  margin-left: auto;
}
.wrapper .top .example {
  color: #333;
  font-size: 22px;
  font-weight: 700;
}
.wrapper .top li i {
  margin-left: 20px;
  color: #88949b;
}
.wrapper .right .count {
  margin-right: 60px;
  color: #fff;
  padding: 4px 10px;
  background-color: #88949b;
  border-top-left-radius: 4px;
  border-bottom-left-radius: 4px;
  border: 1px solid #88949b;
}
.wrapper .right .score {
  position: absolute;
  left: 53px;
  top: -5px;
  padding: 1px 12px;
  font-size: 20px;
  color: #88949b;
  border: 1px dashed #88949b;
  border-left: none;
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
  font-weight: bold;
}
.wrapper .right div {
  position: relative;
}
</style>
