
package vip.xiaonuo.sys.modular.index.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import vip.xiaonuo.auth.core.pojo.SaBaseLoginUser;
import vip.xiaonuo.auth.core.util.StpLoginUserUtil;
import vip.xiaonuo.common.pojo.CommonValidList;
import vip.xiaonuo.dev.api.DevLogApi;
import vip.xiaonuo.dev.api.DevMessageApi;
import vip.xiaonuo.sys.modular.index.param.*;
import vip.xiaonuo.sys.modular.index.result.*;
import vip.xiaonuo.sys.modular.index.service.SysIndexService;
import vip.xiaonuo.sys.entity.SysRelation;
import vip.xiaonuo.sys.modular.relation.enums.SysRelationCategoryEnum;
import vip.xiaonuo.sys.modular.relation.service.SysRelationService;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统首页Service接口实现类
 *
 * @author xuyuxiang
 * @date 2022/9/2 10:45
 */
@Service
public class SysIndexServiceImpl implements SysIndexService {

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private DevMessageApi devMessageApi;

    @Resource
    private DevLogApi devLogApi;

    @Override
    public void addSchedule(SysIndexScheduleAddParam sysIndexScheduleAddParam) {
        SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
        sysIndexScheduleAddParam.setScheduleUserId(loginUser.getId());
        sysIndexScheduleAddParam.setScheduleUserName(loginUser.getName());
        sysRelationService.saveRelationWithAppend(loginUser.getId(), sysIndexScheduleAddParam.getScheduleDate(),
                SysRelationCategoryEnum.SYS_USER_SCHEDULE_DATA.getValue(), JSONUtil.toJsonStr(sysIndexScheduleAddParam));
    }

    @Override
    public void deleteSchedule(CommonValidList<SysIndexScheduleIdParam> sysIndexScheduleIdParamList) {
        List<String> scheduleIdList = sysIndexScheduleIdParamList.stream().map(SysIndexScheduleIdParam::getId)
                .collect(Collectors.toList());
        if(ObjectUtil.isNotEmpty(scheduleIdList)) {
            sysRelationService.removeByIds(scheduleIdList);
        }
    }

    @Override
    public List<SysIndexScheduleListResult> scheduleList(SysIndexScheduleListParam sysIndexScheduleListParam) {
        return sysRelationService.list(new LambdaQueryWrapper<SysRelation>().eq(SysRelation::getObjectId, StpUtil.getLoginIdAsString())
                .eq(SysRelation::getTargetId, sysIndexScheduleListParam.getScheduleDate())
                .eq(SysRelation::getCategory, SysRelationCategoryEnum.SYS_USER_SCHEDULE_DATA.getValue()))
                .stream().map(sysRelation -> {
                    SysIndexScheduleListResult sysIndexScheduleListResult = JSONUtil.toBean(sysRelation.getExtJson(), SysIndexScheduleListResult.class);
                    sysIndexScheduleListResult.setId(sysRelation.getId());
                    return sysIndexScheduleListResult;
                }).collect(Collectors.toList());
    }

    @Override
    public List<SysIndexMessageListResult> messageList(SysIndexMessageListParam sysIndexMessageListParam) {
        return devMessageApi.list(CollectionUtil.newArrayList(StpUtil.getLoginIdAsString()),
                ObjectUtil.isEmpty(sysIndexMessageListParam.getLimit())?10:sysIndexMessageListParam.getLimit()).stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, SysIndexMessageListResult.class)).collect(Collectors.toList());
    }

    @Override
    public SysIndexMessageDetailResult messageDetail(SysIndexMessageIdParam sysIndexMessageIdParam) {
        return JSONUtil.toBean(devMessageApi.detail(sysIndexMessageIdParam.getId()), SysIndexMessageDetailResult.class);
    }

    @Override
    public List<SysIndexVisLogListResult> visLogList() {
        return devLogApi.currentUserVisLogList().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, SysIndexVisLogListResult.class)).collect(Collectors.toList());
    }

    @Override
    public List<SysIndexOpLogListResult> opLogList() {
        return devLogApi.currentUserOpLogList().stream()
                .map(jsonObject -> JSONUtil.toBean(jsonObject, SysIndexOpLogListResult.class)).collect(Collectors.toList());
    }
}
