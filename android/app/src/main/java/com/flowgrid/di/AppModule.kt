package com.flowgrid.di

import android.content.Context
import androidx.room.Room
import com.flowgrid.data.AppDatabase
import com.flowgrid.data.DataStoreManager
import com.flowgrid.data.GameResultDao
import com.flowgrid.data.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "flowgrid_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameResultDao(appDatabase: AppDatabase): GameResultDao {
        return appDatabase.gameResultDao()
    }
    
    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext appContext: Context): DataStoreManager {
        return DataStoreManager(appContext.dataStore)
    }
}
