package com.mika10095.escapefromkotlin

import android.graphics.Canvas
import android.view.SurfaceHolder

class GameThread(val surfaceHolder: SurfaceHolder, val gameInstance: GameInstance) : Thread() {
    var running = false
    var frames = 0
    var FPS = 0
    var updateMili = 0
    //short for delta time
    val dt get() = updateMili.toDouble()/1000.0
    var lastMili = 0
    var secondTimer = System.currentTimeMillis()
    override fun run(){
        while (running)
        {


            frames++
            val currentTime = System.currentTimeMillis()
            updateMili = (currentTime-lastMili).toInt()
            lastMili = currentTime.toInt()

            if (currentTime - secondTimer > 1000)
            {
                println("Framerate: $frames")
                FPS = frames
                frames = 0
                secondTimer = currentTime
            }

            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null){
                synchronized(surfaceHolder){
                    gameInstance.draw(canvas)
                }
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
        }
    }
}
