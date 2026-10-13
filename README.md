# Relily Java Backend

基于 Spring Boot 的 Java 后端服务框架。

## 项目简介

Relily Java Backend 是一个基于 Spring Boot 的后端服务框架，提供 RESTful API 接口，支持前后端分离架构。

## 技术栈

- Java 17+
- Spring Boot 4.x
- Spring Security
- MyBatis Plus
- Redis
- JWT

## 项目结构

```
relily-admin/        # 管理后台模块
relily-common/       # 公共模块
relily-framework/    # 框架核心模块
relily-quartz/       # 定时任务模块
relily-system/       # 系统管理模块
sql/                 # 数据库脚本
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis

### 安装运行

```bash
# 克隆项目
git clone https://github.com/xxx/relily-java.git

# 进入项目目录
cd relily-java

# 编译打包
mvn clean package -Dmaven.test.skip=true

# 运行项目
java -jar relily-admin/target/relily-admin.jar
```

### 数据库初始化

```bash
# 执行 sql 目录下的初始化脚本
mysql -u root -p relily < sql/relily.sql
```

## 配置说明

配置文件位于 `relily-admin/src/main/resources/application.yml`，主要配置项：

- 数据库连接配置
- Redis 连接配置
- JWT 密钥配置
- 文件上传路径配置

## API 文档

启动服务后访问：http://localhost:8080/swagger-ui.html

## 许可证

MIT License
