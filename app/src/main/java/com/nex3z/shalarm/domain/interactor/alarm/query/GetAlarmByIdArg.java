package com.nex3z.shalarm.domain.interactor.alarm.query;

public class GetAlarmByIdArg {
    private final long mId;

    public GetAlarmByIdArg(long id) {
        mId = id;
    }

    public long getId() {
        return mId;
    }
}
