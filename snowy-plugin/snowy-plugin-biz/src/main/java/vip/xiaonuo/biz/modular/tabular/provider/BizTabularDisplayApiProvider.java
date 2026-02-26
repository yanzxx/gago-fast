
package vip.xiaonuo.biz.modular.tabular.provider;

import org.springframework.stereotype.Service;
import vip.xiaonuo.biz.api.BizTabularDisplayApi;
import vip.xiaonuo.biz.modular.tabular.service.BizTabularDisplayService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 短信API接口提供者
 *
 * @author xuyuxiang
 * @date 2022/6/22 15:33
 **/
@Service
public class BizTabularDisplayApiProvider implements BizTabularDisplayApi {

    @Resource
    private BizTabularDisplayService bizTabularDisplayService;

    @Override
    public Map<String, List<String>> getDisplayColumnMap(String userId) {
        return bizTabularDisplayService.getDisplayColumnMap(userId);
    }
}
