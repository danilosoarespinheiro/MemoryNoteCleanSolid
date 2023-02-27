package com.example.memorynotes.presentation

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.core.data.Note
import com.example.memorynotes.databinding.FragmentNoteBinding
import com.example.memorynotes.framework.NoteViewModel

class NoteFragment : Fragment() {

    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: NoteViewModel
    private var currentNote = Note("", "", 0L, 0L)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

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
    }

    private fun hideKeyboard() {
        val inv = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inv.hideSoftInputFromWindow(binding.titleView.windowToken, 0)
    }

    private fun printToasts(msg: String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}