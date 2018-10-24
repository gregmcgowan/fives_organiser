package com.gregmcgowan.fivesorganiser.importcontacts

import TEST_COROUTINE_DISPTACHERS_AND_CONTEXT
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.flextrade.jfixture.annotations.Fixture
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsNavEvent.RequestPermission
import com.gregmgowan.fivesorganiser.test_shared.getValueForTest
import com.nhaarman.mockitokotlin2.verify
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
    lateinit var fixtUiModel: ImportContactsUiModel

    @Fixture
    lateinit var fixtContacts: List<Contact>

    private lateinit var sut: ImportContactsViewModel

    private lateinit var fixture: JFixture

    private val testDispatchers = TEST_COROUTINE_DISPTACHERS_AND_CONTEXT

    @Before
    fun setUp() {
        fixture = JFixture()
        MockitoAnnotations.initMocks(this)
        FixtureAnnotations.initFixtures(this)
    }

    @Test
    fun `init() with permission sends idle event`() = runBlocking {
        // setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)

        // run
        val output = sut.contactUiNavLiveData.getValueForTest()

        // verify
        assertThat(output as ImportContactsNavEvent.Idle, equalTo(ImportContactsNavEvent.Idle))
    }


    @Test
    fun `init() when permission is granted shows loading then content`() = runBlocking {
        // setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)

        // verify loading UI
        val actualLoadingOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualLoadingOutput, equalTo(ImportContactsUiModel(
                emptyList(), true, false,
                false, false, null)))

        // trigger contacts loaded
        testDispatchers.getTestCoroutineContext().triggerActions()

        // verify
        val actualContentOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualContentOutput, equalTo(fixtUiModel))
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
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = false)

        // verify loading
        val actualLoadingOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualLoadingOutput, equalTo(ImportContactsUiModel(
                emptyList(), true, false, false, false, null)))

        // run on contact permission granted
        sut.onContactsPermissionGranted()
        testDispatchers.getTestCoroutineContext().triggerActions()

        // verify output
        val actualContentOutput = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualContentOutput, equalTo(fixtUiModel))
    }


    @Test
    fun `onContactSelected() updates model when none are selected`() = runBlocking {
        // initial setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()

        //add contact
        val contactId = fixtUiModel.contacts[0].contactId
        val fixtFirstUiModel = fixture.create(ImportContactsUiModel::class.java)
        whenever(mockUiModelMaper.map(fixtContacts, setOf(contactId))).thenReturn(fixtFirstUiModel)
        sut.contactSelected(contactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check ui model is updated correctly
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(fixtFirstUiModel))
    }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() = runBlocking {
        // initial setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // add contact
        val fixtFirstUiModel = fixture.create(ImportContactsUiModel::class.java)
        val firstContactId = fixtUiModel.contacts[0].contactId
        whenever(mockUiModelMaper.map(fixtContacts, setOf(firstContactId))).thenReturn(fixtFirstUiModel)
        sut.contactSelected(firstContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // add another
        val fixtSecondUiModel = fixture.create(ImportContactsUiModel::class.java)
        val secondContactId = fixtUiModel.contacts[1].contactId
        whenever(mockUiModelMaper.map(fixtContacts, setOf(firstContactId, secondContactId)))
                .thenReturn(fixtSecondUiModel)
        sut.contactSelected(secondContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check the second UI model is emitted
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(fixtSecondUiModel))
    }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() = runBlocking {
        // initial setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // add contact
        val fixtFirstUiModel = fixture.create(ImportContactsUiModel::class.java)
        val firstContactId = fixtUiModel.contacts[0].contactId
        whenever(mockUiModelMaper.map(fixtContacts, setOf(firstContactId))).thenReturn(fixtFirstUiModel)
        sut.contactSelected(firstContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // deselect
        val fixtSecondUiModel = fixture.create(ImportContactsUiModel::class.java)
        whenever(mockUiModelMaper.map(fixtContacts, emptySet())).thenReturn(fixtSecondUiModel)
        sut.contactDeselected(firstContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check the second UI model is emitted
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(fixtSecondUiModel))
    }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() = runBlocking {
        // initial setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(contactPermission = true)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // add contact
        val fixtFirstUiModel = fixture.create(ImportContactsUiModel::class.java)
        val firstContactId = fixtUiModel.contacts[0].contactId
        whenever(mockUiModelMaper.map(fixtContacts, setOf(firstContactId))).thenReturn(fixtFirstUiModel)
        sut.contactSelected(firstContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // add anotehr contact
        val fixtSecondUiModel = fixture.create(ImportContactsUiModel::class.java)
        val secondContactID = fixtUiModel.contacts[1].contactId
        whenever(mockUiModelMaper.map(fixtContacts, setOf(firstContactId, secondContactID))).thenReturn(fixtSecondUiModel)
        sut.contactSelected(secondContactID)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // deselect first one
        val fixtUpdatedUiModel = fixture.create(ImportContactsUiModel::class.java)
        whenever(mockUiModelMaper.map(fixtContacts, setOf(secondContactID))).thenReturn(fixtUpdatedUiModel)
        sut.contactDeselected(firstContactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check the second UI model is emitted
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(fixtUpdatedUiModel))
    }


    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() = runBlocking {
        // initial setup
        setupMocks(fixtContacts, fixtUiModel)
        setupSut(true)
        testDispatchers.getTestCoroutineContext().triggerActions()

        //add contact
        val contactId = fixtUiModel.contacts[0].contactId
        val fixtFirstUiModel = fixture.create(ImportContactsUiModel::class.java)
        whenever(mockUiModelMaper.map(fixtContacts, setOf(contactId))).thenReturn(fixtFirstUiModel)

        sut.contactSelected(contactId)
        testDispatchers.getTestCoroutineContext().triggerActions()

        //run
        sut.onAddButtonPressed()

        // check models
        val actualUpdatedModel = sut.contactUiModelLiveData.getValueForTest()
        assertThat(actualUpdatedModel, equalTo(ImportContactsUiModel(
                fixtFirstUiModel.contacts, true, false,
                fixtFirstUiModel.importContactsButtonEnabled, fixtFirstUiModel.showErrorMessage,
                fixtFirstUiModel.errorMessage)))

        // dispatch
        testDispatchers.getTestCoroutineContext().triggerActions()

        // check models and mocks
        val actualContentOutput = sut.contactUiNavLiveData.getValueForTest()
        assertThat(actualContentOutput as ImportContactsNavEvent.CloseScreen,
                equalTo(ImportContactsNavEvent.CloseScreen))
        verify(mockSavePlayersUseCase).execute(setOf(contactId))
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

    private suspend fun setupMocks(contacts: List<Contact>,
                                   uiModel: ImportContactsUiModel,
                                   selectedContacts: Set<Long> = emptySet()) {
        whenever(mockGetContactsUseCase.execute()).thenReturn(contacts)
        whenever(mockUiModelMaper.map(contacts, selectedContacts)).thenReturn(uiModel)
    }


}