/*
 * Copyright (C) 2016-2021 Álinson Santos Xavier <git@axavier.org>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import dagger.Component
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.core.ui.widgets.WidgetBehavior
import org.isoron.uhabits.inject.HabitsApplicationComponent
import org.isoron.uhabits.intents.IntentParser.CheckmarkIntentData
import org.isoron.uhabits.preferences.SharedPreferencesStorage
import org.isoron.uhabits.widgets.activities.HabitPickerDialog

/**
 * The Android BroadcastReceiver for Loop Habit Tracker.
 *
 *
 * All broadcast messages are received and processed by this class.
 */
class WidgetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("huahua", "onReceive")
        val app = context.applicationContext as HabitsApplication
        val component = DaggerWidgetReceiver_WidgetComponent
            .builder()
            .habitsApplicationComponent(app.component)
            .build()
        val parser = app.component.intentParser
        val controller = component.widgetController
        val prefs = app.component.preferences
        val widgetUpdater = app.component.widgetUpdater
        Log.i(TAG, String.format("Received intent: %s", intent.toString()))
        lastReceivedIntent = intent
        try {
            var data: CheckmarkIntentData? = null

            if (intent.action !== ACTION_UPDATE_WIDGETS_VALUE) {
                data = parser.parseCheckmarkIntent(intent)
            }
            when (intent.action) {
                ACTION_ADD_REPETITION -> {
                    Log.d(
                        TAG,
                        String.format(
                            "onAddRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )
                    controller.onAddRepetition(
                        data.habit,
                        data.timestamp
                    )
                }

                ACTION_TOGGLE_REPETITION -> {
                    Log.d("pong", "onReceive")
                    Log.d(
                        TAG,
                        String.format(
                            "onToggleRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )

                    if(SharedPreferencesStorage(context).getInt("currentvalue", 0) == 0){
                        if (data.habit.originalEntries.get(data.timestamp).value == 0) {
                            val intent = Intent(context, HabitPickerDialog::class.java)
                            intent.putExtra("type", "special")
                            intent.putExtra("id", data.habit.id)
                            intent.putExtra("timestamp", data.timestamp.unixTime)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            ContextCompat.startActivity(context, intent, null)
                        }
                    }

                    if (SharedPreferencesStorage(context).getInt("currentvalue", 0) == 5) {
                        if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 5
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2
                        } else {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                5
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 5
                        }
                        SharedPreferencesStorage(context).putInt("currentvalue", 0)

                        controller.onToggleRepetition(
                            data.habit,
                            data.timestamp
                        )

                    } else if (SharedPreferencesStorage(context).getInt("currentvalue", 0) == 4) {
                        if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 4
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2
                        } else {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                4
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 4
                        }
                        SharedPreferencesStorage(context).putInt("currentvalue", 0)

                        controller.onToggleRepetition(
                            data.habit,
                            data.timestamp
                        )

                    } else if (SharedPreferencesStorage(context).getInt("currentvalue", 0) == 2) {
                        if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 2
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2
                        } else {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                2
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 0
                        }
                        SharedPreferencesStorage(context).putInt("currentvalue", 0)

                        controller.onToggleRepetition(
                            data.habit,
                            data.timestamp
                        )
                    } else {
                        Log.d("currentvalue", "zero")
                        if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 4
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2

                            controller.onToggleRepetition(
                                data.habit,
                                data.timestamp
                            )
                        } else if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 5
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2

                            controller.onToggleRepetition(
                                data.habit,
                                data.timestamp
                            )
                        } else if (SharedPreferencesStorage(context).getInt(
                                data.timestamp.unixTime.toString(),
                                0
                            ) == 2
                        ) {
                            SharedPreferencesStorage(context).putInt(
                                data.timestamp.unixTime.toString(),
                                0
                            )
                            data.habit.originalEntries.get(data.timestamp).value = 2

                            controller.onToggleRepetition(
                                data.habit,
                                data.timestamp
                            )
                        }else{
                            data.habit.originalEntries.get(data.timestamp).value = 2

                            controller.onToggleRepetition(
                                data.habit,
                                data.timestamp
                            )
                        }
                    }
                }

                ACTION_REMOVE_REPETITION -> {
                    Log.d(
                        TAG,
                        String.format(
                            "onRemoveRepetition habit=%d timestamp=%d",
                            data!!.habit.id,
                            data.timestamp.unixTime
                        )
                    )
                    controller.onRemoveRepetition(
                        data.habit,
                        data.timestamp
                    )
                }

                ACTION_UPDATE_WIDGETS_VALUE -> {
                    widgetUpdater.updateWidgets()
                    widgetUpdater.scheduleStartDayWidgetUpdate()
                }
            }
        } catch (e: RuntimeException) {
            Log.e("WidgetReceiver", "could not process intent", e)
        }
    }

    @ReceiverScope
    @Component(dependencies = [HabitsApplicationComponent::class])
    internal interface WidgetComponent {
        val widgetController: WidgetBehavior
    }

    companion object {
        const val ACTION_ADD_REPETITION = "org.isoron.uhabits.ACTION_ADD_REPETITION"
        const val ACTION_DISMISS_REMINDER = "org.isoron.uhabits.ACTION_DISMISS_REMINDER"
        const val ACTION_REMOVE_REPETITION = "org.isoron.uhabits.ACTION_REMOVE_REPETITION"
        const val ACTION_TOGGLE_REPETITION = "org.isoron.uhabits.ACTION_TOGGLE_REPETITION"
        const val ACTION_UPDATE_WIDGETS_VALUE = "org.isoron.uhabits.ACTION_UPDATE_WIDGETS_VALUE"
        private const val TAG = "WidgetReceiver"
        var lastReceivedIntent: Intent? = null
            private set

        fun clearLastReceivedIntent() {
            lastReceivedIntent = null
        }
    }
}
