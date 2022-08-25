package se.umu.nien1121.truthgame.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import se.umu.nien1121.truthgame.databinding.FragmentAddQuestionDialogBinding
import se.umu.nien1121.truthgame.model.Question
import se.umu.nien1121.truthgame.model.QuestionViewModel

//Constants
private const val QUESTION_TEXT_KEY = "se.umu.nien1121.questionText"

/**
 * Dialog used for adding a [Question] to the database, launched by [QuestionListFragment]
 */
class AddQuestionDialogFragment : DialogFragment() {

    //Shared ViewModel
    private val questionViewModel: QuestionViewModel by activityViewModels()

    //ViewBinding
    private var _binding: FragmentAddQuestionDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddQuestionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Reads written text from saved state and calls [setListeners]
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            binding.textInputLayoutQuestion.editText!!.setText("")
            binding.textInputLayoutQuestion.editText!!.append(
                savedInstanceState.getString(
                    QUESTION_TEXT_KEY
                )
            )
        }

        setListeners()
    }

    /**
     * Helper function that configures listeners for both buttons in dialog
     */
    private fun setListeners() {
        binding.okButton.setOnClickListener {
            //Get written text
            val text = binding.textInputLayoutQuestion.editText!!.text.toString()

            //Input validate and create question
            if (text.isNotEmpty()) {
                val question = Question(content = text)
                questionViewModel.addQuestion(question)
                dismiss()
            } else {
                binding.textInputLayoutQuestion.error = "Enter question"
            }
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    /**
     * Places any written text in [outState], should user rotate screen
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            QUESTION_TEXT_KEY,
            binding.textInputLayoutQuestion.editText!!.text.toString()
        )
    }

    companion object {
        fun newInstance(): AddQuestionDialogFragment {
            return AddQuestionDialogFragment()
        }
    }
}