package com.example.expense_tracking_app.application.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(ActivityComponent.class)
public class ResourcesModule {

    @Provides
    public Resources provideSharedPreferences(@ActivityContext Context context) {
        return context.getResources();
    }
}
