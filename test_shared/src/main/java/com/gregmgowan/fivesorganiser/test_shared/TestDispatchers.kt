import com.gregmcgowan.fivesorganiser.core.CoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
val TEST_COROUTINE_DISPATCHERS: CoroutineDispatchers =
        CoroutineDispatchers(TestCoroutineDispatcher(), TestCoroutineDispatcher())

