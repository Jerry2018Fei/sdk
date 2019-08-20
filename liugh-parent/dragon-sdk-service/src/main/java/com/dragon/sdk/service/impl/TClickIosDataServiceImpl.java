package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.entity.TClickAndroidData;
import com.dragon.sdk.entity.TClickIosData;
import com.dragon.sdk.entity.TouTiaoAdData;
import com.dragon.sdk.mapper.TClickAndroidDataMapper;
import com.dragon.sdk.mapper.TClickIosDataMapper;
import com.dragon.sdk.service.ITClickAndroidDataService;
import com.dragon.sdk.service.ITClickIosDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 服务实现类
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class TClickIosDataServiceImpl extends ServiceImpl<TClickIosDataMapper, TClickIosData>
    implements ITClickIosDataService {

    @Resource private TClickIosDataMapper mapper;


    @Override
    public boolean insert(TClickIosData entity) {
        return mapper.insert(entity)==1;
    }

    @Override
    public List<TClickIosData> selectByDevice(String idfa) {
        return mapper.selectByDevice(idfa);
    }
}
