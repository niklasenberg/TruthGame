package se.umu.nien1121.truthgame.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import se.umu.nien1121.truthgame.databinding.FragmentRulesBinding

class RulesFragment : Fragment() {

    //ViewBinding
    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)

        return binding.root
    }

    companion object {
        fun newInstance(): RulesFragment {
            return RulesFragment()
        }
    }
}