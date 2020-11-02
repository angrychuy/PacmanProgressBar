package com.carlosdurazo.pacman

import com.intellij.openapi.util.ScalableIcon
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.SwingConstants
import javax.swing.plaf.ComponentUI
import javax.swing.plaf.basic.BasicProgressBarUI

class PacmanProgressBarUi : BasicProgressBarUI() {
    @Volatile private var offset: Int = 0
    @Volatile private var objPosition: Int = 0
    @Volatile private var direction: Int = 1
    private val arcRoundCorner: Float = JBUIScale.scale(15f)

    override fun getPreferredSize(c: JComponent?): Dimension {
        return Dimension(super.getPreferredSize(c).width, JBUI.scale(20))
    }

    override fun installListeners() {
        super.installListeners()
        progressBar.addComponentListener(object : ComponentAdapter() {})
    }

    override fun getBoxLength(availableLength: Int, otherDimension: Int): Int {
        return availableLength
    }

    override fun paintIndeterminate(g: Graphics?, c: JComponent?) {
        if (g !is Graphics2D) return

        val b: Insets = progressBar.insets
        val barRectWidth: Int = progressBar.width - (b.right + b.left)
        val barRectHeight: Int = progressBar.height - (b.top + b.bottom)

        if (barRectWidth <= 0 || barRectHeight <= 0) return

        val w: Int = c!!.width
        var h: Int = c.preferredSize.height
        if (!isEven(c.height - h)) h++

        val containingRoundRect = Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, arcRoundCorner, arcRoundCorner))
        g.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        g.fill(containingRoundRect)

        offset = (offset + 1) % JBUI.scale(16)
        objPosition += direction
        if (objPosition <= 8) { // Object is "touching" the left corner
            objPosition = 8
            direction = 1 // Set to move right
        } else if (objPosition >= w - JBUI.scale(10)) { // Object is "touching" the right corner
            objPosition = w - JBUI.scale(15)
            direction = -1 // Set to move left
        }

        val area = Area(Rectangle2D.Float(0F, 0F, w.toFloat(), h.toFloat()))
        area.subtract(Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, arcRoundCorner, arcRoundCorner)))
        g.paint = Gray._128
        if (c.isOpaque) {
            g.fill(area)
        }
        area.subtract(Area(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), arcRoundCorner, arcRoundCorner)))
        if (c.isOpaque) {
            g.fill(area)
        }

        // Is the object moving to the right?
        val scaledIcon: Icon = if (direction > 0) (PAC_GIF_R as ScalableIcon) else (PAC_GIF_L as ScalableIcon)

        scaledIcon.paintIcon(progressBar, g, objPosition - JBUI.scale(10), 0)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            if (progressBar.orientation == SwingConstants.HORIZONTAL) {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.x, boxRect.width)
            } else {
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, boxRect.y, boxRect.height)
            }
        }
    }

    override fun paintDeterminate(g: Graphics?, c: JComponent) {
        if (g !is Graphics2D) return

        if (progressBar.orientation != SwingConstants.HORIZONTAL ||
                !c.componentOrientation.isLeftToRight) {
            super.paintDeterminate(g, c)
            return
        }

        val b: Insets = progressBar.insets
        val w = progressBar.width
        var h = progressBar.preferredSize.height
        if (!isEven(c.height - h)) h++

        val R: Float = JBUIScale.scale(15f)
        val R2: Float = JBUIScale.scale(9f)

        if (w <= 0 || h <= 0) {
            return
        }

        // Creates the rectangle object used for the background color
        val containingRoundRect: Area = Area(RoundRectangle2D.Float(1f, 1f, w - 2f, h - 2f, R, R))
        // Sets colors for background used on regular/dark themes
        g.color = JBColor(Gray._165.withAlpha(50), Gray._88.withAlpha(50))
        g.fill(containingRoundRect)

        val amountFull = getAmountFull(b, w, h)
        val parent: Container = c.parent
        val background: Color = if (parent != null) parent.background else UIUtil.getPanelBackground()

        g.color = background

        PAC_GIF_R.paintIcon(progressBar, g, amountFull - (JBUI.scale(9) * 2), 0)

        // Deal with possible text painting
        if (progressBar.isStringPainted) {
            paintString(g, b.left, b.top, w, h, amountFull, b)
        }
    }

    private fun paintString(g: Graphics, x: Int, y: Int, w: Int, h: Int, fillStart: Int, amountFull: Int) {
        if (g !is Graphics2D) return

        val progressString = progressBar.string
        g.setFont(progressBar.font)
        val renderLocation: Point = getStringPlacement(g, progressString,
                x, y, w, h)
        val oldClip: Rectangle = g.getClipBounds()

        g.setClip(oldClip)
    }

    companion object {
        fun createUI(c: JComponent): ComponentUI {
            c.border = JBUI.Borders.empty().asUIResource()
            return PacmanProgressBarUi()
        }
    }
}
