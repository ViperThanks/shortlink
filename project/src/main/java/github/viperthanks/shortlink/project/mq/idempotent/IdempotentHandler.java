package github.viperthanks.shortlink.project.mq.idempotent;

/**
 * desc: 幂等处理器接口
 *
 * @author Viper Thanks
 * @since 20/3/2024
 */
public interface IdempotentHandler {
    /**
     * 判断当前消息是否消费过
     * @param messageId 消息唯一标识
     */
    boolean isMessageProcessed(String messageId);

    /**
     * 如果消息处理异常，删除幂等标识
     * @param messageId 消息唯一标识
     */
    boolean delMessageProcessed(String messageId);
}
