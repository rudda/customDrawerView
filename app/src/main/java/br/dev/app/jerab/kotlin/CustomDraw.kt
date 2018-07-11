package br.dev.app.jerab.kotlin

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.GestureDetector

import android.util.AttributeSet
import android.view.MotionEvent


/**
 * Created by Ruddá Beltrao on 11/07/18.
 * rudda@jerab.com.br
 * desenvolvedor mobile jr.
 */

    class CustomDraw(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private val TOUCH_TOLERANCE = 10f
    private val bitmap : Bitmap? = null
    private var bitmapCanvas : Canvas? = null
    private var paintLine : Paint? = null
    private var paintScreen : Paint? = null
    private val pathMap = HashMap<Int, Path>()

    private val previousPointMap = HashMap<Int, Point>()
    private var singleTapDetector: GestureDetector? = null


    init {

        paintScreen = Paint() // usado para exibir bitmap na tela
// ajusta as configurações de exibição iniciais da linha pintada
        paintLine = Paint()
        paintLine!!.isAntiAlias = true // suaviza as bordas da linha desenhada
        paintLine!!.color = Color.BLACK // a cor padrão é preto
        paintLine!!.style = Paint.Style.STROKE // linha cheia
        paintLine!!.setStrokeWidth(5f) // configura a largura de linha padrão
        paintLine!!.strokeCap = Paint.Cap.ROUND // extremidades da linha arredondadas
// GestureDetector para toques rápidos


        // singleTapDetector = GestureDetector(context, singleTapListener)

    }

    override fun onDraw(canvas: Canvas) {

//        canvas.drawBitmap(bitmap!!, 0f, 0f, paintScreen)
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);

        this.bitmapCanvas = Canvas(bitmap!!)

        bitmap.eraseColor(Color.WHITE); // apaga o Bitmap com branco

    }

    public fun clear(){

        pathMap.clear(); // remove todos os caminhos
        previousPointMap.clear(); // remove todos os pontos anteriores
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE)
        }; // apaga o bitmap
        invalidate(); // atualiza a tela
    }


    public fun setDrawingColor( color : Int){

        this.paintLine!!.setColor(color);

    }

    public fun setLineWidth(width : Float){

        paintLine!!.setStrokeWidth(width);

    }





}