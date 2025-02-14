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

package org.isoron.uhabits.widgets.activities

import android.app.Activity
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_MULTIPLE
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import org.isoron.uhabits.HabitsApplication
import org.isoron.uhabits.R
import org.isoron.uhabits.activities.AndroidThemeSwitcher
import org.isoron.uhabits.core.commands.CreateHabitCommand
import org.isoron.uhabits.core.commands.EditHabitCommand
import org.isoron.uhabits.core.models.Frequency
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.HabitType
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.models.Reminder
import org.isoron.uhabits.core.models.Timestamp
import org.isoron.uhabits.core.models.WeekdayList
import org.isoron.uhabits.core.preferences.WidgetPreferences
import org.isoron.uhabits.preferences.SharedPreferencesStorage
import org.isoron.uhabits.receivers.WidgetReceiver
import org.isoron.uhabits.widgets.WidgetUpdater
import java.util.ArrayList

class BooleanHabitPickerDialog : HabitPickerDialog() {
    override fun shouldHideNumerical() = true
    override fun getEmptyMessage() = R.string.no_boolean_habits
}

class NumericalHabitPickerDialog : HabitPickerDialog() {
    override fun shouldHideBoolean() = true
    override fun getEmptyMessage() = R.string.no_numerical_habits
}

open class HabitPickerDialog : Activity() {

    private var widgetId = 0
    private lateinit var widgetPreferences: WidgetPreferences
    private lateinit var widgetUpdater: WidgetUpdater

    protected open fun shouldHideNumerical() = false
    protected open fun shouldHideBoolean() = false
    protected open fun getEmptyMessage() = R.string.no_habits

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component = (applicationContext as HabitsApplication).component
        AndroidThemeSwitcher(this, component.preferences).apply()
        val habitList = component.habitList
        widgetPreferences = component.widgetPreferences
        widgetUpdater = component.widgetUpdater
        widgetId = intent.extras?.getInt(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID) ?: 0

