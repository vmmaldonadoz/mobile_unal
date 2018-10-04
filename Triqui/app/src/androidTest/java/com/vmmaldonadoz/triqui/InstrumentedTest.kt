package com.vmmaldonadoz.triqui

import android.content.pm.ActivityInfo
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.vmmaldonadoz.triqui.activities.MainActivity
import com.vmmaldonadoz.triqui.model.TicTacToeGame
import com.vmmaldonadoz.triqui.utils.containsText
import com.vmmaldonadoz.triqui.utils.isNotEnabled
import com.vmmaldonadoz.triqui.utils.isTextBlank
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
@LargeTest
class InstrumentedTest {

    @JvmField
    @Rule
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    val boardButtons = arrayListOf(
            R.id.buttonOne, R.id.buttonTwo, R.id.buttonThree,
            R.id.buttonFour, R.id.buttonFive, R.id.buttonSix,
            R.id.buttonSeven, R.id.buttonEight, R.id.buttonNine
    )

    private val random: Random = Random()

    private fun getRandomButtonId(): Int {
        return boardButtons[random.nextInt(boardButtons.size)]
    }

    @Test
    fun givenActivityStart_whenPlayerLaunchesApp_thenGameBoardIsEmpty() {
        // Using ActivityTestRule<MainActivity>,
        // a new MainActivity is created for every annotated @Test method.

        // Validate new game
        validateEmptyGameBoard()
    }

    @Test
    fun givenANewGame_whenPlayerMakesAMove_thenMachineMakesAMove() {
        // Validate new game
        validateEmptyGameBoard()

        // Player makes a move
        onView(withId(getRandomButtonId())).apply {
            perform(click())

            check(matches(isNotEnabled()))
            check(matches(containsText("${TicTacToeGame.HUMAN_PLAYER}")))
        }

        // Validate machine movement
        onView(withText("${TicTacToeGame.COMPUTER_PLAYER}"))
                .check(matches(isNotEnabled()))
    }

    @Test
    fun givenAGameInProgress_whenResetIsClicked_thenBoardIsCleared() {
        // Start a new game.
        onView(withId(getRandomButtonId())).apply {
            perform(click())

            // Validate player movement.
            check(matches(isNotEnabled()))
            check(matches(containsText("${TicTacToeGame.HUMAN_PLAYER}")))
        }

        // Restart game.
        onView(withId(R.id.buttonRestart)).perform(click())

        // Validates that the game board is empty.
        validateEmptyGameBoard()

    }

    @Test
    fun givenPortraitScreenOrientation_whenPhoneIsRotated_thenDisplayLandscapeScreen() {

        // Request portrait screen orientation.
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Validate portrait screen orientation.
        onView(withId(R.id.textView_orientation))
                .check(matches(withText(R.string.orientation_portrait)))

        // Request landscape screen orientation.
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Validate landscape screen orientation.
        onView(withId(R.id.textView_orientation))
                .check(matches(withText(R.string.orientation_landscape)))

    }

    @Test
    fun givenGameInProgress_whenPhoneIsRotated_thenDisplaySavedGame() {

        // Request portrait screen orientation.
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Validate new game.
        validateEmptyGameBoard()

        // Save button selected by player.
        val playerMovement = getRandomButtonId()

        // Player makes a move.
        onView(withId(playerMovement))
                .perform(click())

        // Request landscape screen orientation.
        rule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Validate
        onView(withId(playerMovement))
                .apply {
                    check(matches(isNotEnabled()))
                    check(matches(containsText("${TicTacToeGame.HUMAN_PLAYER}")))
                }

    }

    /**
     * Validates that the game board is empty.
     */
    private fun validateEmptyGameBoard() {
        boardButtons.forEach { buttonId ->
            onView(withId(buttonId))
                    .check(matches(isTextBlank()))
        }
    }
}