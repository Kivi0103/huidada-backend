# 数据库初始化
-- 创建库
create database if not exists huidada_data_base;

-- 切换库
use huidada_data_base;

-- 创建表
CREATE TABLE user
(
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户id',
    user_name    VARCHAR(128) NOT NULL UNIQUE COMMENT '用户名',
    password     VARCHAR(256) NOT NULL COMMENT '用户密码',
    head_picture VARCHAR(1024) COMMENT '用户头像，关联到cos存储地址',
    role         VARCHAR(32)      NOT NULL DEFAULT 0 COMMENT '用户权限、user为普通用户，admin为管理员，ban为禁止人员',
    union_id     VARCHAR(256) COMMENT '微信开放平台id',
    mp_open_id   VARCHAR(256) COMMENT '公众号openId',
    create_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete    TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除、0表示未删除，1表示删除',
    PRIMARY KEY (id)
) COMMENT ='用户表' ENGINE = InnoDB
                    collate = utf8mb4_unicode_ci;

CREATE TABLE test_paper
(
    id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '试卷id',
    test_name        VARCHAR(256) NOT NULL DEFAULT 'test_paper' UNIQUE COMMENT '试卷名称',
    description      TEXT COMMENT '试卷描述',
    question_content TEXT         NOT NULL COMMENT '题目内容，每道题由题目、选项key，选项值构成',
    is_ai            TINYINT      NOT NULL DEFAULT 0 COMMENT '是否为ai生成试卷题目，0表示自定义的试卷题目，1表示ai试卷',
    user_id          BIGINT       NOT NULL COMMENT '试卷创建人id',
    user_name        VARCHAR(128) NOT NULL COMMENT '试卷创建人名称',
    bg_picture       VARCHAR(1024) COMMENT '试卷封面背景图，关联到cos存储地址',
    type             TINYINT      NOT NULL DEFAULT 0 COMMENT '试卷类型，0表示打分类，1表示测评类',
    scoring_strategy TINYINT      NOT NULL DEFAULT 0 COMMENT '评分策略类型，0表示用户自定义的评分策略，1表示ai生成的评分策略',
    create_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete        TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除、0表示未删除，1表示删除',
    PRIMARY KEY (id)
) COMMENT ='试卷表' ENGINE = InnoDB
                    collate = utf8mb4_unicode_ci;

CREATE TABLE scoring_result
(
    id                 BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评分策略id',
    result_name        VARCHAR(128) NOT NULL COMMENT '结果名称',
    result_desc        TEXT COMMENT '结果描述',
    result_prop        VARCHAR(128) COMMENT '结果属性集合 JSON，如 [I,S,T,J]，用于测评类试卷的匹配',
    result_picture     VARCHAR(1024) COMMENT '结果图片、创建用户上传、存在cos中的地址',
    result_score_range INT COMMENT '结果得分范围，用于打分类试卷匹配结果，如 80，表示 80及以上的分数命中此结果',
    test_paper_id      BIGINT       NOT NULL COMMENT '该评分结果所属试卷id',
    user_id            BIGINT       NOT NULL COMMENT '试卷创建人id',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除、0表示未删除，1表示删除',
    PRIMARY KEY (id)
) COMMENT ='评分结果表' ENGINE = InnoDB
                        collate = utf8mb4_unicode_ci;

CREATE TABLE user_answer
(
    id                BIGINT   NOT NULL AUTO_INCREMENT COMMENT '用户答案id',
    test_paper_id     BIGINT   NOT NULL COMMENT '所属试卷id',
    scoring_result_id BIGINT COMMENT '答案命中的评分结果id，有可能为null，因为采用ai评分',
    scoring_type      TINYINT  NOT NULL DEFAULT 0 COMMENT '用户选择的打分策略，默认采用自定义打分策略，1表示ai评分',
    choices           TEXT     NOT NULL COMMENT '用户答案（JSON 数组：[A,B,A,C...]）',
    score             INT COMMENT '答案得分，评分类题目会产生',
    user_id           BIGINT   NOT NULL COMMENT '作答人id',
    create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete         TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除、0表示未删除，1表示删除',
    PRIMARY KEY (id)
) COMMENT ='用户答案表' ENGINE = InnoDB
                        collate = utf8mb4_unicode_ci;


