package se.umu.nien1121.truthgame.controller

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import se.umu.nien1121.truthgame.R
import se.umu.nien1121.truthgame.databinding.FragmentQuestionListBinding
import se.umu.nien1121.truthgame.model.GameViewModel
import se.umu.nien1121.truthgame.model.Question
import se.umu.nien1121.truthgame.setSupportActionBar

/**
 * Fragment which presents all [Question] objects in database.
 */
class QuestionListFragment : Fragment() {

    /**
     * Shared ViewModel owned by [MainActivity]
     */
    private val gameViewModel: GameViewModel by activityViewModels()

    //Adapter for recyclerview
    private var adapter: QuestionAdapter? = QuestionAdapter(emptyList())

    //ViewBinding
    private var _binding: FragmentQuestionListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionListBinding.inflate(inflater, container, false)

        //Set LayoutManager to manage animations of recyclerview
        binding.questionRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Set actionbar and view listeners
        setSupportActionBar("My questions", requireActivity(), viewLifecycleOwner)
        setListeners()
    }

    /**
     * Sets view listeners
     */
    private fun setListeners() {
        //Launch dialog for adding questions
        binding.addQuestionButton.setOnClickListener {
            AddQuestionDialogFragment.newInstance()
                .show(this@QuestionListFragment.parentFragmentManager, "")
        }

        //Observe database livedata and update question list
        gameViewModel.questionListLiveData.observe(
            viewLifecycleOwner
        ) { questions ->
            questions?.let {
                //Reverse list to make most recently created Question appear first
                updateUI(it.asReversed())
            }
        }
    }

    /**
     * Updates list of [Question] via [adapter]
     */
    private fun updateUI(questions: List<Question>) {
        //Toggle visibility of text if list is empty
        if (questions.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.questionRecyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.questionRecyclerView.visibility = View.VISIBLE
            adapter = QuestionAdapter(questions)
            binding.questionRecyclerView.adapter = adapter
        }
    }

    /**
     * Custom [RecyclerView.Adapter] to be used in RecyclerView
     */
    private inner class QuestionAdapter(var questions: List<Question>) :
        RecyclerView.Adapter<QuestionListFragment.QuestionHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): QuestionListFragment.QuestionHolder {
            //Inflate via custom ViewHolder
            return QuestionHolder(
                layoutInflater.inflate(
                    R.layout.list_item_question,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: QuestionListFragment.QuestionHolder, position: Int) {
            holder.bind(questions[position])
        }

        override fun getItemCount() = questions.size

    }

    /**
     * Custom [RecyclerView.ViewHolder]. Implements [View.OnLongClickListener].
     */
    private inner class QuestionHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnLongClickListener {
        private lateinit var question: Question
        private val questionTextView: TextView = itemView.findViewById(R.id.question_textView)

        init {
            itemView.setOnLongClickListener(this)
        }

        fun bind(question: Question) {
            this.question = question
            questionTextView.text = this.question.content
        }

        override fun onLongClick(p0: View?): Boolean {
            //Launch deletion dialog
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete question")
                .setMessage(
                    "Are you sure you want to delete this question?"
                )
                .setNegativeButton("No") { _, _ ->
                    //Do nothing
                }
                .setPositiveButton("Yes") { _, _ ->
                    //Remove player and update list
                    gameViewModel.deleteQuestion(question)
                }
                .show()
            return true
        }

    }

    companion object {
        fun newInstance(): QuestionListFragment {
            return QuestionListFragment()
        }
    }
}