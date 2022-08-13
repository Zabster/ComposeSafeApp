@file:OptIn(ExperimentalMaterialApi::class)

package com.zabster.safeappcompose.screens.main.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zabster.safeappcompose.R
import com.zabster.safeappcompose.screens.common.models.CategoryModel
import com.zabster.safeappcompose.screens.common.models.CredentialModel
import com.zabster.safeappcompose.screens.main.models.MainScreenEvents
import com.zabster.safeappcompose.screens.main.models.PasswordDialogKey
import com.zabster.safeappcompose.ui.dialog.view.ShowSimpleDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val bottomSheetStateState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    ObserveStates(viewModel, scaffoldState)
    ModalBottomSheetLayout(
        sheetState = bottomSheetStateState,
        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        sheetContent = { CategoryListSheetContent(viewModel) }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState,
            bottomBar = { BottomBar(viewModel, bottomSheetStateState, coroutineScope) },
            floatingActionButton = { ActionButton(viewModel) },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
        ) { paddingValues ->
            PasswordList(paddingValues, viewModel)
        }
        BackHandler(bottomSheetStateState.isVisible) {
            coroutineScope.launch {
                bottomSheetStateState.hide()
            }
        }
    }
}

@Composable
private fun PasswordList(paddingValues: PaddingValues, viewModel: MainViewModel) {
    AnimatedVisibilityLoading(
        visible = viewModel.loadingState,
        loadingContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter))
            }
        },
        content = {
            LazyColumn(
                modifier = Modifier,
                contentPadding = paddingValues,
                content = {
                    items(viewModel.credentialListState) { model ->
                        ItemCell(
                            model = model,
                            onItemClick = {
                                viewModel.sendEvent(MainScreenEvents.ShowPassword(model))
                            },
                            onDelete = {
                                viewModel.sendEvent(MainScreenEvents.OnCredentialRemove(model))
                            },
                            onEdit = {
                                viewModel.sendEvent(MainScreenEvents.OnCredentialUpdate(model))
                            }
                        )
                    }
                }
            )
        }
    )
}

@Composable
private fun ItemCell(
    model: CredentialModel,
    onItemClick: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val coroutineScope = rememberCoroutineScope()

    val actionSize = 56 // размер эдемента с экшеном
    val actionPaddingHorizontal = 16 // паддинг по бокам
    val actionItemCount = 2 // кол-во кнопок
    val cardOffset =
        actionSize * actionItemCount + actionPaddingHorizontal * 2 // размер сдвига по горизонтали

    val sizePx = with(LocalDensity.current) { cardOffset.dp.toPx() } // сдвиг в пикселях
    val anchors = mapOf(0f to 0, -sizePx to 1)

    Box(
        modifier = Modifier
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                orientation = Orientation.Horizontal,
                thresholds = { _, _ -> FixedThreshold(cardOffset.dp) },
            )
    ) {
        CredentialActions(
            actionItemHeight = actionSize.dp,
            actionItemHorizontalPadding = actionPaddingHorizontal.dp,
            swipeableState = swipeableState,
            modifier = Modifier.align(Alignment.CenterEnd),
            onDelete = {
                onDelete.invoke()
                coroutineScope.launch {
                    swipeableState.snapTo(0)
                }
            },
            onEdit = onEdit::invoke
        )
        Row(
            content = { CredentialItem(Modifier.clickable(onClick = onItemClick), model) },
            modifier = Modifier.offset {
                IntOffset(swipeableState.offset.value.roundToInt(), 0)
            }
        )
    }
}

@Composable
private fun CredentialActions(
    actionItemHeight: Dp,
    actionItemHorizontalPadding: Dp,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    swipeableState: SwipeableState<Int>,
    modifier: Modifier = Modifier,
) {
    val transition = updateTransition(
        targetState = swipeableState.offset.value.roundToInt(),
        label = "actionTransition"
    )

    val animSize = transition.animateDp(label = "actionAnim") { offsetValue ->
        with(LocalDensity.current) {
            val newDp = abs(offsetValue.div(2)).toDp()
            return@animateDp if (newDp < actionItemHeight) newDp else actionItemHeight
        }
    }

    Row(
        modifier.padding(horizontal = actionItemHorizontalPadding, vertical = 8.dp)
    ) {
        IconButton(
            modifier = Modifier.size(animSize.value),
            onClick = onEdit::invoke
        ) {
            Icon(imageVector = Icons.Filled.ChangeCircle, contentDescription = "Update credential")
        }
        IconButton(
            modifier = Modifier.size(animSize.value),
            onClick = onDelete::invoke
        ) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete credential")
        }
    }
}

