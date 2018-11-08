package com.vmmaldonadoz.triqui.controllers

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vmmaldonadoz.triqui.constants.GAMES_REFERENCE
import com.vmmaldonadoz.triqui.mappings.toMap
import com.vmmaldonadoz.triqui.mappings.toRemoteGame
import com.vmmaldonadoz.triqui.model.RemoteGame
import com.vmmaldonadoz.triqui.utils.className
import com.vmmaldonadoz.triqui.utils.tryOrPrintException
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class GameController {

    private var query = getReference("")

    private fun getReference(gameId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("$GAMES_REFERENCE/$gameId")
    }

    private val subject: BehaviorSubject<RemoteGame> = BehaviorSubject.create()

    private val valueEventListener by lazy {
        object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(this.className(), databaseError.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                tryOrPrintException {
                    val remoteGame = dataSnapshot.toRemoteGame()
                    subject.onNext(remoteGame)
                }
            }
        }
    }

    fun connect(gameId: String) {
        query = getReference(gameId)
        query.removeEventListener(valueEventListener)
        query.addValueEventListener(valueEventListener)
    }

    fun disconnect() {
        query.removeEventListener(valueEventListener)
    }

    fun getGame(): Observable<RemoteGame> {
        return subject.hide()
    }

    fun saveRemoteGame(remoteGame: RemoteGame) {
        query.setValue(remoteGame.toMap())
    }

}