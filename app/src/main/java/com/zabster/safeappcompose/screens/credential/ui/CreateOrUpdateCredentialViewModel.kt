package com.zabster.safeappcompose.screens.credential.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.di.NavigationDispatcher
import com.zabster.safeappcompose.di.res.ResourceManager
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel
import com.zabster.safeappcompose.screens.common.vm.BaseViewModel
import com.zabster.safeappcompose.screens.credential.domain.CategoryInteractor
import com.zabster.safeappcompose.screens.credential.domain.FetchCredentialInteractor
import com.zabster.safeappcompose.screens.credential.domain.SaveCredentialInteractor
import com.zabster.safeappcompose.screens.credential.models.CredentialEvents
import com.zabster.safeappcompose.screens.utils.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateOrUpdateCredentialViewModel @Inject constructor(
    private val categoryInteractor: CategoryInteractor,
    private val saveCredentialInteractor: SaveCredentialInteractor,
    private val credentialInteractor: FetchCredentialInteractor,
    private val resourceManager: ResourceManager,
    navigationDispatcher: NavigationDispatcher
) : BaseViewModel(navigationDispatcher) {

    val credTitleState = mutableStateOf("")
    val credDescState = mutableStateOf("")
    val credPasswordState = mutableStateOf("")
    val categoryState = mutableStateOf(TextFieldValue())
    val categoryExpandedState = mutableStateOf(false)
    val categoryListState = mutableStateOf(listOf<CategoryModel>())

    var buttonNameState by mutableStateOf("")
        private set
    var loadingState by mutableStateOf(false)
        private set
    var errorMessageState by mutableStateOf("")
        private set
    var validationState by mutableStateOf(false)
        private set

    private val quaeOperationSearching = MutableSharedFlow<suspend () -> Unit>()
    private var noChangesState by mutableStateOf(false)
    private var credentialIdState by mutableStateOf<Long?>(null)

    init {
        observeStates()
    }

    fun sendEvent(events: CredentialEvents) {
        when (events) {
            is CredentialEvents.RequestScreen -> requestScreen(events.id)
            is CredentialEvents.ChooseCategory -> choseCategory(events.categoryModel)
            is CredentialEvents.ShowAllCategories -> showAllCategories()
            is CredentialEvents.ShowCategoriesByName -> showCategoriesByName(events.name)
            is CredentialEvents.Save -> saveCredential()
            is CredentialEvents.Validate -> validate()
        }
    }

    private fun validate() {
        validationState = saveCredentialInteractor.validate(
            credTitleState.value,
            credPasswordState.value,
            categoryState.value.text
        )
    }

    private fun requestScreen(id: Long?) {
        credentialIdState = id
        if (id != null && id >= 0) viewModelScope.launch {
            showLoading()
            val (credential, category) = credentialInteractor.getCredential(id)
            updateData(credential, category)
            hideLoading()
        } else R.string.new_credential_data_save_btn_text

        val buttonName = if (id != null) R.string.new_credential_data_update_btn_text
        else R.string.new_credential_data_save_btn_text
        buttonNameState = resourceManager.getString(buttonName)
    }

    private fun updateData(credential: CredentialModel, category: CategoryModel) {
        noChangesState = true

        credTitleState.value = credential.title
        credDescState.value = credential.subtitle
        credPasswordState.value = credential.passwordHash
        categoryState.value = categoryState.value.copy(
            category.title,
            TextRange(category.title.length),
        )
        categoryExpandedState.value = false
    }

    private fun choseCategory(categoryModel: CategoryModel) {
        noChangesState = true
        categoryState.value = categoryState.value.copy(
            categoryModel.title,
            TextRange(categoryModel.title.length),
        )
        categoryExpandedState.value = false
    }

    private fun saveCredential() = viewModelScope.launch {
        showLoading()
        quaeOperationSearching.emit {
            saveCredentialInteractor.saveCredential(
                id = credentialIdState,
                title = credTitleState.value,
                desc = credDescState.value,
                pass = credPasswordState.value,
                categoryName = categoryState.value.text
            )
        }
    }

    private fun showCategoriesByName(name: String) = viewModelScope.launch {
        if (noChangesState) {
            noChangesState = false
            return@launch
        }
        quaeOperationSearching.emit { categoryInteractor.fetchCategoriesByName(name) }
    }

    private fun showAllCategories() = viewModelScope.launch {
        quaeOperationSearching.emit(categoryInteractor::fetchAllCategories)
    }

    private fun showSavedError() {
        errorMessageState = resourceManager.getString(R.string.error_text_save_error)
    }

    private fun showLoading() {
        loadingState = true
    }

    private fun hideLoading() {
        loadingState = false
    }

    private fun observeStates() {
        viewModelScope.launch {
            categoryInteractor.categoriesList
                .collectLatest { list ->
                    categoryListState.value = list
                    categoryExpandedState.value = list.isNotEmpty()
                }
        }
        viewModelScope.launch {
            quaeOperationSearching
                .debounce(DEBOUNCE_INPUT_TIME_OUT)
                .cancellable()
                .collectLatest { action ->
                    action.invoke()
                }
        }
        viewModelScope.launch {
            saveCredentialInteractor.saveState
                .collectLatest { isSuccess ->
                    hideLoading()
                    if (isSuccess) navigateSingleTo(Screen.MainScreen)
                    else showSavedError()
                }
        }
    }

    companion object {
        private const val DEBOUNCE_INPUT_TIME_OUT = 500L
    }
}