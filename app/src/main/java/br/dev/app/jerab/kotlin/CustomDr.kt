package br.dev.app.jerab.kotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

import java.util.HashMap

/**
 * Created by Ruddá Beltrao on 11/07/18.
 * rudda@jerab.com.br
 * desenvolvedor mobile jr.
 */
class CustomDr(context: Context) : View(context) {
    private val bitmap: Bitmap? = null // área de desenho a exibir ou salvar
    private val bitmapCanvas: Canvas? = null // usado para desenhar no bitmap
    private val paintScreen: Paint? = null // usado para desenhar o bitmap na tela
    private val paintLine: Paint? = null // usado para desenhar linhas no bitmap
    private val pathMap = HashMap<Int, Path>()

    private val previousPointMap = HashMap<Int, Point>()

    override fun onDraw(canvas: Canvas) {

        canvas.drawBitmap(bitmap!!, 0f, 0f, paintScreen)
        for (key in pathMap.keys) {
            canvas.drawPath(pathMap[key], paintLine!!) // desenha a linha
        }


    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val action = event.actionMasked // tipo de evento
        val actionIndex = event.actionIndex // ponteiro (isto é, o dedo)

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {

            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex))

        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {

            touchEnded(event.getPointerId(actionIndex))


        } else {

            touchMoved(event)


        }

        invalidate()
        return true

    }

    private fun touchMoved(event: MotionEvent) {

        for (i in 0 until event.pointerCount) {
            // obtém o identificador e o índice do ponteiro
            val pointerID = event.getPointerId(i)
            val pointerIndex = event.findPointerIndex(pointerID)

            // se existe um caminho associado ao ponteiro
            if (pathMap.containsKey(pointerID)) {
                // obtém as novas coordenadas do ponteiro
                val newX = event.getX(pointerIndex)
                val newY = event.getY(pointerIndex)

                // obtém o objeto Path e o objeto Point
                // anterior associados a esse ponteiro

                val path = pathMap[pointerID]
                val point = previousPointMap[pointerID]


                // calcula quanto o usuário moveu a partir da última atualização
                val deltaX = Math.abs(newX - point!!.x)
                val deltaY = Math.abs(newY - point!!.y)

                // se a distância é significativa o suficiente para ter importância
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move o caminho para o novo local

                    path!!.quadTo(point.x.toFloat(), point.y.toFloat(), (newX + point.x) / 2,
                            (newY + point.y) / 2)

                    // armazena as novas coordenadas
                    point.x = newX.toInt()
                    point.y = newY.toInt()
                }
            }
        }
    }

    private fun touchStarted(x: Float, y: Float, pointerId: Int) {

        val path: Path?
        val point: Point?

        if (pathMap.containsKey(pointerId)) {

            path = pathMap[pointerId]
            path!!.reset()
            point = previousPointMap[pointerId]

        } else {

            path = Path()
            pathMap[pointerId] = path
            point = Point()
            previousPointMap[pointerId] = point


        }

        path.moveTo(x, y)
        point!!.x = x.toInt()
        point!!.y = y.toInt()


    }

    private fun touchEnded(lineID: Int) {


        val path = pathMap[lineID] // obtém o objeto Path correspondente
        bitmapCanvas!!.drawPath(path, paintLine!!) // desenha em bitmapCanvas
        path!!.reset()


    }

    companion object {

        private val TOUCH_TOLERANCE = 10f
    }


}
