package vip.xiaonuo.dev.modular.job.listener;

import cn.hutool.cron.TaskExecutor;
import cn.hutool.cron.listener.TaskListener;
import cn.hutool.cron.task.CronTask;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CommonJobTaskListener implements TaskListener {


    @Override
    public void onStart(TaskExecutor executor) {
        CronTask cronTask = executor.getCronTask();
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.warn("当前时间{}, id={} 的定时任务开始", time, cronTask.getId());

    }

    @Override
    public void onSucceeded(TaskExecutor executor) {
        CronTask cronTask = executor.getCronTask();
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.warn("当前时间{}, id={} 的定时任务正常结束", time, cronTask.getId());
    }

    @Override
    public void onFailed(TaskExecutor executor, Throwable exception) {
        CronTask cronTask = executor.getCronTask();
        String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        log.error("当前时间{}, id={} 的定时任务异常结束", time, cronTask.getId());
        // TODO: 2023/12/5 可以添加企业微信通知或者邮件通知
    }
}
