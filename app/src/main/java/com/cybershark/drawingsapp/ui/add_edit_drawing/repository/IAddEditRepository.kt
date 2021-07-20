package com.cybershark.drawingsapp.ui.add_edit_drawing.repository

import com.cybershark.drawingsapp.data.room.entities.DrawingEntity

interface IAddEditRepository {
    suspend fun getDrawingById(drawingId: Int): Result<DrawingEntity>
    suspend fun updateDrawing(value: DrawingEntity?): Result<Unit>
    suspend fun addDrawing(value: DrawingEntity?): Result<Unit>
}