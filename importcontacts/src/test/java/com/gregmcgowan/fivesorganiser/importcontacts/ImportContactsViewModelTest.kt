package com.gregmcgowan.fivesorganiser.importcontacts

import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.core.NO_STRING_RES_ID
import com.gregmcgowan.fivesorganiser.core.permissions.Permission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUiEvent.RequestPermission
import com.gregmcgowan.fivesorganiser.importcontacts.ImportContactsUserEvent.*
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import com.gregmcgowan.fivesorganiser.test_shared.createList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

private val LOADING_UI_MODEL = ImportContactsUiModel(
        contacts = emptyList(),
        showLoading = true,
        showContent = false,
        importContactsButtonEnabled = false,
        errorMessage = NO_STRING_RES_ID
)

class ImportContactsViewModelTest {

    // StandardTestDispatcher does not run coroutines by default. So we can control the execution
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule(testDispatcher)

    private lateinit var fixture: JFixture

    lateinit var fakeSavePlayersUseCase: FakeSavePlayersUseCase
    lateinit var fakeUiModelMapper: FakeUiModelMapper
    lateinit var fakePermission: FakePermission
    lateinit var fakeFakeGetContactsUseCase: FakeGetContactsUseCase

    private lateinit var sut: ImportContactsViewModel

    @Before
    fun setUp() {
        fixture = JFixture()
        fixture.customise().lazyInstance(ContactItemUiModel::class.java) {
            ContactItemUiModel(
                    name = fixture.build(),
                    isSelected = false,
                    contactId = fixture.build()
            )
        }

        fakeFakeGetContactsUseCase = FakeGetContactsUseCase()
        fakeUiModelMapper = FakeUiModelMapper()
        fakePermission = FakePermission()
        fakeSavePlayersUseCase = FakeSavePlayersUseCase()
    }


    @Test
    fun `init() when permission is granted shows loading then content`() = runTest {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()

        // verify loading UI
        val actualLoadingOutput = sut.uiModel
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        // run
        runCurrent()

        // verify content
        val actualContentOutput = sut.uiModel
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }

    @Test
    fun `init() without permission sends request permission event`() = runTest {
        setupFakes(permission = false)
        setupSut()

        // run
        val output: ImportContactsUiEvent = sut.importContactsUiEvent.first()
        runCurrent()

        // verify
        assertThat(output as RequestPermission, equalTo(RequestPermission))
    }

    @Test
    fun `onContactsPermissionGranted() loads contacts`() = runTest {
        // setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = false)
        setupSut()

        // verify loading
        val actualLoadingOutput = sut.uiModel
        assertThat(actualLoadingOutput, equalTo(LOADING_UI_MODEL))

        // run on contact permission granted
        sut.handleEvent(ContactPermissionGrantedEvent)
        runCurrent()

        // verify output
        val actualContentOutput = sut.uiModel
        assertThat(actualContentOutput, equalTo(fixtInitialUiModel))
    }


    @Test
    fun `onContactSelected() updates model when one is selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val actualInitialUiModel = sut.uiModel

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // check ui model is updated correctly
        val expectedContactUiModelList = createSelectedContacts(actualInitialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel,
                equalTo(actualInitialUiModel.copy(
                        contacts = expectedContactUiModelList,
                        importContactsButtonEnabled = true)
                ))
    }

    @Test
    fun `onContactSelected() updates model when some are already are selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiModel

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
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedContactUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onContactDeselected() when only 1 is already selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiModel

        // add contact
        val firstContactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(firstContactId, true))
        runCurrent()

        // deselect
        sut.handleEvent(ContactSelectedEvent(firstContactId, false))
        runCurrent()

        // check that the ui model is back to initial
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(importContactsButtonEnabled = false)))
    }

    @Test
    fun `onContactDeselected() when there is more than 1 selected`() = runTest {
        // initial setup
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiModel

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
        val expectedUiModelList = createSelectedContacts(initialUiModel, selectedContacts = setOf(1))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(contacts = expectedUiModelList,
                importContactsButtonEnabled = true)))
    }

    @Test
    fun `onAddButtonPressed() saves contacts and close screens`() = runTest {
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiModel

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // check close screen event
        sut.handleEvent(AddButtonPressedEvent)
        val actualContentOutput = sut.importContactsUiEvent.first()
        runCurrent()

        assertThat(actualContentOutput as ImportContactsUiEvent.CloseScreen,
                equalTo(ImportContactsUiEvent.CloseScreen))

        // check models
        val expectedUiModelList = createSelectedContacts(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(
                contacts = expectedUiModelList,
                showLoading = true,
                showContent = false,
                importContactsButtonEnabled = true
        )))

    }

    @Test
    fun `onAddButtonPressed() when there is an error`() = runTest {
        val fixtInitialUiModel = createInitialUiModel()
        setupFakes(uiModel = fixtInitialUiModel, permission = true)
        setupSut()
        runCurrent()

        val initialUiModel = sut.uiModel

        // add contact
        val contactId = fixtInitialUiModel.contacts[0].contactId
        sut.handleEvent(ContactSelectedEvent(contactId, true))
        runCurrent()

        // run
        val runTimeException: RuntimeException = fixture.build()
        fakeSavePlayersUseCase.exception = runTimeException
        sut.handleEvent(AddButtonPressedEvent)
        runCurrent()

        // check models
        val expectedUiModelList = createSelectedContacts(initialUiModel, selectedContacts = setOf(0))
        val actualUpdatedModel = sut.uiModel
        assertThat(actualUpdatedModel, equalTo(initialUiModel.copy(
                contacts = expectedUiModelList,
                showLoading = false,
                showContent = true,
                importContactsButtonEnabled = true
        )))

    }

    private fun setupSut() {
        sut = ImportContactsViewModel(
                fakeUiModelMapper,
                fakeSavePlayersUseCase,
                fakeFakeGetContactsUseCase,
                fakePermission
        )
    }

    private fun setupFakes(contacts: List<Contact> = fixture.createList(),
                           uiModel: ImportContactsUiModel = createInitialUiModel(),
                           permission: Boolean) {
        fakeFakeGetContactsUseCase.contacts = contacts.toMutableList()
        fakeUiModelMapper.uiModel = uiModel
        fakePermission.hasPermission = permission
    }

    private fun createInitialUiModel(): ImportContactsUiModel =
            ImportContactsUiModel(
                    contacts = fixture.createList(),
                    showLoading = false,
                    showContent = true,
                    importContactsButtonEnabled = false
            )

    private fun createSelectedContacts(initialUiModel: ImportContactsUiModel,
                                       selectedContacts: Set<Int> = emptySet()): List<ContactItemUiModel> {
        return initialUiModel.contacts
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

class FakeUiModelMapper : ImportContactsUiModelMapper {

    lateinit var uiModel: ImportContactsUiModel

    override fun map(contacts: List<Contact>, selectedContacts: Set<Long>): ImportContactsUiModel =
            this.uiModel

}

class FakePermission : Permission {

    var hasPermission: Boolean = false

    override val name: String
        get() = "Contacts permission"

    override fun hasPermission(): Boolean = hasPermission

}
