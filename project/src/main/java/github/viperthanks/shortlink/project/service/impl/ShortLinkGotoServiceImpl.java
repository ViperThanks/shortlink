package github.viperthanks.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.project.dao.entity.ShortLinkGotoDO;
import github.viperthanks.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import github.viperthanks.shortlink.project.service.ShortLinkGotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * desc: 短链接路由实现层
 *
 * @author Viper Thanks
 * @since 28/2/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkGotoServiceImpl extends ServiceImpl<ShortLinkGotoMapper, ShortLinkGotoDO> implements ShortLinkGotoService {
}
