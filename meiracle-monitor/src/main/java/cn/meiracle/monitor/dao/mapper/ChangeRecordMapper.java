package cn.meiracle.monitor.dao.mapper;

import cn.meiracle.monitor.domain.ChangeRecord;
import cn.meiracle.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 变动追踪数据访问层。
 */
@Mapper
public interface ChangeRecordMapper extends BaseMapperPlus<ChangeRecord, ChangeRecord> {

}
