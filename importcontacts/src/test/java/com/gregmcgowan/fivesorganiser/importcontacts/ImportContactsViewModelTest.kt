package com.gregmcgowan.fivesorganiser.importcontacts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.core.Either
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.RequestPermission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent
import com.gregmgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
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

class ImportContactsViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

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

    private val testCoroutineDispatcher get() = coroutinesTestRule.testDispatcher


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
    fun `init() when permission is granted shows loading then content`() = testCoroutineDispatcher.runBlockingTest {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)

        testCoroutineDispatcher.pauseDispatcher()
        setupSut(contactPermission = true)

        // verify loading UI
        val actualLoadingOutput = sut.uiModel
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        testCoroutineDispatcher.resumeDispatcher()
        // verify
        val actualContentOutput = sut.uiModel
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `init() without permission sends request permission event`() = testCoroutineDispatcher.runBlockingTest {
        testCoroutineDispatcher.pauseDispatcher()
        setupSut(contactPermission = false)

        // run
        val output: ImportContactsUiEvent = sut.importContactsUiEvent.first()
        testCoroutineDispatcher.resumeDispatcher()

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
        val actualLoadingOutput = sut.uiModel
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        // run on contact permission granted
        sut.handleEvent(ImportContactsUserEvent.ContactPermissionGrantedEvent)

        // verify output
        val actualContentOutput = sut.uiModel
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `onContactSelected() updates model when none are selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)

        val initialUiModel = sut.uiModel

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))

        // check ui model is updated correctly
        val expectedContactUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(
                initialUiModel.copy(contacts = expectedContactUiModelList,
                        importContactsButtonEnabled = true)))
    }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)

        val initialUiModel = sut.uiModel

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))

        // add another
        val secondContactId = fixtInitialUiModel.contacts[1].contactId
        sut.handleEvent(ContactSelectedEvent(secondContactId, true))

        // check the second UI model is emitted
        val expectedContactUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0, 1))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedContactUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)
        // testDispatchers.getTestCoroutineContext().triggerActions()
        val initialUiModel = sut.uiModel

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))

        // deselect
        sut.handleEvent(ContactSelectedEvent(firstContactId, false))

        // check that the ui model is back to initial
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(importContactsButtonEnabled = false)))
    }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() = runBlocking {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(contactPermission = true)

        val initialUiModel = sut.uiModel

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))

        // add another contact
        val secondContactId = fixtInitialUiModel.contacts[1].contactId
        sut.handleEvent(ContactSelectedEvent(secondContactId, true))

        // deselect first one
        sut.handleEvent(ContactSelectedEvent(firstContactId, false))

        // check the second UI model is emitted
        val expectedUiModelList = updateContact(initialUiModel, selectedContacts = setOf(1))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() = testCoroutineDispatcher.runBlockingTest {
        val fixtInitialUiModel = createInitialUiModel()
        setupMocks(fixtContacts, fixtInitialUiModel)
        setupSut(true)
        val initialUiModel = sut.uiModel

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))

        // run
        whenever(mockSavePlayersUseCase.execute(setOf(contactId))).thenReturn(Either.Right(Unit))
        testCoroutineDispatcher.pauseDispatcher()
        sut.handleEvent(AddButtonPressedEvent)

        // check close screen event
        val actualContentOutput = sut.importContactsUiEvent.first()
        testCoroutineDispatcher.resumeDispatcher()
        assertThat(actualContentOutput as ImportContactsUiEvent.CloseScreen,
                equalTo(ImportContactsUiEvent.CloseScreen))

        // check models
        val expectedUiModelList = updateContact(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(
                contacts = expectedUiModelList,
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = true
        )))

    }

    private fun setupSut(contactPermission: Boolean) {
        sut = ImportContactsViewModel(
                mockUiModelMaper,
                mockSavePlayersUseCase,
                mockGetContactsUseCase,
                contactPermission
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