@Composable
private fun CredentialItem(
    modifier: Modifier,
    model: CredentialModel,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(8.dp)

    ) {
        Text(text = model.title, style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(4.dp))
        if (model.subtitle.isNotBlank()) {
            Text(text = model.subtitle, style = MaterialTheme.typography.subtitle2)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Divider()
    }
}

@Composable
private fun BottomBar(
    viewModel: MainViewModel,
    bottomSheetStateState: ModalBottomSheetState,
    coroutineScope: CoroutineScope
) {
    BottomAppBar(cutoutShape = CircleShape) {
        IconButton(
            onClick = {
                coroutineScope.launch {
                    bottomSheetStateState.show()
                }
            }
        ) { Icon(Icons.Filled.Sort, contentDescription = "") }
        Spacer(Modifier.weight(1f, true))
//        IconButton(
//            onClick = {
//                viewModel.sendEvent(MainScreenEvents.NavigateToSettings)
//            }
//        ) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
    }
}

@Composable
private fun ActionButton(viewModel: MainViewModel) {
    FloatingActionButton(
        modifier = Modifier.rotate(45f),
        onClick = {
            viewModel.sendEvent(MainScreenEvents.NavigateToCreateCredential)
        }
    ) {
        Icon(Icons.Filled.Close, contentDescription = "Add new credential")
    }
}

@Composable
private fun CategoryListSheetContent(viewModel: MainViewModel) {
    Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(id = R.string.category_list_new_category_title)
    )
    Spacer(modifier = Modifier.height(4.dp))
    LazyVerticalGrid(
        modifier = Modifier.padding(bottom = 8.dp),
        columns = GridCells.Fixed(4),
        content = {
            items(viewModel.categoryListState) { model ->
                CategoryItem(
                    categoryModel = model,
                    action = {
                        viewModel.sendEvent(MainScreenEvents.LoadCredentialByCategory(model))
                    },
                    removeAction = {
                        viewModel.sendEvent(MainScreenEvents.RemoveCategory(model))
                    }
                )
            }
        }
    )
}

@Composable
private fun CategoryItem(
    categoryModel: CategoryModel,
    action: () -> Unit,
    removeAction: () -> Unit
) {
    Chip(modifier = Modifier.padding(4.dp), onClick = action::invoke) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.weight(1f, false),
                text = categoryModel.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!categoryModel.isDefault) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(MaterialTheme.colors.secondary, CircleShape)
                ) {
                    Image(
                        modifier = Modifier
                            .size(12.dp)
                            .clickable(onClick = removeAction::invoke),
                        imageVector = Icons.Filled.Close,
                        contentDescription = "remove category"
                    )
                }
            }
        }
    }
}

@Composable
private fun ObserveStates(
    viewModel: MainViewModel,
    scaffoldState: ScaffoldState
) {
    val clipboardManager = LocalClipboardManager.current
    val dialogState = remember { viewModel.showPasswordState }
    ShowSimpleDialog(openableState = dialogState, onPositiveClick = { dialogKey ->
        (dialogKey as? PasswordDialogKey)?.let { key ->
            clipboardManager.setText(AnnotatedString(key.password))
            viewModel.sendEvent(MainScreenEvents.ShowMessage(key.copyText))
        }
    })
    viewModel.messageState?.let { msg ->
        LaunchedEffect(key1 = scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
            scaffoldState.snackbarHostState.showSnackbar(msg)
        }
    }
}


@Composable
private fun AnimatedVisibilityLoading(
    visible: Boolean,
    loadingContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(visible = visible) { loadingContent() }
    AnimatedVisibility(visible = !visible) { content() }
}