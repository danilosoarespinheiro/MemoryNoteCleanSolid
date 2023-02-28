package com.example.memorynotes.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.memorynotes.databinding.FragmentListBinding
import com.example.memorynotes.framework.ListViewModel

class ListFragment : Fragment(), ListAction {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val notesListAdapter = NotesListAdapter(arrayListOf(), this)
    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.noteListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notesListAdapter
        }
        binding.addNote.setOnClickListener { goToNoteDetails() }
        observeViewModel()
    }

    private fun goToNoteDetails(id: Long = 0L) {
        val action: NavDirections = ListFragmentDirections.actionGoToNote(id)
        Navigation.findNavController(binding.noteListView).navigate(action)
    }

    private fun observeViewModel() {
        viewModel.notes.observe(viewLifecycleOwner) {
            binding.loadingView.visibility = View.GONE
            binding.noteListView.visibility = View.VISIBLE
            notesListAdapter.updateNotes(it.sortedBy { note -> note.updateTime })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getNotes()
    }

    override fun onClick(id: Long) {
        goToNoteDetails(id)
    }
}