package com.gregmcgowan.fivesorganiser.core.di

import javax.inject.Scope


@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class MainScope


@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope


@Scope
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentScope