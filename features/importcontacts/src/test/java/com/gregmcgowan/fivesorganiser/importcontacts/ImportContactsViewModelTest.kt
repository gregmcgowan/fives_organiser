package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ContactsListUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ErrorUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.LoadingUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.ShowRequestPermissionDialogUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.TerminalUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiState.UserDeniedPermissionUiState
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.AddButtonPressedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionDeniedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactPermissionGrantedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.ContactSelectedEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.DoNotTryPermissionAgainEvent
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.TryPermissionAgainEvent
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import com.gregmcgowan.fivesorganiser.test_shared.createList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.samePropertyValuesAs

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException


class ImportContactsViewModelTest {

    // StandardTestDispatcher does not run coroutines by default. So we can control the execution
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)

    private lateinit var fixture: JFixture

    private lateinit var fakeSavePlayersUseCase: FakeSavePlayersUseCase
    private lateinit var fakeUiStateMapper: FakeUiStateMapper
    private lateinit var fakePermission: FakePermission
    private lateinit var fakeFakeGetContactsUseCase: FakeGetContactsUseCase

    private lateinit var sut: ImportContactsViewModel

    @Before
    fun setUp() {
        fixture = JFixture()
        fixture.customise().lazyInstance(ContactItemUiState::class.java) {
            ContactItemUiState(
                    name = fixture.build(),
                    isSelected = false,
                    contactId = fixture.build()
            )
        }

        fakeFakeGetContactsUseCase = FakeGetContactsUseCase()
        fakeUiStateMapper = FakeUiStateMapper()
        fakePermission = FakePermission()
        fakeSavePlayersUseCase = FakeSavePlayersUseCase()
    }


    @Test
    fun `init() when permission is granted shows loading then content`() = runTest {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()

        assertThat(sut.uiStateFlow.value, equalTo(LoadingUiState))

        runCurrent()

        assertThat(sut.uiStateFlow.value, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `init() without permission returns request permission state`() = runTest {
        // run
        setupFakes(permission = false)
        setupSut()

        // verify
        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))
    }

    @Test
    fun `onContactsPermissionGranted() loads contacts`() = runTest {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = false)
        setupSut()

        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))

        // run on contact permission granted
        sut.handleEvent(ContactPermissionGrantedEvent)
        runCurrent()

        // verify output
        assertThat(sut.uiStateFlow.value, equalTo(fixtInitialUiModel))
    }


    @Test
    fun `onContactSelected() updates model when one is selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val actualInitialUiModel = sut.uiStateFlow.value

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // check ui model is updated correctly
        val expectedContactUiModelList = createSelectedContacts(
                initialUiModel = actualInitialUiModel,
                selectedContacts = setOf(0)
        )
        assertThat(sut.uiStateFlow.value,
                samePropertyValuesAs(
                        ContactsListUiState(expectedContactUiModelList, true)
                )
        )
    }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiStateFlow.value

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))
        runCurrent()

        // add another
        val secondContactId = fixtInitialUiModel.contacts[1].contactId
        sut.handleEvent(ContactSelectedEvent(secondContactId, true))
        runCurrent()

        // check the second UI model is emitted
        val expectedContactUiModelList = createSelectedContacts(initialUiModel, selectedContacts = setOf(0, 1))
        assertThat(sut.uiStateFlow.value, samePropertyValuesAs(
                ContactsListUiState(expectedContactUiModelList, true))
        )
    }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))
        runCurrent()

        // deselect
        sut.handleEvent(ContactSelectedEvent(firstContactId, false))
        runCurrent()

        // check that the ui model is back to initial
        assertThat(sut.uiStateFlow.value, samePropertyValuesAs(
                ContactsListUiState(fixtInitialUiModel.contacts, false))
        )
    }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiStateFlow.value

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))
        runCurrent()

        // add another contact
        val secondContactId = fixtInitialUiModel.contacts[1].contactId
        sut.handleEvent(ContactSelectedEvent(secondContactId, true))
        runCurrent()

        // deselect first one
        sut.handleEvent(ContactSelectedEvent(firstContactId, false))
        runCurrent()

        // check the second UI model is emitted
        val expectedContactUiModelList = createSelectedContacts(initialUiModel, selectedContacts = setOf(1))
        assertThat(sut.uiStateFlow.value, samePropertyValuesAs(
                ContactsListUiState(expectedContactUiModelList, true))
        )

    }

    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() = runTest {
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // check we show loading then close
        sut.handleEvent(AddButtonPressedEvent)
        assertThat(sut.uiStateFlow.value, equalTo(LoadingUiState))

        // run cour
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(TerminalUiState))
    }

    @Test
    fun `onAddButtonPressed() when there is an error`() = runTest {
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiState = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // run
        val runTimeException: RuntimeException = fixture.build()
        fakeSavePlayersUseCase.exception = runTimeException

        sut.handleEvent(AddButtonPressedEvent)
        assertThat(sut.uiStateFlow.value, equalTo(LoadingUiState))

        // run
        runCurrent()
        assertThat(sut.uiStateFlow.value, samePropertyValuesAs(ErrorUiState(R.string.generic_error_message)))
    }

    @Test
    fun `on contact permission denied return the correct ui state`() = runTest {
        setupFakes(uiState = ShowRequestPermissionDialogUiState, permission = false)
        setupSut()
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))

        sut.handleEvent(ContactPermissionDeniedEvent)
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(UserDeniedPermissionUiState))
    }

    @Test
    fun `when user agrees to ask for permission again return the correct ui state`() = runTest {
        setupFakes(uiState = ShowRequestPermissionDialogUiState, permission = false)
        setupSut()
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))

        sut.handleEvent(TryPermissionAgainEvent)
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))
    }

    @Test
    fun `when user does not agree to ask for permission again return the correct ui state`() = runTest {
        setupFakes(uiState = ShowRequestPermissionDialogUiState, permission = false)
        setupSut()
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(ShowRequestPermissionDialogUiState))

        sut.handleEvent(DoNotTryPermissionAgainEvent)
        runCurrent()
        assertThat(sut.uiStateFlow.value, equalTo(TerminalUiState))
    }

    private fun setupSut() {
        sut = ImportContactsViewModel(
                fakeUiStateMapper,
                fakeSavePlayersUseCase,
                fakeFakeGetContactsUseCase,
                fakePermission
        )
    }

    private fun setupFakes(contacts: List<Contact> = fixture.createList(),
                           uiState: ImportContactsUiState = createInitialUiModel(),
                           permission: Boolean) {
        fakeFakeGetContactsUseCase.contacts = contacts.toMutableList()
        fakeUiStateMapper.uiState = uiState
        fakePermission.hasPermission = permission
    }

    private fun createInitialUiModel(): ContactsListUiState =
            ContactsListUiState(
                    contacts = fixture.createList(),
                    addContactsButtonEnabled = false
            )

    private fun createSelectedContacts(initialUiModel: ImportContactsUiState,
                                       selectedContacts: Set<Int> = emptySet()): List<ContactItemUiState> {
        return initialUiModel.safeContacts
                .toMutableList()
                .mapIndexed { index, contactItemUiModel ->
                    contactItemUiModel.copy(isSelected = selectedContacts.contains(index))
                }
    }

}


class FakeGetContactsUseCase : GetContactsUseCase {

    var contacts: MutableList<Contact> = mutableListOf()

    override suspend fun execute(): List<Contact> {
        return contacts
    }
}

class FakeSavePlayersUseCase : SavePlayersUseCase {

    var exception: RuntimeException? = null

    override suspend fun execute(selectedContacts: Set<Long>) {
        if (exception != null) {
            throw exception!!
        }
    }


}

class FakeUiStateMapper : ImportContactsUiStateMapper {

    lateinit var uiState: ImportContactsUiState

    override fun map(contacts: List<Contact>, selectedContacts: Set<Long>): ImportContactsUiState =
            this.uiState

}

class FakePermission : Permission {

    var hasPermission: Boolean = false

    override val name: String
        get() = "Contacts permission"

    override fun hasPermission(): Boolean = hasPermission

}
