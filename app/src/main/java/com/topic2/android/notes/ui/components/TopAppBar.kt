package com.topic2.android.notes.ui.components

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topic2.android.notes.R
import com.topic2.android.notes.domain.model.ColorModel
import com.topic2.android.notes.domain.model.NEW_NOTE_ID
import com.topic2.android.notes.domain.model.NoteModel
import com.topic2.android.notes.routing.NotesRouter
import com.topic2.android.notes.routing.Screen
import com.topic2.android.notes.ui.fromHex
import kotlinx.coroutines.launch
import com.topic2.android.notes.viewmodel.MainViewModel

@Composable
@ExperimentalMaterialApi
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun SaveNoteScreen(viewModel: MainViewModel){
    val noteEntry: NoteModel by viewModel.noteEntry.observeAsState(NoteModel())
    val colors: List<ColorModel> by viewModel.colors
        .observeAsState(listOf())
    val bottomDrawerState: BottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val moveNoteToTrashDialogShownState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }
    BackHandler(
        onBack = {
            if(bottomDrawerState.isOpen){
                coroutineScope.launch{ bottomDrawerState.close()}
            } else{
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    )
    Scaffold(topBar = {
        val isEditingMode: Boolean = noteEntry.id != NEW_NOTE_ID
        SaveNoteTopAppBar(
            isEditingMode = isEditingMode,
            onBackClick = { NotesRouter.navigateTo(Screen.Notes)
            },
            onSaveNoteClick ={
                viewModel.saveNote(noteEntry)
            },
            onOpenColorPickerClick = {},
            onDeleteNoteClick = {
                viewModel.moveNoteToTrash(noteEntry)
            }
        )
    },
        content = {
            SaveNoteContent(
                note = noteEntry,
                onNoteChange = {updateNoteEntry->
                    viewModel.onNoteEntryChange(updateNoteEntry)
                }
            )
        }
    )
}



@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel)->Unit)
{
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Color picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(
                colors.size
            ){
                    itemIndex ->
                val color = colors[itemIndex]
                ColorItem(color = color, onColorSelect = onColorSelect )
            }
        }
    }
}
@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel)->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = { onColorSelect(color) }
            )
    ){
        NoteColor(modifier = Modifier
            .padding(10.dp), color= Color.fromHex(color.hex), size = 80.dp,
            border = 2.dp
        )
        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(CenterVertically)
        )
    }
}
@Preview
@Composable

fun ColorItemPreview(){
    ColorItem(ColorModel.DEFAULT){}
}

@Preview
@Composable
fun ColorPickerPreview(){
    ColorPicker(colors = listOf(
        ColorModel.DEFAULT,
        ColorModel.DEFAULT,
        ColorModel.DEFAULT
    ) ){}
}

@Composable
private fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPickerClick:()-> Unit,
    onDeleteNoteClick: ()->Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Note",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colors.onPrimary,
                    contentDescription = "Save Note Button"
                )
            }
            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_color_lens_24),
                    contentDescription = "Add Note Button"
                )
            }
        }
    )
    if (isEditingMode){
        IconButton(onClick = onDeleteNoteClick) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Note Button",
                tint = MaterialTheme.colors.onPrimary
            )
        }
    }
}
@Composable
private fun SaveNoteContent(
    note:NoteModel,
    onNoteChange: (NoteModel)->Unit
){
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(label = "Title",
            text =note.title,
            onTextChange = { newTitle ->
                onNoteChange.invoke(note.copy(title = newTitle))
            }
        )
        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Baby",
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))
            }
        )
        val canBeCheckedOff: Boolean = note.isCheckedOff !=null
        NotesCheckOption(isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null
                onNoteChange.invoke(note.copy(isCheckedOff=isCheckedOff))
            }
        )


        PickedColor(color = note.color)
    }
}
@Preview
@Composable

fun NoteCheckOptionPreview(){
    NotesCheckOption(false ){}
}

@Composable
private fun NotesCheckOption(
    isChecked:Boolean,
    onCheckedChange:(Boolean)->Unit
){
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ){
        Text(
            text ="Can note be checked off?", modifier = Modifier.weight(1f)
        )
        Switch(checked = isChecked, onCheckedChange = onCheckedChange, modifier = Modifier.padding(start = 8.dp))
    }
}


@Preview
@Composable
fun SaveNoteTopAppBarPreview() {
    SaveNoteTopAppBar(
        isEditingMode = true,
        onBackClick = {},
        onSaveNoteClick = {},
        onOpenColorPickerClick = {},
        onDeleteNoteClick = {}
    )
}
@Preview
@Composable
fun SaveNoteContentPreview(){
    SaveNoteContent(
        note = NoteModel(title = "Title", content = "content"),
        onNoteChange = {})
}
@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
){
    TextField(value = text,
        onValueChange = onTextChange,
        label = { Text(label)},
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}
@Preview
@Composable
fun ContentTextFieldPreview(){
    ContentTextField(label = "Title",
        text =" ",
        onTextChange = {})
}

@Composable
private fun PickedColor(color:ColorModel){
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ){
        Text(
            text = "Picked color", modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
        )
        NoteColor(color = Color.fromHex(color.hex), size = 40.dp, border =1.dp,
            modifier = Modifier.padding(4.dp))
    }
}
@Preview
@Composable
fun PickedColorPreview(){
    PickedColor(ColorModel.DEFAULT)

}

//READ ME lab 7 last change