package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.gregmcgowan.fivesorganiser.core.permissions.AndroidPermission
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ViewModelComponent::class)
@Module(includes = [ImportContactsModule.Bindings::class])
class ImportContactsModule {
    @Provides
    fun contentResolver(app: Application): ContentResolver {
        return app.contentResolver
    }

    @Provides
    fun hasContactPermission(
        @ApplicationContext context: Context,
    ): Permission {
        return AndroidPermission(context, Manifest.permission.READ_CONTACTS)
    }

    @InstallIn(ViewModelComponent::class)
    @Module
    interface Bindings {
        @Binds
        fun bindContactImporter(impl: AndroidContactImporter): ContactImporter

        @Binds
        fun bindGetContactsUseCase(impl: GetContactsUseCaseImpl): GetContactsUseCase

        @Binds
        fun bindSavePlayersUseCase(impl: SavePlayersUseCaseImpl): SavePlayersUseCase

        @Binds
        fun bindUiMapper(impl: ImportContactsUiStateMapperImpl): ImportContactsUiStateMapper
    }
}
