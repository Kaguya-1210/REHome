-- REHome数据库初始化脚本

-- 安全提示：在生产环境中，请谨慎使用DROP DATABASE语句
-- 如需保留现有数据，请注释掉下面的DROP语句
DROP DATABASE IF EXISTS REHome;

-- 创建数据库并设置字符集和排序规则
CREATE DATABASE IF NOT EXISTS REHome 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 使用创建的数据库
USE REHome;

-- 创建管理员表（增加密码字段长度以支持现代加密算法）
CREATE TABLE IF NOT EXISTS admin (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    password VARCHAR(255) NOT NULL COMMENT '加密后的密码',
    username VARCHAR(20) COMMENT '管理员用户名',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_account (account) -- 为账号字段添加索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 执行成功提示
SELECT '数据库初始化完成！' AS result;

insert into admin (account, password,username)
values ('admin', '123456','W7insvnter');
