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

package org.isoron.uhabits.widgets

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import org.isoron.platform.gui.Color
import org.isoron.platform.gui.toInt
import org.isoron.uhabits.R
import org.isoron.uhabits.core.models.Entry
import org.isoron.uhabits.core.models.Habit
import org.isoron.uhabits.core.models.PaletteColor
import org.isoron.uhabits.core.ui.views.WidgetTheme
import org.isoron.uhabits.core.utils.DateUtils
import org.isoron.uhabits.widgets.activities.HabitPickerDialog
import org.isoron.uhabits.widgets.views.CheckmarkWidgetView

open class CheckmarkWidget(
    context: Context,
    widgetId: Int,
    protected val habit: Habit,
    stacked: Boolean = false
) : BaseWidget(context, widgetId, stacked) {

    override val defaultHeight: Int = 125
    override val defaultWidth: Int = 125

    override fun getOnClickPendingIntent(context: Context): PendingIntent? {
        return if (habit.isNumerical) {
            pendingIntentFactory.showNumberPicker(habit, DateUtils.getTodayWithOffset())
        } else {
            pendingIntentFactory.toggleCheckmark(habit, null)
        }
    }

    override fun refreshData(widgetView: View) {
        (widgetView as CheckmarkWidgetView).apply {
            val today = DateUtils.getTodayWithOffset()
            setBackgroundAlpha(preferedBackgroundAlpha)
            Log.d("CheckmarkWidget", "refreshData: ${habit.name} ${habit.originalEntries.get(today).value}")
            when(habit.originalEntries.get(today).value) {
                Entry.YES_MANUAL -> {
                    activeColor = ContextCompat.getColor(context, R.color.red_700)
                }
                Entry.YES_MANUAL_1 -> {
                    activeColor = ContextCompat.getColor(context, R.color.green_700)
                }
                Entry.YES_MANUAL_2 -> {
                    activeColor = ContextCompat.getColor(context, R.color.blue_700)
                }
                Entry.NO -> {

                }
                Entry.SKIP -> {

                }
                Entry.UNKNOWN -> {

                }
                else ->{
                    activeColor = WidgetTheme().color(habit.color).toInt()
                }
            }
            name = habit.name
            entryValue = habit.computedEntries.get(today).value
            if (habit.isNumerical) {
                isNumerical = true
                entryState = getNumericalEntryState()
            } else {
                entryState = habit.computedEntries.get(today).value
            }
            percentage = habit.scores[today].value.toFloat()
            refresh()
        }
    }

    override fun buildView(): View {
        return CheckmarkWidgetView(context)
    }

    private fun getNumericalEntryState(): Int {
        return if (habit.isCompletedToday()) {
            Entry.YES_MANUAL
        } else {
            Entry.NO
        }
    }
}
