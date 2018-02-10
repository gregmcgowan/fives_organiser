package com.gregmcgowan.fivesorganiser.importContacts

import android.content.ContentResolver
import com.gregmcgowan.fivesorganiser.core.data.player.PlayerRepoModule
import com.gregmcgowan.fivesorganiser.core.data.player.PlayersFirebaseRepo
import com.gregmcgowan.fivesorganiser.core.di.ActivityScope
import com.gregmcgowan.fivesorganiser.core.di.AppComponent
import com.gregmcgowan.fivesorganiser.core.di.ViewModelBuilder
import com.gregmcgowan.fivesorganiser.core.di.ViewModelComponent
import dagger.BindsInstance
import dagger.Component

@Component(
        dependencies = [AppComponent::class],
        modules = [
            ImportContactsModule::class,
            PlayerRepoModule::class,
            ViewModelBuilder::class
        ]
)
@ActivityScope
interface ImportContactsComponent : ViewModelComponent {

    fun inject(importContactsActivity: ImportContactsActivity)

    fun contentResolver(): ContentResolver

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun contentResolver(contentResolver: ContentResolver): Builder

        fun appComponent(appComponent: AppComponent): Builder

        fun build(): ImportContactsComponent
    }

}