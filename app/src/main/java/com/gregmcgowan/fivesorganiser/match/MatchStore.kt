package com.gregmcgowan.fivesorganiser.match

import com.gregmcgowan.fivesorganiser.core.data.match.Match
import com.gregmcgowan.fivesorganiser.core.data.match.MatchRepo
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.ZonedDateTime

class MatchStore(val matchId: String?,
                 val matchRepo: MatchRepo) {

    var match = Match("", "", ZonedDateTime.now())

    fun updateLocation(event: MatchContract.MatchUiEvent.LocationUpdatedEvent): Single<Match> {
        match = match.copy(location = event.location)
        return Single.just(match)
    }

    fun updateTime(event: MatchContract.MatchUiEvent.TimeUpdatedEvent): Single<Match> {
        match = match.copy(
                dateTime = ZonedDateTime.of(
                        match.dateTime.year,
                        match.dateTime.month.value,
                        match.dateTime.dayOfMonth,
                        event.hour,
                        event.minute,
                        match.dateTime.second,
                        match.dateTime.nano,
                        match.dateTime.zone
                ))
        return Single.just(match)
    }

    fun updateDate(event: MatchContract.MatchUiEvent.DateUpdatedEvent): Single<Match> {
        val dateTime = ZonedDateTime.of(
                event.year,
                event.month + 1,
                event.date,
                match.dateTime.hour,
                match.dateTime.minute,
                match.dateTime.second,
                match.dateTime.nano,
                match.dateTime.zone
        )
        match = match.copy(dateTime = dateTime)
        return Single.just(match)
    }

    fun getMatch(): Single<Match> {
        return Single.just(match)
    }

    fun saveMatch(): Completable {
        return Completable.fromCallable {
            if (matchId == null) {
                matchRepo.createMatch(match.dateTime, match.location)
            } else {
                matchRepo.saveMatch(match)
            }
        }
    }

    fun loadMatch(): Single<Match> {
        return if (matchId == null) {
            Single.just(match)
        } else {
            matchRepo.getMatch(matchId)
        }
    }

}