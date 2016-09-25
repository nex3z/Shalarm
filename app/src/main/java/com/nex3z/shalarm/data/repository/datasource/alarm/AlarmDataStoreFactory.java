package com.nex3z.shalarm.data.repository.datasource.alarm;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlarmDataStoreFactory {

    @Inject
    public AlarmDataStoreFactory() {}

    public AlarmDataStore createContentProviderDataStore() {
        return new ContentProviderDataStore();
    }
}
