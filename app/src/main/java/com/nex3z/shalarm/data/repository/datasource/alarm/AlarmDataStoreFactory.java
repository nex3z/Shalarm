package com.nex3z.shalarm.data.repository.datasource.alarm;

public class AlarmDataStoreFactory {

    public AlarmDataStore createContentProviderDataStore() {
        return new ContentProviderDataStore();
    }
}
