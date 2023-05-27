package com.example.expense_tracking_app.application.modules;

import android.content.Context;
import android.content.SharedPreferences;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;

@Module
@InstallIn(ActivityComponent.class)
public class SharedPreferencesModule {

    private static final String SHARED_PREFERENCES_FILE = "com.victor_pan.expense_tracking_app.expenses";

    @Provides
    public SharedPreferences provideSharedPreferences(@ActivityContext Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
    }
}
