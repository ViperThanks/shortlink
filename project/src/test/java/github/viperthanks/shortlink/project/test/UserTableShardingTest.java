package github.viperthanks.shortlink.project.test;

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
            create table t_link_%d
            (
                id              bigint auto_increment comment 'ID'
                    primary key,
                domain          varchar(128)                   null comment '域名',
                short_uri       varchar(8) collate utf8mb4_bin null comment '短链接',
                full_short_url  varchar(128)                   null comment '完整短链接',
                origin_url      varchar(1024)                  null comment '原始链接',
                click_num       int default 0                  null comment '点击量',
                gid             varchar(32)                    null comment '分组标识',
                enable_status   tinyint(1)                     null comment '启用标识 0：未启用 1：已启用',
                created_type    tinyint(1)                     null comment '创建类型 0：控制台 1：接口',
                valid_date_type tinyint(1)                     null comment '有效期类型 0：永久有效 1：用户自定义',
                valid_date      datetime                       null comment '有效期',
                `describe`      varchar(1024)                  null comment '描述',
                create_time     datetime                       null comment '创建时间',
                update_time     datetime                       null comment '修改时间',
                del_flag        tinyint(1)                     null comment '删除标识 0：未删除 1：已删除',
                constraint idx_unique_full_short_url
                    unique (full_short_url)
            )
                charset = utf8mb4;
            """;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((DDL_SQL) + "%n", i);
        }
    }
}
