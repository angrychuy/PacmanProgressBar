package com.carlosdurazo.pacman

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import javax.swing.UIManager

class PacmanProgressBarListener : LafManagerListener {

    override fun lookAndFeelChanged(source: LafManager) {
        UIManager.put("ProgressBarUI", PacmanProgressBarUi::class.qualifiedName)
        UIManager.getDefaults()[PacmanProgressBarUi::class.qualifiedName] = PacmanProgressBarUi::class
    }
}
