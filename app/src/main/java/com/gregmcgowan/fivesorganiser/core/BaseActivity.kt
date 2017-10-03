package com.gregmcgowan.fivesorganiser.core

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gregmcgowan.fivesorganiser.Dependencies

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    val dependencies: Dependencies get() = getApp().dependencies

    private lateinit var lifecycleRegistry: LifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleRegistry = LifecycleRegistry(this)
    }

    override fun getLifecycle(): LifecycleRegistry {
        return lifecycleRegistry
    }
}