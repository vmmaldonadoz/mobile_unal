package com.vmmaldonadoz.triqui.controllers

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vmmaldonadoz.triqui.constants.GAMES_REFERENCE
import com.vmmaldonadoz.triqui.mappings.toRemoteGame
import com.vmmaldonadoz.triqui.model.RemoteGame
import com.vmmaldonadoz.triqui.utils.tryOrDefault
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class GameListController {

    private val query by lazy { FirebaseDatabase.getInstance().getReference(GAMES_REFERENCE) }

    private val subject: BehaviorSubject<List<RemoteGame>> = BehaviorSubject.createDefault(emptyList())

    private val valueEventListener by lazy {
        object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                subject.onNext(emptyList())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val remoteGames = mutableListOf<RemoteGame>()

                for (snapshot in dataSnapshot.children) {
                    tryOrDefault({ snapshot.toRemoteGame() }, null)?.let { remoteGames.add(it) }
                }

                subject.onNext(remoteGames)
            }

        }
    }

    fun connect() {
        query.removeEventListener(valueEventListener)
        query.addValueEventListener(valueEventListener)
    }

    fun disconnect() {
        query.removeEventListener(valueEventListener)
    }

    fun getGames(): Observable<List<RemoteGame>> {
        return subject.hide()
    }

}