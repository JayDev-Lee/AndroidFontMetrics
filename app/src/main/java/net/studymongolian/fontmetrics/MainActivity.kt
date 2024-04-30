package net.studymongolian.fontmetrics

import android.content.Context
import android.graphics.Typeface
import android.graphics.fonts.SystemFonts
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import net.studymongolian.fontmetrics.databinding.ActivityMainBinding
import java.io.File

class MainActivity : ComponentActivity(), View.OnClickListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val systemFontPaths by lazy {
        sequenceOf(
            File("/system/fonts"),
            File("/product/fonts")
        ).filter { it.exists() && it.canRead() }.toList()
    }

    private var systemFontFilesList: List<File> = emptyList()

    private val FILE_NAME_FILTERS = arrayOf(
        "bold",
        "clock",
        "emoji",
        "extra",
        "mono",
        "script",
        "symbol",
        "json", // there may be a config.json in /product/fonts
    )

    private val FILTER_FILE_SIZE = 50 * 1024

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        getSystemFontFiles()
        binding.spinnerSystemFont.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, systemFontFilesList)
        binding.spinnerSystemFont.onItemSelectedListener = object: OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.viewWindow.setTypeface(Typeface.createFromFile(systemFontFilesList[position]))
                binding.etTextString.setTypeface(Typeface.createFromFile(systemFontFilesList[position]))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        binding.etTextString.setText("My text line")
        binding.etFontSize.setText(FontMetricsView.DEFAULT_FONT_SIZE_PX.toString())

        binding.updateButton.setOnClickListener(this)

        binding.cbTop.setOnClickListener(this)
        binding.cbAscent.setOnClickListener(this)
        binding.cbBaseline.setOnClickListener(this)
        binding.cbDescent.setOnClickListener(this)
        binding.cbBottom.setOnClickListener(this)
        binding.cbTextBounds.setOnClickListener(this)
        binding.cbWidth.setOnClickListener(this)

        updateTextViews()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.updateButton -> {
                binding.viewWindow.setText(binding.etTextString.getText().toString())
                val fontSize = try {
                    binding.etFontSize.getText().toString().toInt()
                } catch (e: NumberFormatException) {
                    FontMetricsView.DEFAULT_FONT_SIZE_PX
                }
                binding.viewWindow.setTextSizeInPixels(fontSize)
                updateTextViews()
                hideKeyboard(currentFocus)
            }

            R.id.cbTop -> binding.viewWindow.setTopVisible(binding.cbTop.isChecked)
            R.id.cbAscent -> binding.viewWindow.setAscentVisible(binding.cbAscent.isChecked)
            R.id.cbBaseline -> binding.viewWindow.setBaselineVisible(binding.cbBaseline.isChecked)
            R.id.cbDescent -> binding.viewWindow.setDescentVisible(binding.cbDescent.isChecked)
            R.id.cbBottom -> binding.viewWindow.setBottomVisible(binding.cbBottom.isChecked)
            R.id.cbTextBounds -> binding.viewWindow.setBoundsVisible(binding.cbTextBounds.isChecked)
            R.id.cbWidth -> binding.viewWindow.setWidthVisible(binding.cbWidth.isChecked)
        }
    }

    fun updateTextViews() {
        binding.tvTop.text = binding.viewWindow.fontMetrics.top.toString()
        binding.tvAscent.text = binding.viewWindow.fontMetrics.ascent.toString()
        binding.tvBaseline.text = 0f.toString()
        binding.tvDescent.text = binding.viewWindow.fontMetrics.descent.toString()
        binding.tvBottom.text = binding.viewWindow.fontMetrics.bottom.toString()
        binding.tvTextBounds.text = "w = " + (binding.viewWindow.textBounds?.width().toString() +
                "     h = " + binding.viewWindow.textBounds?.height().toString()).toString()
        binding.tvWidth.text = binding.viewWindow.measuredTextWidth.toString()
        binding.tvLeadingValue.text = binding.viewWindow.fontMetrics.leading.toString()
    }

    private fun hideKeyboard(view: View?) {
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getSystemFontFiles(): List<File> = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            systemFontPaths.map { folder ->
                folder.walk()
                    .maxDepth(1)
                    .filter { file ->
                        FILE_NAME_FILTERS.none { file.name.contains(it, true) }
                    }
                    .filter {
                        it.length() > FILTER_FILE_SIZE
                    }
                    .sortedBy { it.name }
                    .toList()
            }.flatten()
        } else {
            getSystemFonts()
        }.also {
            systemFontFilesList = it
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getSystemFonts() = SystemFonts.getAvailableFonts()
        .asSequence()
        .filter { font ->
            // set to italic if file or name is null, it will be filted here
            FILE_NAME_FILTERS.none { filter ->
                (font.file?.name ?: "italic").contains(filter, true)
            }
        }
        .filter {
            (it.file?.length() ?: 0) > FILTER_FILE_SIZE
        }
        .filterNot {
            it.localeList.toLanguageTags().contains("und-")
        }
        // file won't be null here
        .groupBy { it.file!!.path }
        .map {
            it.value.first().file!!
        }
        .sortedBy { it.name }
        .toList()
}
