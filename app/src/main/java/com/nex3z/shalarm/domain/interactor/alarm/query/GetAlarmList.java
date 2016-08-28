package com.nex3z.shalarm.domain.interactor.alarm.query;

import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.repository.AlarmRepository;

import rx.Observable;

public class GetAlarmList extends UseCase<GetAlarmListArg> {
    private final String LOG_TAG = GetAlarmList.class.getSimpleName();

    private final AlarmRepository mRepository;

    public GetAlarmList(GetAlarmListArg arg, AlarmRepository repository,
                        ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);

        mRepository = repository;
        mArg = arg;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null");
        }
        return mRepository.getAlarms(mArg.getSortOrder());
    }

}
