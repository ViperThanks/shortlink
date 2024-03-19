package github.viperthanks.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import github.viperthanks.shortlink.project.dao.entity.LinkStatsTodayDO;
import github.viperthanks.shortlink.project.dao.mapper.LinkStatsTodayMapper;
import github.viperthanks.shortlink.project.service.LinkStatsTodayService;
import org.springframework.stereotype.Service;

@Service
public class LinkStatsTodayServiceImpl extends ServiceImpl<LinkStatsTodayMapper, LinkStatsTodayDO> implements LinkStatsTodayService {
}