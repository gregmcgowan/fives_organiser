package com.gregmcgowan.fivesorganiser.importcontacts

import android.app.Activity
import android.content.ContentResolver
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module(
        includes = [
            ImportContactsModule.Bindings::class,
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ]
)
class ImportContactsModule {

    @Provides
    fun contentResolver(importContactsActivity: Activity): ContentResolver {
        return importContactsActivity.contentResolver
    }

    @Provides
    fun hasContactPermission(importContactsActivity: Activity): Boolean {
        return (importContactsActivity as ImportContactsActivity).hasContactPermission
    }

    @InstallIn(ActivityComponent::class)
    @Module
    interface Bindings {

        @Binds
        fun bindContactImporter(androidContactImporter: AndroidContactImporter): ContactImporter

    }


}