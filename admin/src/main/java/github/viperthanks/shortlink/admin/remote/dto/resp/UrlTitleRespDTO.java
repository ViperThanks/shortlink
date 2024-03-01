package github.viperthanks.shortlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * desc: url标题返回dto
 * @author Viper Thanks
 * @since 1/3/2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlTitleRespDTO {
    private String title;
}
