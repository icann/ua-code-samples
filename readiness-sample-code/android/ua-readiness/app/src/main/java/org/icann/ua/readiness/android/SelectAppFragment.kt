package org.icann.ua.readiness.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.icann.ua.readiness.android.databinding.FragmentSelectAppBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SelectAppFragment : Fragment() {

    private var _binding: FragmentSelectAppBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelectAppBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAppHttp.setOnClickListener {
            findNavController().navigate(R.id.action_SelectAppFragment_to_HttpAppFragment)
        }
        binding.buttonAppSmtp.setOnClickListener {
            findNavController().navigate(R.id.action_SelectAppFragment_to_SmtpAppFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}