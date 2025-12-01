package com.comixa.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.comixa.app.databinding.ActivityMainBinding
import com.comixa.app.model.DrawerGroup
import com.comixa.app.model.DrawerSection
import com.comixa.app.model.DrawerSubItem
import com.comixa.app.ui.drawer.DrawerAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        drawerLayout = binding.drawerLayout
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        forceHamburger()
        navController.addOnDestinationChangedListener { _, _, _ -> forceHamburger() }

        val sections = mutableListOf(
            DrawerSection(
                title = getString(R.string.menu_home),
                iconRes = android.R.drawable.ic_menu_today,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_home_section1), R.id.nav_home_section1),
                    DrawerSubItem(getString(R.string.menu_home_section2), R.id.nav_home_section2)
                )
            ),
            DrawerSection(
                title = getString(R.string.menu_library),
                iconRes = android.R.drawable.ic_menu_sort_by_size,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_library_section1), R.id.nav_library_section1),
                    DrawerSubItem(getString(R.string.menu_library_section2), R.id.nav_library_section2)
                )
            ),
            DrawerSection(
                title = getString(R.string.menu_discover),
                iconRes = android.R.drawable.ic_menu_search,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_discover_section1), R.id.nav_discover_section1),
                    DrawerSubItem(getString(R.string.menu_discover_section2), R.id.nav_discover_section2)
                )
            ),
            DrawerSection(
                title = getString(R.string.menu_community),
                iconRes = android.R.drawable.ic_menu_share,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_community_section1), R.id.nav_community_section1),
                    DrawerSubItem(getString(R.string.menu_community_section2), R.id.nav_community_section2)
                )
            ),
            DrawerSection(
                title = getString(R.string.menu_profile),
                iconRes = android.R.drawable.ic_menu_myplaces,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_profile_section1), R.id.nav_profile_section1),
                    DrawerSubItem(getString(R.string.menu_profile_section2), R.id.nav_profile_section2)
                )
            ),
            DrawerSection(
                title = getString(R.string.menu_settings),
                iconRes = android.R.drawable.ic_menu_preferences,
                subItems = listOf(
                    DrawerSubItem(getString(R.string.menu_settings_section1), R.id.nav_settings_section1),
                    DrawerSubItem(getString(R.string.menu_settings_section2), R.id.nav_settings_section2)
                )
            ),
            DrawerSection(
                title = "Labs",
                iconRes = android.R.drawable.ic_menu_agenda,
                groups = listOf(
                    DrawerGroup(
                        title = "Lab1",
                        subItems = listOf(
                            DrawerSubItem("Panel", R.id.nav_lab1_panel)
                        )
                    ),
                    DrawerGroup(
                        "Lab2 (Organizer)",
                        subItems = listOf(
                            DrawerSubItem("Calendar", R.id.nav_lab2)
                        )
                    ),
                    DrawerGroup(
                        "Lab3 (RSS)",
                        subItems = listOf(
                            DrawerSubItem("Sources", R.id.nav_lab3_sources)
                        )
                    ),
                    DrawerGroup(
                        "Lab4 (Loader)",
                        subItems = listOf(
                            DrawerSubItem("Demo", R.id.nav_lab4)
                        )
                    ),
                    DrawerGroup(
                        "Lab5 (Telemedicine)",
                        subItems = listOf(
                            DrawerSubItem("Telemedicine UI", R.id.nav_lab5_telemedicine)
                        )
                    )
                )
            )
        )

        binding.drawerRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DrawerAdapter(navController, drawerLayout, sections)
            setHasFixedSize(true)
        }
    }

    private fun forceHamburger() {
        binding.toolbar.navigationIcon = DrawerArrowDrawable(this).apply {
            progress = 0f
        } // 0 = hamburger
        binding.toolbar.setNavigationOnClickListener {
            drawerLayout.open()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.open()
        return true
    }
}
