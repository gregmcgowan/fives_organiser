package com.gregmcgowan.fivesorganiser.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
class DataModule {

    @Provides
    fun fireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}