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

class ImportContactsViewModelTest {
    // StandardTestDispatcher does not run coroutines by default. So we can control the execution
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)

    private lateinit var fixture: JFixture

    private lateinit var fakeSavePlayersUseCase: FakeSavePlayersUseCase
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
                contactId = fixture.build(),
            )
        }

        fakeFakeGetContactsUseCase = FakeGetContactsUseCase()
        fakePermission = FakePermission()
        fakeSavePlayersUseCase = FakeSavePlayersUseCase()
    }

    @Test
    fun `init() when permission is granted shows loading then content`() =
        runTest {
            // setup
            val contacts: List<Contact> = fixture.createList()
            val expectedUi: List<ContactItemUiState> = contacts.toUiState()
            setupFakes(permission = true, contacts = contacts)
            setupSut()

            assertThat(sut.uiState.value, equalTo(LoadingUiState))

            runCurrent()

            assertThat(
                sut.uiState.value,
                equalTo(
                    ContactsListUiState(
                        contacts = expectedUi,
                        addContactsButtonEnabled = false,
                    ),
                ),
            )
        }

    @Test
    fun `init() without permission returns request permission state`() =
        runTest {
            // run
            setupFakes(permission = false)
            setupSut()

            // verify
            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))
        }

    @Test
    fun `onContactsPermissionGranted() loads contacts`() =
        runTest {
            // setup
            val contacts: List<Contact> = fixture.createList()
            val expectedUi: List<ContactItemUiState> = contacts.toUiState()
            setupFakes(permission = false, contacts = contacts)
            setupSut()

            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))

            // run on contact permission granted
            sut.handleEvent(ContactPermissionGrantedEvent)
            runCurrent()

            // verify output
            assertThat(
                sut.uiState.value,
                equalTo(
                    ContactsListUiState(
                        contacts = expectedUi,
                        addContactsButtonEnabled = false,
                    ),
                ),
            )
        }

    @Test
    fun `onContactSelected() updates model when one is selected`() =
        runTest {
            // initial setup
            val contacts: List<Contact> = fixture.createList()
            val expectedUi: List<ContactItemUiState> = contacts.toUiState()
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val contactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(contactId, true))
            runCurrent()

            val expectedContactUiModelList: List<ContactItemUiState> =
                listOf(
                    expectedUi[0].copy(isSelected = true),
                    expectedUi[1],
                    expectedUi[2],
                )

            assertThat(
                sut.uiState.value,
                samePropertyValuesAs(
                    ContactsListUiState(
                        contacts = expectedContactUiModelList,
                        addContactsButtonEnabled = true,
                    ),
                ),
            )
        }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() =
        runTest {
            // initial setup
            val contacts: List<Contact> = fixture.createList()
            val expectedUi: List<ContactItemUiState> = contacts.toUiState()
            setupFakes(permission = true, contacts = contacts)
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val firstContactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(firstContactId, true))
            runCurrent()

            // add another
            val secondContactId = contacts[1].contactId
            sut.handleEvent(ContactSelectedEvent(secondContactId, true))
            runCurrent()

            // check the second UI model is emitted
            val expectedContactUiModelList: List<ContactItemUiState> =
                listOf(
                    expectedUi[0].copy(isSelected = true),
                    expectedUi[1].copy(isSelected = true),
                    expectedUi[2],
                )
            assertThat(
                sut.uiState.value,
                samePropertyValuesAs(
                    ContactsListUiState(
                        contacts = expectedContactUiModelList,
                        addContactsButtonEnabled = true,
                    ),
                ),
            )
        }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() =
        runTest {
            // initial setup
            val contacts: List<Contact> = fixture.createList()
            val expectedUi: List<ContactItemUiState> = contacts.toUiState()
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val firstContactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(firstContactId, true))
            runCurrent()

            // deselect
            sut.handleEvent(ContactSelectedEvent(firstContactId, false))
            runCurrent()

            // check that the ui model is back to initial
            assertThat(
                sut.uiState.value,
                samePropertyValuesAs(
                    ContactsListUiState(
                        contacts = expectedUi,
                        addContactsButtonEnabled = false,
                    ),
                ),
            )
        }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() =
        runTest {
            // initial setup
            val contacts: List<Contact> = fixture.createList()
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val firstContactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(firstContactId, true))
            runCurrent()

            // add another contact
            val secondContactId = contacts[1].contactId
            sut.handleEvent(ContactSelectedEvent(secondContactId, true))
            runCurrent()

            // deselect first one
            sut.handleEvent(ContactSelectedEvent(firstContactId, false))
            runCurrent()

            // check the second UI model is emitted

            // asserThat()
        }

    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() =
        runTest {
            val contacts: List<Contact> = fixture.createList()
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val contactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(contactId, true))
            runCurrent()

            // check we show loading then close
            sut.handleEvent(AddButtonPressedEvent)
            assertThat(sut.uiState.value, equalTo(LoadingUiState))

            // run cour
            runCurrent()
            assertThat(sut.uiState.value, equalTo(TerminalUiState))
        }

    @Test
    fun `onAddButtonPressed() when there is an error`() =
        runTest {
            val contacts: List<Contact> = fixture.createList()
            setupFakes(permission = true, contacts = contacts)
            setupSut()
            runCurrent()

            // add contact
            val contactId = contacts[0].contactId
            sut.handleEvent(ContactSelectedEvent(contactId, true))
            runCurrent()

            // run
            val runTimeException: RuntimeException = fixture.build()
            fakeSavePlayersUseCase.exception = runTimeException

            sut.handleEvent(AddButtonPressedEvent)
            assertThat(sut.uiState.value, equalTo(LoadingUiState))

            // run
            runCurrent()
            assertThat(
                sut.uiState.value,
                samePropertyValuesAs(ErrorUiState(R.string.generic_error_message)),
            )
        }

    @Test
    fun `on contact permission denied return the correct ui state`() =
        runTest {
            setupFakes(permission = false)
            setupSut()
            runCurrent()
            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))

            sut.handleEvent(ContactPermissionDeniedEvent)
            runCurrent()
            assertThat(sut.uiState.value, equalTo(UserDeniedPermissionUiState))
        }

    @Test
    fun `when user agrees to ask for permission again return the correct ui state`() =
        runTest {
            setupFakes(permission = false)
            setupSut()
            runCurrent()
            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))

            sut.handleEvent(TryPermissionAgainEvent)
            runCurrent()
            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))
        }

    @Test
    fun `when user does not agree to ask for permission again return the correct ui state`() =
        runTest {
            setupFakes(permission = false)
            setupSut()
            runCurrent()
            assertThat(sut.uiState.value, equalTo(ShowRequestPermissionDialogUiState))

            sut.handleEvent(DoNotTryPermissionAgainEvent)
            runCurrent()
            assertThat(sut.uiState.value, equalTo(TerminalUiState))
        }

    private fun setupSut() {
        sut =
            ImportContactsViewModel(
                savePlayersUseCase = fakeSavePlayersUseCase,
                getContactsUseCase = fakeFakeGetContactsUseCase,
                contactsPermission = fakePermission,
            )
    }

    private fun setupFakes(
        contacts: List<Contact> = fixture.createList(),
        permission: Boolean,
    ) {
        fakeFakeGetContactsUseCase.contacts = contacts
        fakePermission.hasPermission = permission
    }

    private fun List<Contact>.toUiState(): List<ContactItemUiState> =
        listOf(
            createContactItemUiState(
                name = this[0].name,
                contactId = this[0].contactId,
            ),
            createContactItemUiState(
                name = this[1].name,
                contactId = this[1].contactId,
            ),
            createContactItemUiState(
                name = this[2].name,
                contactId = this[2].contactId,
            ),
        )

    fun createContactItemUiState(
        name: String,
        contactId: Long,
    ): ContactItemUiState =
        fixture
            .build<ContactItemUiState>()
            .copy(name = name, contactId = contactId)
}

class FakeGetContactsUseCase : GetContactsUseCase {
    var contacts: List<Contact> = listOf()

    override suspend fun execute(): List<Contact> = contacts
}

class FakeSavePlayersUseCase : SavePlayersUseCase {
    var exception: RuntimeException? = null

    override suspend fun execute(selectedContacts: Set<Long>) {
        if (exception != null) {
            throw exception!!
        }
    }
}

class FakePermission : Permission {
    var hasPermission: Boolean = false

    override val name: String
        get() = "Contacts permission"

    override fun hasPermission(): Boolean = hasPermission
}
