package se.umu.nien1121.truthgame.controller

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import se.umu.nien1121.truthgame.databinding.FragmentRulesBinding
import se.umu.nien1121.truthgame.setSupportActionBar

/**
 * Fragment which presents the rules of TruthGame. Presented in both [MainActivity] and [WelcomeActivity].
 */
class RulesFragment : Fragment() {

    //ViewBinding
    private var _binding: FragmentRulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRulesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //If fragment is presented from Lobby, set actionbar
        if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
            setSupportActionBar("Rules", requireActivity(), viewLifecycleOwner)
        }
    }

    companion object {
        fun newInstance(): RulesFragment {
            return RulesFragment()
        }
    }
}