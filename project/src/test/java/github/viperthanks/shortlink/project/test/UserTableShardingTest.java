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
            create table t_link_goto_%d
            (
                id             bigint auto_increment comment 'ID'
                    primary key,
                gid            varchar(32) default 'default' comment '分组标识',
                full_short_url varchar(128) null comment '完整短链接'
            );
            """;

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((DDL_SQL) + "%n", i);
        }
    }
}
