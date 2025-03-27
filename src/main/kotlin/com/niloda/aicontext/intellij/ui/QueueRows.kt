package com.niloda.aicontext.intellij.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niloda.aicontext.intellij.ui.BuildConfig.debugBorder
import com.niloda.aicontext.intellij.uibridge.Facade
import com.niloda.aicontext.model.IProject
import com.niloda.aicontext.model.QueueItem
import com.niloda.aicontext.model.QueueItem.Status
import com.niloda.aicontext.model.SendToAi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

