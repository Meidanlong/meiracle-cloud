package cn.meiracle.monitor.service.impl;

import cn.meiracle.monitor.dao.mapper.ChangeRecordMapper;
import cn.meiracle.monitor.domain.ChangeRecord;
import cn.meiracle.monitor.service.ChangeRecordService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ChangeRecordServiceImpl extends ServiceImpl<ChangeRecordMapper, ChangeRecord> implements ChangeRecordService {

}