        if(intent.getStringExtra("type") == "special"){
            setContentView(R.layout.activity_choose_color)

            val id = intent.getLongExtra("id", -1)
            val timestamp = intent.getLongExtra("timestamp", -1)

            Log.d("HabitPickerDialog", "id: $id")

            val habit = component.habitList.getById(id)!!
            val habitType = habit.type
            val color = habit.color
            val freqNum = habit.frequency.numerator
            val freqDen = habit.frequency.denominator
            val targetType = habit.targetType
            var reminderHour = -1
            var reminderMin = -1
            var reminderDays: WeekdayList = WeekdayList.EVERY_DAY

            habit.reminder?.let {
                reminderHour = it.hour
                reminderMin = it.minute
                reminderDays = it.days
            }
            val name = (habit.name)
            val question = (habit.question)
            val desc = (habit.description)
            val unit = (habit.unit)
            val targetValue = (habit.targetValue.toString())

            val red = findViewById<View>(R.id.red_color_option)
            val green = findViewById<View>(R.id.green_color_option)
            val blue = findViewById<View>(R.id.blue_color_option)

            red.setOnClickListener {
                val component = (application as HabitsApplication).component
                val habit = component.modelFactory.buildHabit()

                var original: Habit? = null
                if (id >= 0) {
                    original = component.habitList.getById(id)!!
                    habit.copyFrom(original)
                }

                habit.id = id
                habit.name = name
                habit.question = question
                habit.description = desc
                habit.color = PaletteColor(0)
                if (reminderHour >= 0) {
                    habit.reminder = Reminder(reminderHour, reminderMin, reminderDays)
                } else {
                    habit.reminder = null
                }

                habit.frequency = Frequency(freqNum, freqDen)
                if (habitType == HabitType.NUMERICAL) {
                    habit.targetValue = targetValue.toDouble()
                    habit.targetType = targetType
                    habit.unit = unit
                }
                habit.type = habitType

                SharedPreferencesStorage(this).putInt("currentvalue", 2)

                val intent = Intent(this, WidgetReceiver::class.java).apply {
                    data = Uri.parse(habit.uriString)
                    action = WidgetReceiver.ACTION_TOGGLE_REPETITION
                    if (timestamp != null) putExtra("timestamp", timestamp)
                }

                this.sendBroadcast(intent)


//                val command = if (id >= 0) {
//                    EditHabitCommand(
//                        component.habitList,
//                        id,
//                        habit
//                    )
//                } else {
//                    CreateHabitCommand(
//                        component.modelFactory,
//                        component.habitList,
//                        habit
//                    )
//                }
//                component.commandRunner.run(command)

                finish()
            }

            blue.setOnClickListener {
                val component = (application as HabitsApplication).component
                val habit = component.modelFactory.buildHabit()

                var original: Habit? = null
                if (id >= 0) {
                    original = component.habitList.getById(id)!!
                    habit.copyFrom(original!!)
                }

                habit.id = id
                habit.name = name
                habit.question = question
                habit.description = desc
                habit.color = PaletteColor(0)
                if (reminderHour >= 0) {
                    habit.reminder = Reminder(reminderHour, reminderMin, reminderDays)
                } else {
                    habit.reminder = null
                }

                habit.frequency = Frequency(freqNum, freqDen)
                if (habitType == HabitType.NUMERICAL) {
                    habit.targetValue = targetValue.toDouble()
                    habit.targetType = targetType
                    habit.unit = unit
                }
                habit.type = habitType

                SharedPreferencesStorage(this).putInt("currentvalue", 5)

                val intent = Intent(this, WidgetReceiver::class.java).apply {
                    data = Uri.parse(habit.uriString)
                    action = WidgetReceiver.ACTION_TOGGLE_REPETITION
                    if (timestamp != null) putExtra("timestamp", timestamp)
                }

                this.sendBroadcast(intent)

//                val command = if (id >= 0) {
//                    EditHabitCommand(
//                        component.habitList,
//                        id,
//                        habit
//                    )
//                } else {
//                    CreateHabitCommand(
//                        component.modelFactory,
//                        component.habitList,
//                        habit
//                    )
//                }
//                component.commandRunner.run(command)

                finish()
            }

            green.setOnClickListener {
                val component = (application as HabitsApplication).component
                val habit = component.modelFactory.buildHabit()

                var original: Habit? = null
                if (id >= 0) {
                    original = component.habitList.getById(id)!!
                    habit.copyFrom(original!!)
                }

                habit.id = id
                habit.name = name
                habit.question = question
                habit.description = desc
                habit.color = PaletteColor(0)
                if (reminderHour >= 0) {
                    habit.reminder = Reminder(reminderHour, reminderMin, reminderDays)
                } else {
                    habit.reminder = null
                }

                habit.frequency = Frequency(freqNum, freqDen)
                if (habitType == HabitType.NUMERICAL) {
                    habit.targetValue = targetValue.toDouble()
                    habit.targetType = targetType
                    habit.unit = unit
                }
                habit.type = habitType

                SharedPreferencesStorage(this).putInt("currentvalue", 4)

                val intent = Intent(this, WidgetReceiver::class.java).apply {
                    data = Uri.parse(habit.uriString)
                    action = WidgetReceiver.ACTION_TOGGLE_REPETITION
                    if (timestamp != null) putExtra("timestamp", timestamp)
                }

                this.sendBroadcast(intent)

//                val command = if (id >= 0) {
//                    EditHabitCommand(
//                        component.habitList,
//                        id,
//                        habit
//                    )
//                } else {
//                    CreateHabitCommand(
//                        component.modelFactory,
//                        component.habitList,
//                        habit
//                    )
//                }
//                component.commandRunner.run(command)

                finish()
            }

        }else{
            val habitIds = ArrayList<Long>()
            val habitNames = ArrayList<String>()
            for (h in habitList) {
                if (h.isArchived) continue
                if (h.isNumerical and shouldHideNumerical()) continue
                if (!h.isNumerical and shouldHideBoolean()) continue
                habitIds.add(h.id!!)
                habitNames.add(h.name)
            }

            if (habitNames.isEmpty()) {
                setContentView(R.layout.widget_empty_activity)
                findViewById<TextView>(R.id.message).setText(getEmptyMessage())
                return
            }

            setContentView(R.layout.widget_configure_activity)
            val listView = findViewById<ListView>(R.id.listView)
            val saveButton = findViewById<Button>(R.id.buttonSave)

            with(listView) {
                adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_list_item_multiple_choice,
                    habitNames
                )
                choiceMode = CHOICE_MODE_MULTIPLE
                itemsCanFocus = false
            }
            saveButton.setOnClickListener {
                val selectedIds = mutableListOf<Long>()
                for (i in 0..listView.count) {
                    if (listView.isItemChecked(i)) {
                        selectedIds.add(habitIds[i])
                    }
                }
                confirm(selectedIds)
            }
        }
    }

    fun confirm(selectedIds: List<Long>) {
        widgetPreferences.addWidget(widgetId, selectedIds.toLongArray())
        widgetUpdater.updateWidgets()
        setResult(
            RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_APPWIDGET_ID, widgetId)
            }
        )
        finish()
    }
}