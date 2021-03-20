package com.gregmcgowan.fivesorganiser.importcontacts

import android.app.Activity
import android.content.ContentResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module(includes = [ImportContactsModule.Bindings::class])
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