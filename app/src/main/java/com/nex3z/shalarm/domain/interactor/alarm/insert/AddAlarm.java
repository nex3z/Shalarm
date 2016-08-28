package com.nex3z.shalarm.domain.interactor.alarm.insert;

import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.repository.AlarmRepository;

import rx.Observable;

public class AddAlarm extends UseCase<AlarmArg> {
    private final String LOG_TAG = AddAlarm.class.getSimpleName();

    private final AlarmRepository mRepository;

    public AddAlarm(AlarmRepository repository, ThreadExecutor threadExecutor,
                    PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);

        mRepository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null");
        }
        return mRepository.addAlarm(mArg.getAlarm());
    }
}
