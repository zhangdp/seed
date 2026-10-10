package io.github.seed.service.sys.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.mapper.sys.JobLogMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.JobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 定时任务执行日志服务实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class JobLogServiceImpl implements JobLogService {

    private final JobLogMapper jobLogMapper;

    @Override
    public PageData<JobLog> queryPage(PageQuery<JobLogQuery> pageQuery) {
        return jobLogMapper.selectPage(pageQuery);
    }

    @Override
    public boolean save(JobLog jobLog) {
        String message = jobLog.getMessage();
        if (message != null && message.length() > JobLog.MAX_MESSAGE_LENGTH) {
            jobLog.setMessage(message.substring(0, JobLog.MAX_MESSAGE_LENGTH));
        }
        return jobLogMapper.insert(jobLog) > 0;
    }

    @Override
    public int deleteByJobId(Long jobId) {
        return jobLogMapper.deleteByQuery(QueryWrapper.create().eq(JobLog::getJobId, jobId));
    }

    @Override
    public int clear() {
        return jobLogMapper.deleteByQuery(QueryWrapper.create());
    }
}
