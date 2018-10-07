package com.gregmcgowan.fivesorganiser.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    fun fireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}