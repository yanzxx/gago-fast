
package vip.xiaonuo.biz.api;

import java.util.List;
import java.util.Map;

public interface BizTabularDisplayApi {


    Map<String, List<String>> getDisplayColumnMap(String userId);
}
