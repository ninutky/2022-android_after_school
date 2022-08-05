package com.example.bookreview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    lateinit var drawerToggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)

        var sData = resources.getStringArray(R.array.books)
        var adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sData)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame, BookWriteFragment())
            .commit()


        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.write -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, BookWriteFragment())
                        .commit()
                }
//                R.id.retouch -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.frame, BookRetouch())
//                        .commit()
//                }
//                R.id.delete -> {
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.frame, BookDelete())
//                        .commit()
//                }
            }

            drawerLayout.closeDrawers()

            true
        }

        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ) {}

        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        drawerToggle.syncState()
    }

}