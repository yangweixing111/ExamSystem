<template>
  <section class="index">
    <div class="hello">
      <i class="iconfont icon-xihuan"></i><span>很高兴遇见你,{{user.userName}}老师。</span>
    </div>
    <div class="msg">
      <p class="title">教务公告：</p>
      <ul>
        <li @click="openMsg('qingming')"><i class="iconfont icon-flag"></i>清明节放假通知</li>
        <li @click="openMsg('meeting')"><i class="iconfont icon-flag"></i>下周例行工作报告会</li>
        <li @click="openMsg('defense')"><i class="iconfont icon-flag"></i>关于毕业生答辩具体流程</li>
      </ul>
    </div>
  </section>
</template>

<script>
export default {
  data() {
    return {
      user: { //用户信息
        userName: null,
        userId: null
      }
    }
  },
  created() {
    this.getUserInfo()
  },
  methods: {
    getUserInfo() { //获取用户信息
      let userName = this.$cookies.get("cname")
      let userId = this.$cookies.get("cid")
      this.user.userName = userName
      this.user.userId = userId
    },
    openMsg(type) {
      const messages = {
        qingming: {
          title: '清明节放假通知',
          content: '根据《国务院办公厅关于2026年部分节假日安排的通知》精神，越城区行政服务中心将于4月5日（星期五）至4月7日（星期天）进行清明节放假调休，共3天，放假期间不受理业务。4月8日（星期一）开始正常上班受理业务。望市民朋友相互转告，给您带来不便，敬请谅解。'
        },
        meeting: {
          title: '下周例行工作报告会',
          content: '定于下周一（4月15日）上午9:00在行政楼三楼会议室召开例行工作报告会。请各部门负责人准备好本部门第一季度工作总结报告，准时参会。报告内容应包括：教学工作进展、学生管理情况、科研项目推进等。请各位老师提前安排好工作，按时出席。'
        },
        defense: {
          title: '关于毕业生答辩具体流程',
          content: '2026届本科毕业论文答辩工作安排如下：\n\n一、答辩时间：5月10日-5月15日\n二、答辩地点：各专业教研室\n三、答辩流程：\n1. 学生陈述（5分钟）：介绍论文选题背景、研究内容、创新点等\n2. 教师提问（10分钟）：答辩委员会老师针对论文内容提问\n3. 学生回答（5分钟）：学生回答问题并补充说明\n4. 评议打分：答辩委员会根据论文质量和答辩表现评分\n\n四、注意事项：\n1. 请参加答辩的同学提前15分钟到达答辩地点\n2. 准备好PPT演示文稿和相关材料\n3. 论文终稿一式三份提交给答辩委员会\n4. 着装整洁，态度认真，展现良好精神风貌\n\n请同学们认真准备，祝各位同学答辩顺利！'
        }
      }

      const msg = messages[type]
      if (msg) {
        this.$alert(msg.content, msg.title, {
          confirmButtonText: '确定',
          customClass: 'msg-box'
        })
      }
    }
  }
}
</script>


<style lang="less" scoped>
.index {
  margin-left: 70px;
  .hello {
    font-size: 20px;
    color: #726f70;
    .icon-xihuan {
      font-size: 30px;
      color: #dd6572;
    }
  }
  .msg {
    .title {
      font-size: 16px;
      color: #000;
      margin-top: 20px;
      margin-left: 10px;
    }
    ul {
      display: flex;
      flex-direction: column;
      width: 200px;
      overflow: hidden;
    }
    li {
      margin-top: 10px;
      font-size: 14px;
      color: lightcoral;
      cursor: pointer;
      display: inline-block;
    }
  }
}
</style>

