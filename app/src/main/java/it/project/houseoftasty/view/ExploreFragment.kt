package it.project.houseoftasty.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.search.SearchBar
import it.project.houseoftasty.R
import it.project.houseoftasty.databinding.FragmentExploreBinding

private val tabName = arrayOf("Cerca","Popolari","Nuovi")

class ExploreFragment : Fragment() {

    private lateinit var binding: FragmentExploreBinding
    private lateinit var exploreFragmentAdapter: ExploreFragmentAdapter
    private lateinit var viewPager: ViewPager

    // private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentExploreBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Modifico il titolo della Action Bar
        (activity as MainActivity).setActionBarTitle("Esplora")

        exploreFragmentAdapter = ExploreFragmentAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = exploreFragmentAdapter


    }

    class ExploreFragmentAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {

            return when (i) {
                0 -> SearchFragment()
                1 -> PopularFragment()
                2 -> RecentFragment()
                else -> HomeFragment()
            }
        }

        override fun getCount(): Int  = 3

        override fun getPageTitle(position: Int): CharSequence {
            return tabName[position]
        }
    }

    class ExploreObjectFragment(i: Int) : Fragment() {

        private var pos = i

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            arguments?.takeIf { it.containsKey(tabName[pos]) }?.apply {
                val textView: TextView = view.findViewById(android.R.id.text1)
                textView.text = getInt(tabName[pos]).toString()
            }
        }

    }
}


