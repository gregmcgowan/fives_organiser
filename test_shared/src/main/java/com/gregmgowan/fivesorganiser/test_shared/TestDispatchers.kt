import com.gregmcgowan.fivesorganiser.core.CoroutineDisptachersAndContext
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineContext

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
val TEST_COROUTINE_DISPTACHERS_AND_CONTEXT: CoroutineDisptachersAndContext =
        CoroutineDisptachersAndContext(Unconfined, Unconfined, TestCoroutineContext())

fun CoroutineDisptachersAndContext.getTestCoroutineContext(): TestCoroutineContext {
    return this.context as TestCoroutineContext
}

