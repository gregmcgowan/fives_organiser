package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.gregmcgowan.fivesorganiser.core.permissions.AndroidPermission
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
class ImportContactsModule {
    @Provides
    fun contentResolver(app: Application): ContentResolver = app.contentResolver

    @Provides
    fun hasContactPermission(context: Context): Permission =
        AndroidPermission(
            context = context,
            permissionName = Manifest.permission.READ_CONTACTS,
        )

    @ContributesTo(AppScope::class)
    @BindingContainer
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
