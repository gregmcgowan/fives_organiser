package com.gregmcgowan.fivesorganiser.data.player

import android.R.attr.phoneNumber
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.flextrade.jfixture.FixtureAnnotations
import com.flextrade.jfixture.JFixture
import com.gregmcgowan.fivesorganiser.data.FivesOrganiserDatabase
import com.gregmcgowan.fivesorganiser.test_shared.CoroutinesTestRule
import com.gregmcgowan.fivesorganiser.test_shared.build
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayersSqliteRepoTest {
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    private lateinit var fixture: JFixture
    private lateinit var driver: SqlDriver
    private lateinit var sut: PlayersSqliteRepo

    @Before
    fun before() {
        fixture = JFixture()
        FixtureAnnotations.initFixtures(this, fixture)
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        FivesOrganiserDatabase.Schema.create(driver)
        sut = PlayersSqliteRepo(database = FivesOrganiserDatabase(driver = driver))
    }

    @After
    fun after() {
        driver.close()
    }

    @Test
    fun `add and get player`() =
        runTest {
            val name: String = fixture.build()
            val email: String = fixture.build()
            val phoneNumber: String = fixture.build()
            val contactId: Long = fixture.build()

            sut.addPlayer(
                name = name,
                email = email,
                phoneNumber = phoneNumber,
                contactId = contactId,
            )

            val players = sut.getPlayers()

            assertThat(players, hasSize(1))
            assertThat(players[0].name, equalTo(name))
            assertThat(players[0].email, equalTo(email))
            assertThat(players[0].phoneNumber, equalTo(phoneNumber))
            assertThat(players[0].contactId, equalTo(contactId))
        }

    @Test
    fun `add and get player with null values`() =
        runTest {
            val name: String = fixture.build()

            sut.addPlayer(
                name = name,
                email = null,
                phoneNumber = null,
                contactId = null,
            )

            val players = sut.getPlayers()

            assertThat(players, hasSize(1))
            assertThat(players[0].name, equalTo(name))
            assertThat(players[0].email, nullValue())
            assertThat(players[0].phoneNumber, nullValue())
            assertThat(players[0].contactId, nullValue())
        }

    @Test
    fun `get player when there are no players`() =
        runTest {
            val players = sut.getPlayers()

            assertThat(players, hasSize(0))
        }
}
