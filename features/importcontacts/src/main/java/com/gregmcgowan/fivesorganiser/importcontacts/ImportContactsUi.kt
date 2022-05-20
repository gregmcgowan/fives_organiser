package com.gregmcgowan.fivesorganiser.importcontacts

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gregmcgowan.fivesorganiser.core.compose.AppTheme
import com.gregmcgowan.fivesorganiser.core.compose.Loading
import com.gregmcgowan.fivesorganiser.core.compose.rememberStateWithLifecycle
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


@Composable
fun ImportContactsScreen(exitScreenHandler: () -> Unit) {
    val importContactsViewModel: ImportContactsViewModel = hiltViewModel()
    val uiState by rememberStateWithLifecycle(importContactsViewModel.uiStateFlow)

    ImportContactsScreen(
            uiState,
            userEventHandler = { event -> importContactsViewModel.handleEvent(event) },
            exitScreenHandler = exitScreenHandler
    )
}


@Composable
fun ImportContactsScreen(importContactsUiState: ImportContactsUiState,
                         userEventHandler: (ImportContactsUserEvent) -> Unit,
                         exitScreenHandler: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { result ->
        val event = if (result) ContactPermissionGrantedEvent else ContactPermissionDeniedEvent
        userEventHandler.invoke(event)
    }

    BackHandler(onBack = exitScreenHandler)

    when (importContactsUiState) {
        is LoadingUiState -> Loading()
        is ShowRequestPermissionDialogUiState -> {
            SideEffect { launcher.launch(Manifest.permission.READ_CONTACTS) }
        }
        is UserDeniedPermissionUiState -> {
            NoPermissionGrantedUi(userEventHandler)
        }
        is ContactsListUiState -> {
            ContactListUi(exitScreenHandler, importContactsUiState, userEventHandler)
        }
        is ErrorUiState -> {
            Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 8.dp)
                    .wrapContentSize(Alignment.Center)) {
                Text(stringResource(id = importContactsUiState.errorMessage))
            }
        }
        is TerminalUiState -> {
            SideEffect { exitScreenHandler.invoke() }
        }
    }
}

@Composable
fun NoPermissionGrantedUi(userEventHandler: (ImportContactsUserEvent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.size(32.dp))
        Text(stringResource(id = R.string.permissions_denied_text), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.size(16.dp))
        Button(
                modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp),
                content = { Text(stringResource(id = R.string.permissions_denied_request_permission_again)) },
                onClick = { userEventHandler.invoke(TryPermissionAgainEvent) }
        )
        Spacer(modifier = Modifier.size(16.dp))
        Button(
                modifier = Modifier
                        .align(CenterHorizontally)
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp),
                content = { Text(stringResource(id = R.string.permissions_denied_no)) },
                onClick = { userEventHandler.invoke(DoNotTryPermissionAgainEvent) }
        )
    }
}

@Composable
private fun ContactListUi(exitScreenHandler: () -> Unit, importContactsUiState: ContactsListUiState,
                          userEventHandler: (ImportContactsUserEvent) -> Unit) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text(text = stringResource(id = R.string.import_contacts_title))
                        },
                        navigationIcon = {
                            IconButton(onClick = exitScreenHandler) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription =
                                        stringResource(R.string.navigate_up_content_description),
                                )
                            }
                        }
                )
            },
            content = {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.weight(1.0f)) {
                        LazyColumn {
                            items(importContactsUiState.contacts) { contact ->
                                ContactItem(contact, userEventHandler)
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                    .padding(start = 48.dp, end = 48.dp, bottom = 16.dp)
                                    .fillMaxWidth()
                    ) {
                        AnimatedVisibility(importContactsUiState.addContactsButtonEnabled) {
                            Button(
                                    modifier = Modifier.fillMaxWidth(),
                                    content = { Text(stringResource(id = R.string.import_contacts_add)) },
                                    onClick = { userEventHandler.invoke(AddButtonPressedEvent) },
                            )
                        }

                    }
                }
            })
}


@Composable
fun ContactItem(contact: ContactItemUiState,
                eventHandler: (ImportContactsUserEvent) -> Unit) {
    Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "ContactItem-${contact.name}" }
                    .padding(top = 4.dp, bottom = 4.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = {
                BiggerBadge(
                        backgroundColor = MaterialTheme.colors.secondary,
                        content = {
                            Text(
                                    text = contact.name.substring(0, 1).uppercase(),
                                    fontSize = 20.sp,
                                    color = Color.White
                            )
                        },
                )
                Text(
                        text = contact.name,
                        modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp))
                Checkbox(
                        checked = contact.isSelected,
                        modifier = Modifier.padding(start = 16.dp),
                        onCheckedChange = { selected ->
                            eventHandler.invoke(ContactSelectedEvent(contact.contactId, selected))
                        }
                )
            }
    )
}

@Composable
fun BiggerBadge(modifier: Modifier = Modifier,
                backgroundColor: Color,
                radius: Dp = 18.dp,
                content: @Composable (RowScope.() -> Unit)? = null) {
    val shape = RoundedCornerShape(radius)
    Row(
            modifier = modifier
                    .defaultMinSize(minWidth = radius * 2, minHeight = radius * 2)
                    .background(color = backgroundColor, shape = shape)
                    .clip(shape)
                    .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
    ) {
        if (content != null) {
            content()
        }
    }
}

@Preview
@Composable
fun NoPermissionGrantedPreview() {
    AppTheme {
        NoPermissionGrantedUi {}
    }

}

@Preview
@Composable
fun ContactListPreview() {
    AppTheme {
        ImportContactsScreen(
                ContactsListUiState(
                        listOf(
                                ContactItemUiState(name = "Greg", isSelected = true, contactId = 1),
                                ContactItemUiState(name = "Frances", isSelected = true, contactId = 2),
                                ContactItemUiState(name = "Joe Wicks", isSelected = true, contactId = 3)
                        ),
                        addContactsButtonEnabled = true,
                ),
                { }, { }
        )
    }
}
