package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.lifecycle.ViewModel
import android.content.ContentResolver
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import com.gregmcgowan.fivesorganiser.data.player.PlayerRepoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(
        includes = [
            ImportContactsModule.Bindings::class,
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ]
)
class ImportContactsModule {

    @Provides
    fun contentResolver(importContactsActivity: ImportContactsActivity): ContentResolver {
        return importContactsActivity.contentResolver
    }

    @Provides
    fun hasContactPermission(importContactsActivity: ImportContactsActivity) : Boolean  {
        return importContactsActivity.hasContactPermission
    }

    @Module
    interface Bindings {

        @Binds
        @IntoMap
        @ViewModelKey(ImportContactsViewModel::class)
        fun bindViewModel(viewModel: ImportContactsViewModel): ViewModel

        @Binds
        fun bindContactImporter(androidContactImporter: AndroidContactImporter): ContactImporter

    }


}