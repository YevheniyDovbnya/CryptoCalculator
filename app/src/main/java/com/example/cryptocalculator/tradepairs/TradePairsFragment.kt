package com.example.cryptocalculator.tradepairs

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.cryptocalculator.R
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.fragment_trade_pairs.*

class TradePairsFragment : Fragment() {

    private lateinit var viewModel: TradePairsViewModel

    private lateinit var tradePairsAdapter: TradePairsAdapter

    private val repeatingTasksObserver = Observer<Boolean> {
        viewModel.getTradePairs(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tradePairsAdapter = TradePairsAdapter(mutableListOf(),
            {
            findNavController().navigate(R.id.action_tradePairsfragment_to_currencyCalculatorFragment, Bundle().apply {
                putParcelable("tradePair", it)
            })
            },
            {
                viewModel.repeatingTasks.removeObservers(this)
                tradePairsAdapter.clearItems()
                viewModel.saveCurrency(it)
                viewModel.getTradePairs(true)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trade_pairs, container, false)
    }

    override fun onStart() {
        super.onStart()
        viewModel.getTradePairs(true)
        viewModel.getCurrency()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spacing = resources.getDimensionPixelSize(R.dimen.recycler_spacing)
        list.apply {
            layoutManager = GridLayoutManager(context, SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) {
                            spanCount
                        } else {
                            1
                        }
                    }
                }
            }
            setPadding(spacing, spacing, spacing, spacing)
            clipToPadding = false
            clipChildren = false
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.set(spacing, spacing, spacing, spacing)
                }
            })
            adapter
        }
        list.adapter = tradePairsAdapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(requireActivity().application)).get(TradePairsViewModel::class.java)
        viewModel.tradingPairs.observe(this, Observer {
            if (tradePairsAdapter.itemCount == 1) {
                tradePairsAdapter.addItems(it)
                viewModel.repeatingTasks.observe(this, repeatingTasksObserver)
            } else {
                tradePairsAdapter.updateItems(it)
            }

        })
        viewModel.isLoading.observe(this, Observer {
            progressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
        viewModel.error.observe(this, Observer {
            view?.let { view ->
                Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
            }
        })
        viewModel.currencyId.observe(this, Observer {
            list.layoutManager?.findViewByPosition(0)?.findViewById<Spinner>(R.id.currencySpinnerView)?.setSelection(it)
        })
    }

    companion object {

        private const val SPAN_COUNT = 2

        @JvmStatic
        fun newInstance() = TradePairsFragment()
    }
}

