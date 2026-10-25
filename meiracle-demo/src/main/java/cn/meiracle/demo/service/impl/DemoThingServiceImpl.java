package cn.meiracle.demo.service.impl;

import cn.meiracle.demo.dao.mapper.DemoThingMapper;
import cn.meiracle.demo.domain.DemoThing;
import cn.meiracle.demo.service.DemoThingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * DemoThing 服务实现。
 */
@Service
public class DemoThingServiceImpl extends ServiceImpl<DemoThingMapper, DemoThing> implements DemoThingService {

}
