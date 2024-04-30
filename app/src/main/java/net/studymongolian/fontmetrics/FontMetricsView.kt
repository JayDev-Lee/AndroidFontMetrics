package net.studymongolian.fontmetrics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.time.format.TextStyle

class FontMetricsView : View {
    private var mText: String? = null
    private var mTextSize = 0
    private lateinit var mAscentPaint: Paint
    private lateinit var mTopPaint: Paint
    private lateinit var mBaselinePaint: Paint
    private lateinit var mDescentPaint: Paint
    private lateinit var mBottomPaint: Paint
    private lateinit var mMeasuredWidthPaint: Paint
    private lateinit var mTextBoundsPaint: Paint
    private lateinit var mTextPaint: TextPaint
    private lateinit var mLinePaint: Paint
    private lateinit var mRectPaint: Paint
    private lateinit var mBounds: Rect
    private var mIsTopVisible = false
    private var mIsAscentVisible = false
    private var mIsBaselineVisible = false
    private var mIsDescentVisible = false
    private var mIsBottomVisible = false
    private var mIsBoundsVisible = false
    private var mIsWidthVisible = false


    constructor(context: Context?) : super(context) {
        init()
    }


    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        mText = "My text line"
        mTextSize = DEFAULT_FONT_SIZE_PX
        mTextPaint = TextPaint()
        mTextPaint.isAntiAlias = true
        mTextPaint.textSize = mTextSize.toFloat()
        mTextPaint.color = Color.BLACK

        mLinePaint = Paint()
        mLinePaint.color = Color.RED
        mLinePaint.strokeWidth = STROKE_WIDTH

        mAscentPaint = Paint()
        mAscentPaint.color = resources.getColor(R.color.ascent)
        mAscentPaint.strokeWidth = STROKE_WIDTH

        mTopPaint = Paint()
        mTopPaint.color = resources.getColor(R.color.top)
        mTopPaint.strokeWidth = STROKE_WIDTH

        mBaselinePaint = Paint()
        mBaselinePaint.color = resources.getColor(R.color.baseline)
        mBaselinePaint.strokeWidth = STROKE_WIDTH

        mBottomPaint = Paint()
        mBottomPaint.color = resources.getColor(R.color.bottom)
        mBottomPaint.strokeWidth = STROKE_WIDTH

        mDescentPaint = Paint()
        mDescentPaint.color = resources.getColor(R.color.descent)
        mDescentPaint.strokeWidth = STROKE_WIDTH

        mMeasuredWidthPaint = Paint()
        mMeasuredWidthPaint.color = resources.getColor(R.color.measured_width)
        mMeasuredWidthPaint.strokeWidth = STROKE_WIDTH

        mTextBoundsPaint = Paint()
        mTextBoundsPaint.color = resources.getColor(R.color.text_bounds)
        mTextBoundsPaint.strokeWidth = STROKE_WIDTH
        mTextBoundsPaint.style = Paint.Style.STROKE

        mRectPaint = Paint()
        mRectPaint.color = Color.BLACK
        mRectPaint.strokeWidth = STROKE_WIDTH
        mRectPaint.style = Paint.Style.STROKE


        mBounds = Rect()

        mIsTopVisible = true
        mIsAscentVisible = true
        mIsBaselineVisible = true
        mIsDescentVisible = true
        mIsBottomVisible = true
        mIsBoundsVisible = true
        mIsWidthVisible = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // center the text baseline vertically
        val verticalAdjustment = this.height / 2
        canvas.translate(0f, verticalAdjustment.toFloat())

        var startX = paddingLeft.toFloat()
        var startY = 0f
        var stopX = this.measuredWidth.toFloat()
        var stopY: Float

        // draw text
        canvas.drawText(mText!!, startX, startY, mTextPaint) // x=0, y=0

        // draw lines
        startX = 0f

