package com.zabster.safeappcompose.screens.main.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.zabster.safeappcompose.di.NavigationDispatcher
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel
import com.zabster.safeappcompose.screens.common.vm.BaseViewModel
import com.zabster.safeappcompose.screens.main.domain.*
import com.zabster.safeappcompose.screens.main.models.MainScreenEvents
import com.zabster.safeappcompose.screens.utils.Screen
import com.zabster.safeappcompose.ui.dialog.model.DialogModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val passwordListFetchInteractor: PasswordListFetchInteractor,
    private val categoryListFetchInteractor: CategoryListFetchInteractor,
    private val categoryInteractor: CategoryInteractor,
    private val credentialInteractor: CredentialInteractor,
    private val passwordDialogInteractor: PasswordDialogInteractor,
    navigationDispatcher: NavigationDispatcher
) : BaseViewModel(navigationDispatcher) {

    var credentialListState by mutableStateOf<List<CredentialModel>>(listOf())
        private set

    var categoryListState by mutableStateOf<List<CategoryModel>>(listOf())
        private set

    var loadingState by mutableStateOf(false)
        private set

    var messageState by mutableStateOf<String?>(null)
        private set

    val showPasswordState = mutableStateOf<DialogModel>(DialogModel.HideDialog)

    init {
        fetchAllCredential()
        fetchAllCategories()
        observeStates()
    }

    private fun fetchAllCategories() = viewModelScope.launch {
        categoryListFetchInteractor.fetchAllCategories()
    }

    fun sendEvent(events: MainScreenEvents) {
        when (events) {
            MainScreenEvents.NavigateToCreateCredential -> navigateTo(Screen.CreateCredentialScreen)
            MainScreenEvents.NavigateToSettings -> navigateTo(Screen.SettingsScreen)

            is MainScreenEvents.LoadCredentialByCategory -> fetchByCategory(events.category)
            is MainScreenEvents.RemoveCategory -> removeCategory(events.category)
            is MainScreenEvents.OnCredentialRemove -> removeCredential(events.model)
            is MainScreenEvents.OnCredentialUpdate -> navigateToUpdateCredential(events.model)
            is MainScreenEvents.ShowPassword -> showPassword(events.model)
            is MainScreenEvents.ShowMessage -> showMessage(events.message)
        }
    }

    private fun showMessage(message: String) {
        messageState = message
    }

    private fun showPassword(model: CredentialModel) {
        messageState = null
        showPasswordState.value = passwordDialogInteractor.showPasswordDialog(model.passwordHash)
    }

    private fun navigateToUpdateCredential(model: CredentialModel) {
        navigateTo(Screen.UpdateCredentialScreen(model.id.toString()))
    }

    private fun removeCredential(model: CredentialModel) = viewModelScope.launch {
        credentialInteractor.removeCredential(model.id)
    }

    private fun removeCategory(category: CategoryModel) = viewModelScope.launch {
        categoryInteractor.deleteCategory(category)
    }

    private fun showLoading() {
        loadingState = true
    }

    private fun hideLoading() {
        loadingState = false
    }

    private fun fetchAllCredential() = viewModelScope.launch {
        showLoading()
        fetchByCategory(categoryInteractor.getSelectedCategory())
    }

    private fun fetchByCategory(categoryModel: CategoryModel) = viewModelScope.launch {
        showLoading()
        categoryInteractor.updateSelectedCategory(categoryModel.id)
        if (categoryModel.isDefault) passwordListFetchInteractor.fetchAllPasswords() else {
            passwordListFetchInteractor.fetchPasswordsByCategoryId(categoryModel.id)
        }
    }

    private fun observeStates() {
        viewModelScope.launch {
            passwordListFetchInteractor.credentialsList
                .collectLatest { list ->
                    credentialListState = list
                    hideLoading()
                }
        }
        viewModelScope.launch {
            categoryListFetchInteractor.categoriesList
                .collectLatest { list ->
                    categoryListState = list
                }
        }
    }
}