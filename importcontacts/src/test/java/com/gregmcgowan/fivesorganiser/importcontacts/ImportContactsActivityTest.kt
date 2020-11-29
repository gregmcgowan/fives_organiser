package com.gregmcgowan.fivesorganiser.importcontacts

import android.widget.Button
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmgowan.fivesorganiser.test_shared.RecyclerViewItemCountAssertion
import com.gregmgowan.fivesorganiser.test_shared.TestApp
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.Matchers.not
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = TestApp::class)
class ImportContactsActivityTest {

    private lateinit var fixture: JFixture

    @Fixture
    lateinit var fixtContactUiModels: List<ContactItemUiModel>

    @Before
    fun setUp() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this)
    }

    @Ignore
    @Test
    fun `on create sets listener`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO fix
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
//        })

        val scenario = launch(ImportContactsActivity::class.java)
                .apply { moveToState(Lifecycle.State.CREATED) }

        assertNotNull(scenario)
        scenario.onActivity { activity: ImportContactsActivity? ->
            val button = activity?.findViewById<Button>(R.id.import_contacts_add_button)
            requireNotNull(button)
            assertTrue(button.hasOnClickListeners())
        }
    }

    @Ignore
    @Test
    fun `shows loading`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO fix
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
//        })

        launch(ImportContactsActivity::class.java).apply { moveToState(Lifecycle.State.RESUMED) }

        uiModelMutableLiveData.value = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = false,
                errorMessage = NO_STRING_RES_ID)

        onView(withId(R.id.import_contacts_toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_progress_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_main_content)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_add_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_add_button)).check(matches(not(isEnabled())))
        onView(withId(R.id.import_contacts_error_message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_list)).check(RecyclerViewItemCountAssertion(0))
    }

    @Ignore
    @Test
    fun `shows content`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO fix
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
//        })

        launch(ImportContactsActivity::class.java).apply { moveToState(Lifecycle.State.RESUMED) }

        uiModelMutableLiveData.value = ImportContactsUiModel(
                contacts = fixtContactUiModels,
                showLoading = false,
                showContent = true,
                importContactsButtonEnabled = false,
                errorMessage = NO_STRING_RES_ID)

        onView(withId(R.id.import_contacts_toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_main_content)).check(matches((isDisplayed())))
        onView(withId(R.id.import_contacts_add_button)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_add_button)).check(matches(not(isEnabled())))
        onView(withId(R.id.import_contacts_error_message)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_list)).check(RecyclerViewItemCountAssertion(fixtContactUiModels.size))
    }


    @Test
    @Ignore
    fun `shows empty message`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
        //})

        launch(ImportContactsActivity::class.java).apply { moveToState(Lifecycle.State.RESUMED) }

        uiModelMutableLiveData.value = ImportContactsUiModel(
                contacts = emptyList(),
                showLoading = false,
                showContent = false,
                importContactsButtonEnabled = false,
                errorMessage = R.string.no_contacts_message)

        onView(withId(R.id.import_contacts_toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_main_content)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_add_button)).check(matches(not(isDisplayed())))
        onView(withId(R.id.import_contacts_add_button)).check(matches(not(isEnabled())))
        onView(withId(R.id.import_contacts_error_message)).check(matches(withText(R.string.no_contacts_message)))
        // TODO this test fails on this line, it will pass if a width and height is hardcoded on the text view
        // TODO the widget is visible but with no width and height
        onView(withId(R.id.import_contacts_error_message)).check(matches(isDisplayed()))
        onView(withId(R.id.import_contacts_list)).check(RecyclerViewItemCountAssertion(0))
    }

    @Ignore
    @Test
    fun `onPermissionGranted() forwards to viewModel`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO fix
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
//        })

        val activityScenario = launch(ImportContactsActivity::class.java)
                .apply { moveToState(Lifecycle.State.RESUMED) }

        activityScenario.onActivity { activity: ImportContactsActivity? ->
            requireNotNull(activity)
            activity.onPermissionGranted()
            //TODO fix
            //verify(activity.importImportContactsViewModel).onContactsPermissionGranted()
        }
    }


    @Ignore
    @Test
    fun `close screen event finishes activity`() {
        val uiModelMutableLiveData = MutableLiveData<ImportContactsUiModel>()
        val navEventMutableLiveData = MutableLiveData<ImportContactsNavEvent>()

        //TODO fix
//        injectViewModeProviderFactory(object : MockViewModelProviderFactory() {
//            // Can we somehow infer the type of the ViewModel??
//            override fun <T : ViewModel?> init(viewModel: T?) {
//                if (viewModel is ImportContactsViewModel) {
//                    whenever(viewModel.contactUiModelLiveData).thenReturn(uiModelMutableLiveData)
//                    whenever(viewModel.contactUiNavLiveData).thenReturn(navEventMutableLiveData)
//                }
//            }
//        })

        val activityScenario = launch(ImportContactsActivity::class.java)
                .apply { moveToState(Lifecycle.State.RESUMED) }

        navEventMutableLiveData.value = ImportContactsNavEvent.CloseScreen

        activityScenario.onActivity { assertTrue(it.isFinishing) }
    }


}
