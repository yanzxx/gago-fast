package vip.xiaonuo.common.util;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.stream.Collectors;

public class CommonPageUtil {

    public static <T, R> Page<T> createPageVO(Page<R> srcPage, Class<T> tagClass) {
        return CommonPageUtil.copyPage(srcPage, tagClass);
    }

    private static <R, T> Page<T> copyPage(Page<R> srcPage, Class<T> tagClass) {
        long current = srcPage.getCurrent();
        long size = srcPage.getSize();
        long total = srcPage.getTotal();


        List<T> tagRecord = srcPage.getRecords().stream().map(record -> BeanUtil.copyProperties(record, tagClass)).collect(Collectors.toList());
        Page<T> page = new Page<>(current, size);
        page.setRecords(tagRecord);
        page.setTotal(total);

        return page;
    }

    public static <R, T> Page<T> createEmptyPage(Page<?> srcPage) {
        long current = srcPage.getCurrent();
        long size = srcPage.getSize();
        long total = srcPage.getTotal();

        Page<T> page = new Page<>(current, size);
        page.setTotal(total);

        return page;
    }
}
