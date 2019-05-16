package com.dragon.sdk.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.dragon.sdk.entity.AdminToRole;
import com.dragon.sdk.service.IAdminToRoleService;
import com.dragon.sdk.mapper.AdminToRoleMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.dragon.sdk.util.ComUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dingpengfei
 * @since 2019-05-09
 */
@Service
public class AdminToRoleServiceImpl extends ServiceImpl<AdminToRoleMapper, AdminToRole> implements IAdminToRoleService {

    @Override
//    @Cacheable(value = "AdminToRole",keyGenerator="wiselyKeyGenerator")
    public AdminToRole selectByUserNo(String userNo) {
        EntityWrapper<AdminToRole> ew = new EntityWrapper<>();
        ew.where("user_no={0}", userNo);
        List<AdminToRole> adminToRoleList = this.selectList(ew);
        return ComUtil.isEmpty(adminToRoleList)? null: adminToRoleList.get(0);
    }
}
