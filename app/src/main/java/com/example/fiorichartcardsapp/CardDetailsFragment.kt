package com.example.fiorichartcardsapp

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import com.example.fiorichartcardsapp.databinding.FragmentCardDetailsBinding
import com.example.fiorichartcardsapp.domain.DetailChartData
import com.example.fiorichartcardsapp.viewmodels.FioriChartViewViewModel
import com.github.mikephil.charting.formatter.FioriLegendValueFormatter
import com.sap.cloud.mobile.fiori.chart.BarChartView
import com.sap.cloud.mobile.fiori.chart.ColumnChartView
import com.sap.cloud.mobile.fiori.chart.LineChartView
import com.sap.cloud.mobile.fiori.chartcard.ChartCardDataModel
import java.security.InvalidParameterException
import java.text.DecimalFormat

class CardDetailsFragment : Fragment() {
    private lateinit var binding: FragmentCardDetailsBinding

    private val safeArgs: CardDetailsFragmentArgs by navArgs()

    private lateinit var viewModel: FioriChartViewViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_card_details, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        val viewModelFactory = FioriChartViewViewModel.Factory(activity.application, safeArgs.chartViewData)
        viewModel = viewModelFactory.create(FioriChartViewViewModel::class.java)

        viewModel.selectChartCard.observe(viewLifecycleOwner,
            Observer<DetailChartData> {chartViewData ->
                if (chartViewData != null) {
                    when (chartViewData.plotType) {
                        ChartCardDataModel.LINE_CHART -> {
                            configureLineChartView(chartViewData)
                        }
                        ChartCardDataModel.COLUMN_CHART -> {
                            configureColumnChartView(chartViewData)
                        }
                        ChartCardDataModel.HORIZONTAL_BAR_CHART -> {
                            configureBarChartView(chartViewData)
                        }
                        else -> {
                            configureColumnChartView(chartViewData)
                        }
                    }
                } else {
                    setChartViewVisibility(0)
                }
            }
        )

        binding.toolbar.setNavigationOnClickListener { view ->
            view.findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            item.onNavDestinationSelected(findNavController())
        }

        return binding.root
    }


    private fun configureLineChartView(chartData: DetailChartData) {

        setChartViewVisibility(0)
        val chartView = binding.root.findViewById(R.id.line_chart) as LineChartView
        chartView.setLegendValueFormatter(
            object : FioriLegendValueFormatter {
                override fun formatXValue(value: Float): String {
                    val labelSize: Int = chartData.xLabels.size
                    return chartData.xLabels[(((value - chartView.xChartMin) * (labelSize - 1) / chartView.xRange).toInt())]
                }

                override fun formatYValue(v: Float): String {
                    if (java.lang.Float.isNaN(v)) {
                        return "No Data"
                    }
                    val format = DecimalFormat("#,##0,000")
                    return format.format(v.toDouble())
                }

                override fun formatRangeValue(v: Float): String {
                    val format = DecimalFormat("+#,##0,000; -#,##0,000")
                    return format.format(v.toDouble())
                }
            })
        chartView.setChartTitle(chartData.title)
        chartView.setStatusString(chartData.timestamp)
        chartView.setXLabels(chartData.xLabels)
        chartView.setOnlyLimitLabels(true, true)
        chartView.zoom(4F,1f,0f, 0f)
        for ((key, value) in chartData.plotDataSet!!) {
            chartView.addDataSet(value, key)
        }
        chartView.updateGraph()
    }

    private fun configureColumnChartView(chartData: DetailChartData) {

        setChartViewVisibility(1)
        val chartView = binding.root.findViewById(R.id.column_chart) as ColumnChartView
        chartView.setChartTitle(chartData.title)
        chartView.setStatusString(chartData.timestamp)
        chartView.setXLabels(chartData.xLabels)
        chartView.zoom(2F,1f,0f, 0f)
        chartView.setLegendValueFormatter(
            object : FioriLegendValueFormatter {
                override fun formatXValue(value: Float): String {
                    return chartData.xLabels[value.toInt()]
                }

                override fun formatYValue(v: Float): String {
                    if (java.lang.Float.isNaN(v)) {
                        return "No Data"
                    }
                    val format = DecimalFormat("#,##0,000")
                    return format.format(v.toDouble())
                }

                override fun formatRangeValue(v: Float): String {
                    val format = DecimalFormat("+#,##0,000; -#,##0,000")
                    return format.format(v.toDouble())
                }
            })
        for ((key, value) in chartData.plotDataSet!!) {
            chartView.addDataSet(value, key)
        }
        chartView.updateGraph()
    }

    private fun configureBarChartView(chartData: DetailChartData) {

        setChartViewVisibility(2)
        val chartView = binding.root.findViewById(R.id.bar_chart) as BarChartView
        chartView.setChartTitle(chartData.title)
        chartView.setStatusString(chartData.timestamp)
        chartView.setXLabels(chartData.xLabels)
        chartView.setLegendValueFormatter(
            object : FioriLegendValueFormatter {
                override fun formatXValue(value: Float): String {
                    return chartData.xLabels[value.toInt()]
                }

                override fun formatYValue(v: Float): String {
                    if (java.lang.Float.isNaN(v)) {
                        return "No Data"
                    }
                    val format = DecimalFormat("#,##0,000")
                    return format.format(v.toDouble())
                }

                override fun formatRangeValue(v: Float): String {
                    val format = DecimalFormat("+#,##0,000; -#,##0,000")
                    return format.format(v.toDouble())
                }
            })
        for ((key, value) in chartData.plotDataSet!!) {
            chartView.addDataSet(value, key)
        }
        chartView.updateGraph()
    }

    private fun setChartViewVisibility(plotType: Int) {
        when (plotType) {
            ChartCardDataModel.LINE_CHART -> {
                binding.lineChartView.visibility = View.VISIBLE
                binding.columnChartView.visibility = View.GONE
                binding.barChartView.visibility = View.GONE
            }
            ChartCardDataModel.COLUMN_CHART -> {
                binding.lineChartView.visibility = View.GONE
                binding.columnChartView.visibility = View.VISIBLE
                binding.barChartView.visibility = View.GONE
            }
            ChartCardDataModel.HORIZONTAL_BAR_CHART -> {
                binding.lineChartView.visibility = View.GONE
                binding.columnChartView.visibility = View.GONE
                binding.barChartView.visibility = View.VISIBLE
            }
            else -> {
                throw InvalidParameterException()
            }
        }
    }

}