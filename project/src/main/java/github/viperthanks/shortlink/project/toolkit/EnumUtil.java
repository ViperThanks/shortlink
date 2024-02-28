package github.viperthanks.shortlink.project.toolkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import github.viperthanks.shortlink.project.common.enums.IndexDesc;

import java.util.Arrays;
import java.util.Objects;

/**
 * desc: 枚举工具类
 *
 * @author Viper Thanks
 * @since 30/1/2024
 */
public class EnumUtil {
    public static <T extends IndexDesc> ImmutableMap<Integer, T > getMap(T[] values){
        return Maps.uniqueIndex(Arrays.asList(values), IndexDesc::getIndex);
    }

    /**
     * 通过索引找枚举
     */
    public static <T extends IndexDesc> T findByIndex(T[] values, int index) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].getIndex() == index) {
                return values[i];
            }
        }
        return null;
    }

    /**
     * 通过索引找枚举
     */
    public static <T extends IndexDesc> T findByIndex(Class<T> clazz, int index) {
        return findByIndex(clazz.getEnumConstants(), index);
    }

    /**
     * 如果找不到就抛异常咯
     */
    public static <T extends IndexDesc> T findByIndexStrictly(T[] values, int index) {
        return Objects.requireNonNull(findByIndex(values, index));
    }

    /**
     * 如果找不到就抛异常咯
     */
    public static <T extends IndexDesc> T findByIndexStrictly(Class<T> clazz, int index) {
        return Objects.requireNonNull(findByIndex(clazz, index));
    }
}
