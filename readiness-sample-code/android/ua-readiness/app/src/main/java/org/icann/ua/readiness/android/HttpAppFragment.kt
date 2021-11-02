package org.icann.ua.readiness.android

import android.content.Context
import android.os.Bundle
import android.util.Base64
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
import org.icann.ua.readiness.android.databinding.FragmentHttpAppBinding
import org.icann.ua.readiness.android.services.http.*


/**
 * Fragment with form to make IDNA 2008 compliant HTTP requests.
 */
class HttpAppFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentHttpAppBinding? = null
    private var httpLibraries = listOf(
        ApacheHttpRequestSample(),
        FuelHttpRequestSample(),
        HttpUrlConnectionRequestSample(),
        OkHttpRequestSample(),
        RetrofitHttpRequestSample(),
        VolleyHttpRequestSample(),
    ).associateBy { it.toString() }

    private val binding get() = _binding!!

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHttpAppBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.httpProgressBar.visibility = View.GONE
        val spinner: Spinner = view.findViewById(R.id.httpLibSpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        context?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                httpLibraries.values.toTypedArray()
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = adapter
            }
        }
        binding.httpWebView.settings.defaultTextEncodingName = "utf-8"
        binding.httpRequestButton.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        val imm: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        binding.httpRequestButton.isEnabled = false
        binding.httpWebView.loadUrl("about:blank")
        binding.httpProgressBar.visibility = View.VISIBLE
        val url: String = binding.httpEditTextUrl.text.toString()
        val httpLib: HttpRequestSample = binding.httpLibSpinner.selectedItem as HttpRequestSample
        Toast.makeText(context, "Make request with $httpLib to $url", Toast.LENGTH_SHORT).show()
        scope.launch {
            val data = withContext(Dispatchers.IO) {
                context?.let { httpLib.makeRequest(url, it) }
            }
            val base64 = Base64.encodeToString(
                data?.toByteArray(charset("UTF-8")) ?: ByteArray(0),
                Base64.DEFAULT
            )
            binding.httpWebView.loadData(base64, "text/html; charset=utf-8", "base64")
            /* Another working solution without base64 encoding
            binding.webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null)
            */
            binding.httpProgressBar.visibility = View.GONE
            binding.httpRequestButton.isEnabled = true
        }
    }
}
