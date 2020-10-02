package com.example.fiorichartcardsapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.sap.cloud.mobile.fiori.chartcard.ChartCardDataModel
import com.sap.cloud.mobile.fiori.chartcard.ChartCardView
import com.sap.cloud.mobile.fiori.chartcard.ChartCardViewAdapter
import com.sap.cloud.mobile.fiori.common.FioriItemClickListener

import com.example.fiorichartcardsapp.databinding.FragmentBaseCardsBinding
import com.example.fiorichartcardsapp.domain.*
import com.example.fiorichartcardsapp.viewmodels.FioriChartCardsViewModel

class BaseCardsFragment : Fragment() {

    private lateinit var binding: FragmentBaseCardsBinding

    private val viewModel: FioriChartCardsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, FioriChartCardsViewModel.Factory(
                (requireContext().applicationContext as FioriChartCardsApplication).covidDataRepository,
                activity.application))
            .get(FioriChartCardsViewModel::class.java)
    }

    private var origXLabels: MutableList<String> = mutableListOf()
    private var mChartCardView: ChartCardView? = null

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        binding = FragmentBaseCardsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.chartCardsViewModel = viewModel
        mChartCardView = binding.root.findViewById(R.id.chartCardView)

        viewModel.covidUsChartCards.observe(viewLifecycleOwner,
            Observer<MutableList<ChartCardDataModel>> {
                if (it.isNotEmpty())
                    setChartCardView(it)
            })

        viewModel.xLabels4UsData.observe(viewLifecycleOwner,
            Observer { if (it.isNotEmpty()) origXLabels = it})

        // Observer for the network error.
        viewModel.eventNetworkError.observe(viewLifecycleOwner, Observer<Boolean> { isNetworkError ->
            if (isNetworkError) onNetworkError()
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.chart_cards_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.onRefresh()
                true
            }
            else -> false
        }

    private fun setChartCardView(usChartCards: MutableList<ChartCardDataModel>) {
        val layoutType = binding.chartCardView.layoutType
        (binding.chartCardView).setViewLayoutManager()
        val chartCardViewAdapter = this.activity?.let {
            ChartCardViewAdapter(it, usChartCards, layoutType)
        }
        (binding.chartCardView).setViewAdapter(chartCardViewAdapter)

        mChartCardView?.setItemClickListener(object : FioriItemClickListener {
            override fun onClick(view: View, position: Int) {
                val card = usChartCards[position]
                val chartData = DetailChartData(
                    card.plotType, card.chartCardTitle!!, card.chartCardTimestamp!!,
                    origXLabels, card.plotDataSet!!)
                val action = HomeViewPagerFragmentDirections.actionViewPagerFragmentToCardDetailFragment(chartData)
                findNavController().navigate(action)
            }

            override fun onLongClick(view: View, position: Int) {
                Toast.makeText(activity?.applicationContext, "You long clicked on: $position", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Method for displaying a Toast error message for network errors.
     */
    private fun onNetworkError() {
        if(!viewModel.isNetworkErrorShown.value!!) {
            Toast.makeText(activity, "Network Error", Toast.LENGTH_LONG).show()
            viewModel.onNetworkErrorShown()
        }
    }
}

