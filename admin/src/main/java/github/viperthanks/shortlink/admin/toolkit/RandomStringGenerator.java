package github.viperthanks.shortlink.admin.toolkit;

import java.security.SecureRandom;
import java.util.Random;

import static com.google.common.base.Preconditions.checkState;
/**
 * desc: 随机字符串生成器
 *
 * @author Viper Thanks
 * @since 19/2/2024
 */
public final class RandomStringGenerator {
    /**
     * 基本字符表
     */
    private static final char[] AlphabetPlusDigital = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    private static final class RandomHolder{
        private static final SecureRandom SECURE_RANDOM = new SecureRandom();

        private static final Random RANDOM = new Random();

    }

    /**
     * 生成长度为size的字符串，默认随机器为 {@linkplain SecureRandom}
     */
    public static String generate(int size) {
        return generate(size, true);
    }

    /**
     * 生成长度为size的字符串
     * @param size 长度
     */
    public static String generate(int size, boolean isSecure) {
        checkState(size > 0, "size must big than 0");
        int index = AlphabetPlusDigital.length;
        StringBuilder sb = new StringBuilder(size);
        Random random = isSecure ? RandomHolder.SECURE_RANDOM : RandomHolder.RANDOM;
        for (int i = 0; i < size; i++) {
            final int finalIndex = random.nextInt(index);
            sb.append(AlphabetPlusDigital[finalIndex]);
        }
        return sb.toString();
    }


}
