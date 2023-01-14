package com.gsq.iart.app.ext

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

fun RecyclerView.vertical(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
    return this
}

fun RecyclerView.horizontal(): RecyclerView {
    layoutManager = LinearLayoutManager(this.context, RecyclerView.HORIZONTAL, false)
    return this
}

fun RecyclerView.grid(row: Int): RecyclerView {
    layoutManager = GridLayoutManager(this.context, if (row < 1) 1 else row)
    return this
}

fun RecyclerView.flow(
    row: Int = 0,
    orientation: Int = StaggeredGridLayoutManager.VERTICAL
): RecyclerView {
    layoutManager = StaggeredGridLayoutManager(
        if (row < 1) 1 else row,
        if (orientation != StaggeredGridLayoutManager.HORIZONTAL)
            StaggeredGridLayoutManager.HORIZONTAL
        else StaggeredGridLayoutManager.HORIZONTAL
    )
    return this
}