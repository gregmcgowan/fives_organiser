package com.gregmcgowan.fivesorganiser.importcontacts

import android.arch.lifecycle.ViewModel
import com.gregmcgowan.fivesorganiser.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ImportContactsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ImportContactsViewModel::class)
    abstract fun bindViewModel(viewModel: ImportContactsViewModel): ViewModel

    @Binds
    abstract fun bindContactImporter(androidContactImporter: AndroidContactImporter)
            : ContactImporter

}