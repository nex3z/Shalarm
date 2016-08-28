package com.nex3z.shalarm.domain.interactor.alarm.delete;

import com.nex3z.shalarm.domain.executor.PostExecutionThread;
import com.nex3z.shalarm.domain.executor.ThreadExecutor;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.repository.AlarmRepository;

import rx.Observable;

public class DeleteAlarmById extends UseCase<DeleteAlarmByIdArg> {
    private final String LOG_TAG = DeleteAlarmById.class.getSimpleName();

    private final AlarmRepository mRepository;

    public DeleteAlarmById(AlarmRepository repository, ThreadExecutor threadExecutor,
                        PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);

        mRepository = repository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (mArg == null) {
            throw new IllegalArgumentException("mArg cannot be null");
        }
        return mRepository.deleteAlarm(mArg.getId());
    }
}
