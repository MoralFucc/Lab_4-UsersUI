package com.topic2.android.notes

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.topic2.android.notes.routing.NotesRouter
import com.topic2.android.notes.routing.Screen
import com.topic2.android.notes.theme.NotesTheme
import com.topic2.android.notes.viewmodel.MainViewModelFactory
import com.topic2.android.notes.ui.screens.NotesScreen
import com.topic2.android.notes.ui.screens.SaveNoteScreen
import com.topic2.android.notes.ui.screens.TrashScreen
import com.topic2.android.notes.viewmodel.MainViewModel


/**
 * Main activity приложения.
 */

class MainActivity : AppCompatActivity() {
  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as NotesApplication).dependencyInjector.repository
    )
  })

  @ExperimentalMaterialApi
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      NotesTheme {
        MainActivityScreen(viewModel = viewModel)
      }
    }
  }
}
@Composable
@ExperimentalMaterialApi
private fun MainActivityScreen(viewModel: MainViewModel){
  Surface {
    when (NotesRouter.currentScreen){
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.SaveNote -> SaveNoteScreen(viewModel)
      is Screen.Trash -> TrashScreen(viewModel)
    }

  }
}