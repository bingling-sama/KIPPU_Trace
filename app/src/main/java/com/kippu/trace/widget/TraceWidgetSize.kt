package com.kippu.trace.widget

import com.kippu.trace.R

// 小组件尺寸定义
enum class TraceWidgetSize(val layoutRes: Int) {
    TWO_BY_TWO(layoutRes = R.layout.widget_trace_2x2),
    THREE_BY_TWO(layoutRes = R.layout.widget_trace_3x2),
    FOUR_BY_TWO(layoutRes = R.layout.widget_trace_4x2),
}
