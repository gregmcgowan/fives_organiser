package com.gregmcgowan.fivesorganiser.test_shared

import com.flextrade.jfixture.JFixture

/**
 * From https://www.twisterrob.net/blog/2019/03/practical-jfixture.htm
 */

inline fun <reified T> JFixture.build(): T = create(T::class.java)

inline operator fun <reified T> JFixture.invoke(): T = create(T::class.java)

@Suppress("UNCHECKED_CAST") // can't have List<T>::class literal, so need to cast
inline fun <reified T : Any> JFixture.createList(size: Int = 3): List<T> =
    this.collections().createCollection(List::class.java as Class<List<T>>, T::class.java, size)
