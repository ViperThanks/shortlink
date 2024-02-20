package github.viperthanks.shortlink.admin.toolkit;

/**
 * desc: SQL结果工具类
 *
 * @author Viper Thanks
 * @since 18/2/2024
 */
public final class SQLResultHelper {

    private static final int DML_SUC_MIN_ROW = 0;
    private static final int DQL_SUC_MIN_ROW = 0;
    private static final int COUNT_MIN = 0;

    /**
     * 是否是无效的DML语句
     */
    public static boolean isIllegalDMLResult(int effectRow) {
        return !islLegalDMLResult(effectRow);
    }

    /**
     * 是否是无效的DQL语句
     */
    public static boolean isIllegalDQLResult(int effectRow) {
        return !isLegalDQLResult(effectRow);
    }

    /**
     * 是否是有效的DML语句
     */
    public static boolean islLegalDMLResult(int effectRow) {
        return effectRow > DML_SUC_MIN_ROW;
    }

    /**
     * 是否是有效的DQL语句
     */
    public static boolean isLegalDQLResult(int effectRow) {
        return effectRow > DQL_SUC_MIN_ROW;
    }

    /**
     * 是否是有效的DQL语句
     */
    public static boolean isLegalCountResult(long count) {
        return count > COUNT_MIN;
    }
}
