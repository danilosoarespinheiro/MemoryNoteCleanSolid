package com.example.memorynotes.presentation

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.core.data.Note
import com.example.memorynotes.R
import com.example.memorynotes.databinding.FragmentNoteBinding
import com.example.memorynotes.framework.NoteViewModel


class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()
    private var currentNote = Note("", "", 0L, 0L)
    private var noteId = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.note_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.deleteNote -> {
                        if (context != null && noteId != 0L) {
                            AlertDialog.Builder(context!!)
                                .setMessage("Are you sure you want to delete this note?")
                                .setTitle("Delete note")
                                .setPositiveButton("Yes") { _, _ ->
                                    viewModel.deleteNote(currentNote)
                                }
                                .setNegativeButton("Cancel") { dialogInterface, _ ->
                                    dialogInterface.cancel()
                                }
                                .create()
                                .show()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        arguments?.let {
            noteId = NoteFragmentArgs.fromBundle(it).noteI
        }

        if (noteId != 0L) viewModel.getNote(noteId)

        binding.apply {
            checkButton.setOnClickListener {
                if (titleView.text.toString() != "" || contentView.text.toString() != "") {
                    val time = System.currentTimeMillis()
                    currentNote.title = titleView.text.toString()
                    currentNote.content = contentView.text.toString()
                    currentNote.updateTime = time
                    if (currentNote.id == 0L) currentNote.creationTime = time
                    viewModel.saveNote(currentNote)
                } else Navigation.findNavController(it).popBackStack()

            }
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.saved.observe(viewLifecycleOwner) {
            if (it) {
                printToasts("Done!")
                hideKeyboard()
                Navigation.findNavController(binding.titleView).popBackStack()
            } else printToasts("Something went wrong, please try again!")
        }

        viewModel.currentNote.observe(viewLifecycleOwner) { note ->
            note?.let {
                currentNote = it
                binding.titleView.setText(it.title, TextView.BufferType.EDITABLE)
                binding.contentView.setText(it.content, TextView.BufferType.EDITABLE)
            }
        }
    }

    private fun hideKeyboard() {
        val inv = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inv.hideSoftInputFromWindow(binding.titleView.windowToken, 0)
    }

    private fun printToasts(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}