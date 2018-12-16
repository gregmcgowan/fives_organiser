package com.gregmcgowan.fivesorganiser.importcontacts

import TEST_COROUTINE_DISPTACHERS_AND_CONTEXT
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavEvent.RequestPermission
import com.gregmgowan.fivesorganiser.test_shared.getValueForTest
import com.nhaarman.mockitokotlin2.whenever
import getTestCoroutineContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private val LOADING_UI_MODEL = ImportContactsUiModel(
        contacts = emptyList(),
        showLoading = true,
        showContent = false,
        importContactsButtonEnabled = false,
        errorMessage = NO_STRING_RES_ID
)

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ImportContactsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockUiModelMaper: ImportContactsUiModelMapper

    @Mock
    lateinit var mockSavePlayersUseCase: SavePlayersUseCase

    @Mock
    lateinit var mockGetContactsUseCase: GetContactsUseCase

    @Fixture
    lateinit var fixtContacts: List<Contact>

    @Fixture
    lateinit var fixtContactsUiModel: List<ContactItemUiModel>

    private lateinit var sut: ImportContactsViewModel

    private lateinit var fixture: JFixture

    private val testDispatchers = TEST_COROUTINE_DISPTACHERS_AND_CONTEXT

    @Before
    fun setUp() {
        fixture = JFixture()
        // default to selected is false
        fixture.customise().lazyInstance(ContactItemUiModel::class.java) {
            ContactItemUiModel(fixture.create(String::class.java),
                    false, fixture.create(Long::class.java))
        }
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this, fixture)
    }

    @Test
    fun `init() with permission sends idle event`() = runBlocking {
        // setup
        setupMocks(fixtContacts, createInitialUiModel())
        setupSut(contactPermission = true)

        // run
        val output = sut.contactUiNavLiveData.getValueForTest()

        // verify
        assertThat(output as ImportContactsNavEvent.Idle, equalTo(ImportContactsNavEvent.Idle))
    }


    @Test
    fun `init() when permission is granted shows loading then content`() = runBlocking {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)

        // verify loading UI
        val actualLoadingOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        // trigger contacts loaded
        testDispatchers.getTestCoroutineContext().triggerActions()

        // verify
        val actualContentOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `init() without permission sends request permission event`() {
        // setup
        setupSut(contactPermission = false)

        // run
        val output = sut.contactUiNavLiveData.getValueForTest()

        // verify
        assertThat(output as RequestPermission, equalTo(RequestPermission))
    }

    @Test
    fun `onContactsPermissionGranted() loads contacts`() = runBlocking {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = false)

        // verify loading
        val actualLoadingOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        // run on contact permission granted
        sut.onContactsPermissionGranted()
        testDispatchers.getTestCoroutineContext().triggerActions()

        // verify output
        val actualContentOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `onContactSelected() updates model when none are selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.contactUiModelLiveData.getValueForTest()

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.contactSelected(contactId)

        // check ui model is updated correctly
        val expectedContactUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedContactUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.contactUiModelLiveData.getValueForTest()

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.contactSelected(firstContactId)

        // add another
        val secondContactId = fixtInitialUiModel.contacts[1].contactId
        sut.contactSelected(secondContactId)

        // check the second UI model is emitted
        val expectedContactUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0, 1))
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedContactUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.contactUiModelLiveData.getValueForTest()

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.contactSelected(firstContactId)

        // deselect
        sut.contactDeselected(firstContactId)

        // check that the ui model is back to initial
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(importContactsButtonEnabled = false)))
    }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.contactUiModelLiveData.getValueForTest()

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.contactSelected(firstContactId)

        // add another contact
        val secondContactID = fixtInitialUiModel.contacts[1].contactId
        sut.contactSelected(secondContactID)

        // deselect first one
        sut.contactDeselected(firstContactId)

        // check the second UI model is emitted
        val expectedUiModelList = updateContact(initialUiModel, selectedContacts = setOf(1))
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(true)
        testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.contactUiModelLiveData.getValueForTest()

        //add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.contactSelected(contactId)

        //run
        whenever(mockSavePlayersUseCase.execute(setOf(contactId))).thenReturn(Either.Right(Unit))
        sut.onAddButtonPressed()
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check models
        val expectedUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(
                contacts = expectedUiModelList,
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = true
        )))

        // check models and mocks
        val actualContentOutput = sut.contactUiNavLiveData.getValueForTest()
        assertThat(actualContentOutput as ImportContactsNavEvent.CloseScreen,
                equalTo(ImportContactsNavEvent.CloseScreen))
    }

    private fun setupSut(contactPermission: Boolean) {
        sut = ImportContactsViewModel(
                mockUiModelMaper,
                mockSavePlayersUseCase,
                mockGetContactsUseCase,
                contactPermission,
                testDispatchers
        )
    }

    private fun updateContact(initialUiModel: ImportContactsUiModel,
                              selectedContacts: Set<Int> = emptySet(),
                              notSelectedContacts: Set<Int> = emptySet()): List<ContactItemUiModel> {
        return initialUiModel.contacts.toMutableList().mapIndexed { index, contactItemUiModel ->
            when {
                selectedContacts.contains(index) -> contactItemUiModel.copy(isSelected = true)
                notSelectedContacts.contains(index) -> contactItemUiModel.copy(isSelected = false)
                else -> contactItemUiModel
            }
        }
    }

    private fun createInitialUiModel(): ImportContactsUiModel {
        return ImportContactsUiModel(
                contacts = fixtContactsUiModel,
                showLoading = false,
                showContent = true,
                importContactsButtonEnabled = false
        )

    }

    private suspend fun setupMocks(contacts: List<Contact>,
                                   uiModel: ImportContactsUiModel,
                                   selectedContacts: Set<Long> = emptySet()) {
        whenever(mockGetContactsUseCase.execute()).thenReturn(Either.Right(contacts))
        whenever(mockUiModelMaper.map(contacts, selectedContacts)).thenReturn(uiModel)
    }


}