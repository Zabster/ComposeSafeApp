package com.zabster.safeappcompose.screens.credential.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.screens.credential.models.CredentialEvents
import com.zabster.safeappcompose.screens.utils.LifecycleEvent
import com.zabster.safeappcompose.screens.utils.imeInsets
import com.zabster.safeappcompose.ui.view.InputField
import com.zabster.safeappcompose.ui.view.SelectableInputField

@Composable
fun CreateOrUpdateCredentialScreen(
    navController: NavController,
    id: Long? = null,
    viewModel: CreateOrUpdateCredentialViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    ObserveStates(viewModel, scaffoldState)
    LifecycleEvent { event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> viewModel.sendEvent(CredentialEvents.RequestScreen(id))
            else -> Unit
        }
    }
    Scaffold(
        topBar = {
            TopBar(!viewModel.loadingState) { navController.popBackStack() }
        },
        scaffoldState = scaffoldState
    ) { padding -> Content(padding, viewModel) }
    BackHandler {
        if (!viewModel.loadingState) {
            navController.popBackStack()
        }
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    viewModel: CreateOrUpdateCredentialViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imeInsets(rememberScrollState())
            .padding(padding),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedVisibility(visible = viewModel.loadingState) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        TitleInput(viewModel)
        DescInput(viewModel)
        PasswordInput(viewModel)
        CategoryInput(viewModel)
        Spacer(modifier = Modifier.weight(1f, true))
        Button(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            enabled = !viewModel.loadingState && (viewModel.validationState || viewModel.loadingState),
            onClick = {
                viewModel.sendEvent(CredentialEvents.Save)
            }
        ) { Text(text = viewModel.buttonNameState) }
    }
}

@Composable
private fun TitleInput(viewModel: CreateOrUpdateCredentialViewModel) {
    val (titleText, titleChange) = remember { viewModel.credTitleState }
    InputField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .fillMaxWidth(),
        value = titleText,
        onValueChange = { text ->
            titleChange(text)
            viewModel.sendEvent(CredentialEvents.Validate)
        },
        label = stringResource(id = R.string.new_credential_data_title_hint),
        enabled = !viewModel.loadingState,
        imeAction = ImeAction.Next
    )
}

@Composable
private fun DescInput(viewModel: CreateOrUpdateCredentialViewModel) {
    val (descText, descChange) = remember { viewModel.credDescState }
    InputField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        value = descText,
        onValueChange = descChange,
        label = stringResource(id = R.string.new_credential_data_desc_hint),
        enabled = !viewModel.loadingState,
        imeAction = ImeAction.Next
    )
}

@Composable
private fun PasswordInput(viewModel: CreateOrUpdateCredentialViewModel) {
    val (pasText, pasChange) = remember { viewModel.credPasswordState }
    InputField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        modifierError = Modifier.padding(horizontal = 16.dp),
        value = pasText,
        onValueChange = { text ->
            pasChange(text)
            viewModel.sendEvent(CredentialEvents.Validate)
        },
        label = stringResource(id = R.string.new_credential_data_pass_hint),
        keyboardType = KeyboardType.Password,
        enabled = !viewModel.loadingState,
        imeAction = ImeAction.Next
    )
}

@Composable
private fun CategoryInput(viewModel: CreateOrUpdateCredentialViewModel) {
    val (categoryText, categoryChange) = remember { viewModel.categoryState }
    val categoryExpanded = remember { viewModel.categoryExpandedState }
    val categoryList = remember { viewModel.categoryListState }

    SelectableInputField(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        value = categoryText,
        label = stringResource(id = R.string.new_credential_data_category_hint),
        singleLine = true,
        enabled = !viewModel.loadingState,
        onValueChange = { word ->
            categoryChange(word)
            viewModel.sendEvent(CredentialEvents.Validate)
            if (categoryText.text != word.text) {
                viewModel.sendEvent(CredentialEvents.ShowCategoriesByName(word.text))
            }
        },
        onDoneAction = {
            viewModel.sendEvent(CredentialEvents.Save)
        },
        inputDropDownAction = {
            viewModel.sendEvent(CredentialEvents.ShowAllCategories)
        },
        expanded = categoryExpanded,
        dropDownAction = { category ->
            viewModel.sendEvent(CredentialEvents.ChooseCategory(category))
        },
        dropDownItems = categoryList.value
    )
}

@Composable
private fun TopBar(enabled: Boolean, backAction: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.new_credential_data_screen_title)) },
        navigationIcon = {
            Icon(
                modifier = Modifier.clickable(enabled = enabled, onClick = backAction::invoke),
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "back button"
            )
        }
    )
}

@Composable
private fun ObserveStates(
    viewModel: CreateOrUpdateCredentialViewModel,
    scaffoldState: ScaffoldState
) {
    if (viewModel.errorMessageState.isNotBlank()) {
        LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(viewModel.errorMessageState)
        }
    }
}