        if (mIsTopVisible) {
            startY = mTextPaint.fontMetrics.top
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mTopPaint)
        }

        if (mIsAscentVisible) {
            startY = mTextPaint.fontMetrics.ascent
            stopY = startY
            //mLinePaint.setColor(Color.GREEN);
            canvas.drawLine(startX, startY, stopX, stopY, mAscentPaint)
        }

        if (mIsBaselineVisible) {
            startY = 0f
            stopY = startY
            canvas.drawLine(startX, startY, stopX, stopY, mBaselinePaint)
        }

        if (mIsDescentVisible) {
            startY = mTextPaint.fontMetrics.descent
            stopY = startY
            //mLinePaint.setColor(Color.BLUE);
            canvas.drawLine(startX, startY, stopX, stopY, mDescentPaint)
        }

        if (mIsBottomVisible) {
            startY = mTextPaint.fontMetrics.bottom
            stopY = startY
            // mLinePaint.setColor(ORANGE);
            mLinePaint.color = Color.RED
            canvas.drawLine(startX, startY, stopX, stopY, mBaselinePaint)
        }

        if (mIsBoundsVisible) {
            mTextPaint.getTextBounds(mText, 0, mText!!.length, mBounds)
            val dx = paddingLeft.toFloat()
            canvas.drawRect(
                mBounds.left + dx,
                mBounds.top.toFloat(),
                mBounds.right + dx,
                mBounds.bottom.toFloat(),
                mTextBoundsPaint
            )
        }

        if (mIsWidthVisible) {
            // get measured width


            val width = mTextPaint.measureText(mText)

            // get bounding width so that we can compare them
            mTextPaint.getTextBounds(mText, 0, mText!!.length, mBounds)

            // draw vertical line just before the left bounds
            startX = paddingLeft + mBounds.left - (width - mBounds.width()) / 2
            stopX = startX
            startY = -verticalAdjustment.toFloat()
            stopY = startY + this.height
            canvas.drawLine(startX, startY, stopX, stopY, mMeasuredWidthPaint)

            // draw vertical line just after the right bounds
            startX += width
            stopX = startX
            canvas.drawLine(startX, startY, stopX, stopY, mMeasuredWidthPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var width = 200
        var height = 200

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthRequirement = MeasureSpec.getSize(widthMeasureSpec)
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthRequirement
        } else if (widthMode == MeasureSpec.AT_MOST && width > widthRequirement) {
            width = widthRequirement
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightRequirement = MeasureSpec.getSize(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightRequirement
        } else if (heightMode == MeasureSpec.AT_MOST && width > heightRequirement) {
            height = heightRequirement
        }

        setMeasuredDimension(width, height)
    }

    val fontMetrics: Paint.FontMetrics
        // getters
        get() = mTextPaint.fontMetrics

    val textBounds: Rect
        get() {
            mTextPaint.getTextBounds(mText, 0, mText!!.length, mBounds)
            return mBounds
        }

    val measuredTextWidth: Float
        get() = mTextPaint.measureText(mText)

    val typeface: Typeface
        get() = mTextPaint.typeface

    // setters
    fun setText(text: String?) {
        mText = text
        invalidate()
        requestLayout()
    }

    fun setTextSizeInPixels(pixels: Int) {
        mTextSize = pixels
        mTextPaint.textSize = mTextSize.toFloat()
        invalidate()
        requestLayout()
    }

    fun setTopVisible(isVisible: Boolean) {
        mIsTopVisible = isVisible
        invalidate()
    }

    fun setAscentVisible(isVisible: Boolean) {
        mIsAscentVisible = isVisible
        invalidate()
    }

    fun setBaselineVisible(isVisible: Boolean) {
        mIsBaselineVisible = isVisible
        invalidate()
    }

    fun setDescentVisible(isVisible: Boolean) {
        mIsDescentVisible = isVisible
        invalidate()
    }

    fun setBottomVisible(isVisible: Boolean) {
        mIsBottomVisible = isVisible
        invalidate()
    }

    fun setBoundsVisible(isVisible: Boolean) {
        mIsBoundsVisible = isVisible
        invalidate()
    }

    fun setWidthVisible(isVisible: Boolean) {
        mIsWidthVisible = isVisible
        invalidate()
    }

    fun setTypeface(typeface: Typeface) {
        mTextPaint.typeface = typeface
        invalidate()
    }

    companion object {
        const val DEFAULT_FONT_SIZE_PX: Int = 100

        //private static final int PURPLE = Color.parseColor("#9315db");
        //private static final int ORANGE = Color.parseColor("#ff8a00");
        private const val STROKE_WIDTH = 5.0f
    }
}
