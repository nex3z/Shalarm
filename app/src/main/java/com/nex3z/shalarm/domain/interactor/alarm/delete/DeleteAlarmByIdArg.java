package com.nex3z.shalarm.domain.interactor.alarm.delete;

public class DeleteAlarmByIdArg {
    private final long mId;

    public DeleteAlarmByIdArg(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }
}
