// 我的考试页面
<template>
  <div id="myExam">
    <div class="title">我的练习</div>
    <div class="wrapper">
      <ul class="top">
        <li class="order" :class="{active: activeStatus === 'all'}" @click="selectStatus('all')">
          <el-badge :value="statusCounts.all" class="item" type="primary">
            <span>全部</span>
          </el-badge>
        </li>
        <li class="order" :class="{active: activeStatus === 'notStarted'}" @click="selectStatus('notStarted')">
          <el-badge :value="statusCounts.notStarted" class="item" type="primary">
            <span>未开始</span>
          </el-badge>
        </li>
        <li class="order" :class="{active: activeStatus === 'ongoing'}" @click="selectStatus('ongoing')">
          <el-badge :value="statusCounts.ongoing" class="item" type="primary">
            <span>已开始</span>
          </el-badge>
        </li>
        <li class="order" :class="{active: activeStatus === 'expired'}" @click="selectStatus('expired')">
          <el-badge :value="statusCounts.expired" class="item">
            <span>已过期</span>
          </el-badge>
        </li>
        <li class="search-li"><div class="icon"><input type="text" placeholder="试卷名称" class="search" v-model="key"><i class="el-icon-search"></i></div></li>
        <li><el-button type="primary" @click="search()">搜索试卷</el-button></li>
      </ul>
      <ul class="paper" v-loading="loading">
        <li class="item" v-for="(item,index) in pagination.records" :key="index">
          <h4 @click="toExamMsg(item.examCode)">{{item.source}}</h4>
          <p class="name">{{item.source}}-{{item.description}}</p>
          <div class="info">
            <i class="el-icon-loading"></i><span>{{item.examDate.substr(0,10)}}</span>
            <i class="iconfont icon-icon-time"></i><span v-if="item.totalTime != null">限时{{item.totalTime}}分钟</span>
            <i class="iconfont icon-fenshu"></i><span>满分{{item.totalScore}}分</span>
          </div>
        </li>
      </ul>
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.current"
          :page-sizes="[10, 20, 40, 60]"
          :page-size="pagination.size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  // name: 'myExam'
  data() {
    return {
      loading: false,
      key: null,
      allExam: null,
      pagination: {
        current: 1,
        total: null,
        size: 10
      },
      activeStatus: 'all',
      statusCounts: {
        all: 0,
        notStarted: 0,
        ongoing: 0,
        expired: 0
      }
    }
  },
  created() {
    console.log('=== startExam组件已创建 ===')
    this.loading = true
    this.getExamInfo()
  },
  methods: {
    async getExamInfo() {
      console.log('=== 开始获取考试信息 ===')
      try {
        const res = await this.$axios('/api/exams')
        console.log('API完整响应:', JSON.stringify(res, null, 2))
        console.log('响应状态码:', res.data.code)
        console.log('响应数据:', res.data.data)

        if (res.data.code === 200) {
          this.allExam = res.data.data || []
          console.log('赋值后的allExam:', this.allExam)
          console.log('allExam长度:', Array.isArray(this.allExam) ? this.allExam.length : '不是数组')

          if (Array.isArray(this.allExam) && this.allExam.length > 0) {
            console.log('第一条考试数据:', this.allExam[0])
          } else {
            console.warn('⚠️ 考试列表为空！')
          }

          this.calculateStatusCounts()
          this.filterByStatus()
        } else {
          console.error('❌ API返回错误:', res.data.message)
          this.$message({
            message: res.data.message || '获取考试信息失败',
            type: 'error'
          })
        }
      } catch (error) {
        console.error('❌ 网络请求失败:', error)
        this.$message({
          message: '网络请求失败，请检查后端是否启动',
          type: 'error'
        })
      } finally {
        this.loading = false
      }
    },
    calculateStatusCounts() {
      if (!this.allExam || this.allExam.length === 0) {
        this.statusCounts.all = 0
        this.statusCounts.notStarted = 0
        this.statusCounts.ongoing = 0
        this.statusCounts.expired = 0
        return
      }

      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const todayStr = `${year}-${month}-${day}`

      let all = this.allExam.length
      let notStarted = 0
      let ongoing = 0
      let expired = 0

      this.allExam.forEach(item => {
        let examDateStr = item.examDate ? item.examDate.substr(0, 10) : ''
        examDateStr = this.normalizeDate(examDateStr)

        if (examDateStr > todayStr) {
          notStarted++
        } else if (examDateStr === todayStr) {
          ongoing++
        } else {
          expired++
        }
      })

      this.statusCounts.all = all
      this.statusCounts.notStarted = notStarted
      this.statusCounts.ongoing = ongoing
      this.statusCounts.expired = expired
    },

    normalizeDate(dateStr) {
      if (!dateStr) return ''

      const parts = dateStr.split('-')
      if (parts.length !== 3) return dateStr

      const year = parts[0]
      const month = parts[1].padStart(2, '0')
      const day = parts[2].padStart(2, '0')

      return `${year}-${month}-${day}`
    },
    filterByStatus() {
      if (!this.allExam) {
        this.pagination.records = []
        this.pagination.total = 0
        return
      }

      const now = new Date()
      const year = now.getFullYear()
      const month = String(now.getMonth() + 1).padStart(2, '0')
      const day = String(now.getDate()).padStart(2, '0')
      const todayStr = `${year}-${month}-${day}`

      console.log('=== filterByStatus 调试 ===')
      console.log('今天日期:', todayStr)
      console.log('当前选中状态:', this.activeStatus)

      let filtered = []

      switch(this.activeStatus) {
        case 'all':
          filtered = this.allExam
          break
        case 'notStarted':
          filtered = this.allExam.filter(item => {
            const examDateStr = this.normalizeDate(item.examDate.substr(0, 10))
            return examDateStr > todayStr
          })
          break
        case 'ongoing':
          filtered = this.allExam.filter(item => {
            const examDateStr = this.normalizeDate(item.examDate.substr(0, 10))
            return examDateStr === todayStr
          })
          break
        case 'expired':
          filtered = this.allExam.filter(item => {
            const examDateStr = this.normalizeDate(item.examDate.substr(0, 10))
            return examDateStr < todayStr
          })
          break
      }

      console.log('过滤后的数量:', filtered.length)

      const start = (this.pagination.current - 1) * this.pagination.size
      const end = start + this.pagination.size
      this.pagination.records = filtered.slice(start, end)
      this.pagination.total = filtered.length

      console.log('当前页记录数:', this.pagination.records.length)
      console.log('总记录数:', this.pagination.total)
    },
    selectStatus(status) {
      this.activeStatus = status
      this.pagination.current = 1
      this.filterByStatus()
    },
    handleSizeChange(val) {
      this.pagination.size = val
      this.filterByStatus()
    },
    handleCurrentChange(val) {
      this.pagination.current = val
      this.filterByStatus()
    },
    search() {
      if (!this.allExam) return

      let filtered = this.allExam

      if (this.key && this.key.trim()) {
        filtered = filtered.filter(item => {
          return item.source.includes(this.key) ||
                 item.description.includes(this.key) ||
                 item.institute.includes(this.key)
        })
      }

      if (this.activeStatus !== 'all') {
        const now = new Date()
        const year = now.getFullYear()
        const month = String(now.getMonth() + 1).padStart(2, '0')
        const day = String(now.getDate()).padStart(2, '0')
        const todayStr = `${year}-${month}-${day}`

        switch(this.activeStatus) {
          case 'notStarted':
            filtered = filtered.filter(item => this.normalizeDate(item.examDate.substr(0, 10)) > todayStr)
            break
          case 'ongoing':
            filtered = filtered.filter(item => this.normalizeDate(item.examDate.substr(0, 10)) === todayStr)
            break
          case 'expired':
            filtered = filtered.filter(item => this.normalizeDate(item.examDate.substr(0, 10)) < todayStr)
            break
        }
      }

      this.pagination.total = filtered.length
      const start = (this.pagination.current - 1) * this.pagination.size
      const end = start + this.pagination.size
      this.pagination.records = filtered.slice(start, end)
    },
    toExamMsg(examCode) {
      this.$router.push({path: '/examMsg', query: {examCode: examCode}})
      console.log(examCode)
    }
  }
}
</script>


