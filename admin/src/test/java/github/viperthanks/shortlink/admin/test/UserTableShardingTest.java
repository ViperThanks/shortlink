package github.viperthanks.shortlink.admin.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 16/2/2024
 */

public class UserTableShardingTest {

    private static final Logger LOG = LoggerFactory.getLogger(UserTableShardingTest.class);


    private static final String DDL_SQL = """
            create table t_user_%d
            (
                id            bigint auto_increment comment 'ID'
                    primary key,
                username      varchar(256) null comment '用户名',
                password      varchar(512) null comment '密码',
                real_name     varchar(256) null comment '真实姓名',
                phone         varchar(128) null comment '手机号',
                mail          varchar(512) null comment '邮箱',
                deletion_time bigint       null comment '注销时间戳',
                create_time   datetime     null comment '创建时间',
                update_time   datetime     not null comment '修改时间',
                del_flag      tinyint(1)   null comment '删除标识 0：未删除 1：已删除',
                constraint idx_username
                    unique (username) comment '用户名唯一索引'
            )
                charset = utf8mb4;""";

    public static void main(String[] args) {
        for (int i = 0 ; i < 16 ; i ++) {
            System.out.printf((DDL_SQL) + "%n", i);
        }
    }
}
