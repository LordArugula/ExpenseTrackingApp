package com.example.expense_tracking_app.application.modules;

import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ResourcesModule {

    @Provides
    public Resources provideSharedPreferences(@ApplicationContext Context context) {
        return context.getResources();
    }
}