<style lang="less" scoped>
.pagination {
  padding: 20px 0px 30px 0px;
  .el-pagination {
    display: flex;
    justify-content: center;
  }
}
.paper {
  h4 {
    cursor: pointer;
  }
}
.paper .item a {
  color: #000;
}
.wrapper .top .order {
  cursor: pointer;
}
.wrapper .top .order:hover {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.wrapper .top .order:visited {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.wrapper .top .order.active {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.item .info i {
  margin-right: 5px;
  color: #0195ff;
}
.item .info span {
  margin-right: 14px;
}
.paper .item {
  width: 380px;
  border-radius: 4px;
  padding: 20px 30px;
  border: 1px solid #eee;
  box-shadow: 0 0 4px 2px rgba(217,222,234,0.3);
  transition: all 0.6s ease;
}
.paper .item:hover {
  box-shadow: 0 0 4px 2px rgba(140, 193, 248, 0.45);
  transform: scale(1.03);
}
.paper .item .info {
  font-size: 14px;
  color: #88949b;
}
.paper .item .name {
  font-size: 14px;
  color: #88949b;
}
.paper * {
  margin: 20px 0;
}
.wrapper .paper {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}
.top .el-icon-search {
  position: absolute;
  right: 10px;
  top: 10px;
}
.top .icon {
  position: relative;
}
.wrapper .top {
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}
#myExam .search-li {
  margin-left: auto;
}
.top .search-li {
  margin-left: auto;
}
.top li {
  display: flex;
  align-items: center;
}
.top .search {
  margin-left: auto;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #eee;
  box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
  transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
}
.top .search:hover {
  color: #0195ff;
  border-color: #0195ff;
}
.wrapper .top {
  display: flex;
}
.wrapper .top li {
  margin: 20px;
}
#myExam {
  width: 980px;
  margin: 0 auto;
}
#myExam .title {
  margin: 20px;
}
#myExam .wrapper {
  background-color: #fff;
}
.wrapper .top .order {
  cursor: pointer;
}
.wrapper .top .order:hover {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.wrapper .top .order:visited {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.wrapper .top .order.active {
  color: #0195ff;
  border-bottom: 2px solid #0195ff;
}
.item .info i {
  margin-right: 5px;
  color: #0195ff;
}
.item .info span {
  margin-right: 14px;
}
.paper .item {
  border-radius: 4px;
  padding: 20px 30px;
  border: 1px solid #eee;
  box-shadow: 0 0 4px 2px rgba(217,222,234,0.3);
  transition: all 0.6s ease;
}
.paper .item:hover {
  box-shadow: 0 0 4px 2px rgba(140, 193, 248, 0.45)
}
.paper .item .info {
  font-size: 14px;
  color: #88949b;
}
.paper .item .name {
  font-size: 14px;
  color: #88949b;
}
.paper * {
  margin: 20px 0;
}
.wrapper .paper {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
}
.top .el-icon-search {
  position: absolute;
  right: 10px;
  top: 10px;
}
.top .icon {
  position: relative;
}
.wrapper .top {
  border-bottom: 1px solid #eee;
}
#myExam .search-li {
  margin-left: auto;
}
.top .search-li {
  margin-left: auto;
}
.top li {
  display: flex;
  align-items: center;
}
.top .search {
  margin-left: auto;
  padding: 10px;
  border-radius: 4px;
  border: 1px solid #eee;
  box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
  transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
}
.top .search:hover {
  color: #0195ff;
  border-color: #0195ff;
}
.wrapper .top {
  display: flex;
}
.wrapper .top li {
  margin: 20px;
}
#myExam {
  width: 980px;
  margin: 0 auto;
}
#myExam .title {
  margin: 20px;
}
#myExam .wrapper {
  background-color: #fff;
}
</style>
