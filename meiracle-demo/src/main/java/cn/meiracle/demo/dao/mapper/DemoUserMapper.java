package cn.meiracle.demo.dao.mapper;

import cn.meiracle.demo.domain.DemoUser;
import cn.meiracle.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * DemoUser 数据访问层。继承公共层 BaseMapperPlus&lt;T, V&gt; 获得通用 CRUD 能力；
 * 复杂 SQL 写在 resources/mapper/DemoUserMapper.xml。
 */
@Mapper
public interface DemoUserMapper extends BaseMapperPlus<DemoUser, DemoUser> {

    /**
     * 按 ID 查询（XML 实现，演示 dao/mapper + xml 的组织方式）
     */
    DemoUser selectByIdXml(Long id);

}
