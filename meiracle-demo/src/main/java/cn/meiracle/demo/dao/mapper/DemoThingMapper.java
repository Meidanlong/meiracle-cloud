package cn.meiracle.demo.dao.mapper;

import cn.meiracle.demo.domain.DemoThing;
import cn.meiracle.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * DemoThing 数据访问层。继承公共层 BaseMapperPlus&lt;T, V&gt; 获得通用 CRUD 与 VO 查询能力。
 */
@Mapper
public interface DemoThingMapper extends BaseMapperPlus<DemoThing, DemoThing> {

}
