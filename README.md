# ZabbixWatcher

## 项目简介

利用zabbix提供的api进行二次开发，选取我们关心的、反应系统基础性能的监控项信息，获取它们的数据并进行持久化存储，提供数据查询、grafana的展示和数据下载，以便运维人员及时监控系统，并且可以利用持久化的历史数据进行算法分析，如诊断、预测等。
功能：抓取指定监控项信息；监控项数据持久化存储；数据查询与展示；数据导出与下载
vue.js(前端)+spring boot(后端)+influxdb(数据持久化)

## 项目构建方法

### 环境准备

- nodejs依赖安装
- zabbix监控环境部署 [部署过程](http://github.com/VVphe/zabbix_learning/deployment.md)
- IntelliJ IDEA
- spring boot框架
- vue.js框架

### 获取项目

- get the code from gitlab/github
	> git clone git@github.com:XLab_Tongji/zabbixWatcher.git
    > git clone git@github.com:XLab_Tongji/zabbixWatcherUI.git

### 导入项目

- 打开IntelliJ IDEA
- 选择Import project
- 选择zabbixWatcher

## 项目运行方法

- 运行zabbixWatcher
- 进入zabbixWatcherUI目录下
- 运行前端 `npm run dev`

## 项目基本功能

1. 实现从已部署的zabbix抓取指定监控项信息
2. 实现监控项数据持久化存储
3. 提供web界面指定zabbix和influxdb配置
4. 监控项数据查询与下载

## 代码结构说明

*/images* - the folder contains the document images

*/src* - 项目主要代码

*/src/main/java/com/xlab/config* - swagger、RestTemplate配置类 

*/src/main/java/com/xlab/controller* - 业务逻辑控制层，提供web接口

*/src/main/java/com/xlab/dao* - 数据库操作类

*/src/main/java/com/xlab/entity* - javaBean

*/src/main/java/com/xlab/schedule* - 任务模式component，定时执行方法

*/src/main/java/com/xlab/service* - ZabbixService与ZabbixInfluxService接口和实现类

*/src/test/java/com/xlab* - 测试代码