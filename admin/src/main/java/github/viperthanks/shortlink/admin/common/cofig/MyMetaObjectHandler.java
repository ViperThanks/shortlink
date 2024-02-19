package github.viperthanks.shortlink.admin.common.cofig;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Supplier;

/**
 * desc:
 *
 * @author Viper Thanks
 * @since 15/2/2024
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    private static final Supplier<Integer> ZERO_SUPPLIER = () -> 0;
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        this.strictInsertFill(metaObject, "delFlag", ZERO_SUPPLIER, Integer.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
    }
}