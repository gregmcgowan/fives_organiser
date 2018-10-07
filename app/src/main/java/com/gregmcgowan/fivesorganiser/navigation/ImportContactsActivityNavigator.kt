package com.gregmcgowan.fivesorganiser.navigation

import android.app.Activity
import com.gregmcgowan.fivesorganiser.importcontacts.importContactsIntent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavigator
import javax.inject.Inject

const val IMPORT_CONTACTS = 1

class ImportContactsActivityNavigator @Inject constructor(
        private val activity: Activity
) : ImportContactsNavigator {

    override fun goToImportContacts() {
        activity.startActivityForResult(activity.importContactsIntent(), IMPORT_CONTACTS)
    }
}