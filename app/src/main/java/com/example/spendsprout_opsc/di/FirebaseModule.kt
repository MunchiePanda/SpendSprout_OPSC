package com.example.spendsprout_opsc.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase =
        FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    @Named("rootDatabaseReference")
    fun provideRootDatabaseReference(
        firebaseDatabase: FirebaseDatabase,
    ): DatabaseReference = firebaseDatabase.reference
}

