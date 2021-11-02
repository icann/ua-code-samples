package org.icann.ua.readiness.android

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.icann.ua.readiness.android.databinding.FragmentSmtpAppBinding
import org.icann.ua.readiness.android.services.smtp.EmailIntentRequestSample
import org.icann.ua.readiness.android.services.smtp.JakartaMailSmtpRequestSample
import org.icann.ua.readiness.android.services.smtp.SmtpRequestSample


/**
 * Fragment with form to send EAI compliant emails.
 */
class SmtpAppFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentSmtpAppBinding? = null
    private lateinit var smtpLibraries: Map<String, Any>

    private val binding get() = _binding!!

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smtpLibraries = listOf(
            JakartaMailSmtpRequestSample(requireContext()),
            EmailIntentRequestSample(requireContext()),
        ).associateBy { it.toString() }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSmtpAppBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.smtpProgressBar.visibility = View.GONE
        val spinner: Spinner = view.findViewById(R.id.smtpLibSpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                smtpLibraries.values.toTypedArray()
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }
        binding.smtpRequestButton.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        val imm: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        binding.smtpTextViewError.text = ""
        binding.smtpRequestButton.isEnabled = false
        binding.smtpProgressBar.visibility = View.VISIBLE
        val emailAddress: String = binding.smtpEditTextEmail.text.toString()
        val emailSubject: String = binding.smtpEditTextSubject.text.toString()
        val emailBody: String = binding.smtpEditTextBody.text.toString()
        val smtpLib: SmtpRequestSample = binding.smtpLibSpinner.selectedItem as SmtpRequestSample
        Toast.makeText(context, "Send email with $smtpLib to $emailAddress", Toast.LENGTH_SHORT)
            .show()
        scope.launch {
            withContext(Dispatchers.IO) {
                val result = smtpLib.sendEmail(emailAddress, emailSubject, emailBody)
                binding.smtpTextViewError.text = result
            }
            binding.smtpProgressBar.visibility = View.GONE
            binding.smtpRequestButton.isEnabled = true
        }
    }
}