package dk.arriva.mobilbillet.animationtesting;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class MaskTiltImageView extends View {
    private static final float DEFAULT_TILT_STRENGTH = 1.0f;
    private final TiltEntity mask = new TiltEntity();
    private final TiltEntity image = new TiltEntity();
    private final TiltEntity result = new TiltEntity();
    private Canvas resultCanvas;
    private AccelerometerTiltMonitor tiltMonitor;
    private float tiltStrength = DEFAULT_TILT_STRENGTH;
    private float tiltX = 0;
    private int viewWidth;
    private int viewHeight;

    public MaskTiltImageView(@NonNull Context context) {
        super(context);
        initView(context, null);
    }

    public MaskTiltImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MaskTiltImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        checkAttributeSet(attributeSet);
        setUpValuesFromAttributeSet(attributeSet);
        setUpTiltMonitor(context);
        mask.initXfermode(PorterDuff.Mode.DST_IN);
    }

    private void checkAttributeSet(@Nullable AttributeSet attributeSet) {
        if (attributeSet == null) {
            throw new IllegalStateException("Must provide both source and mask");
        }
    }

    private void setUpValuesFromAttributeSet(@NonNull AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attributeSet, R.styleable.MaskTiltImageView, 0, 0);
        image.drawable = typedArray.getDrawable(R.styleable.MaskTiltImageView_src);
        mask.drawable = typedArray.getDrawable(R.styleable.MaskTiltImageView_mask);
        tiltStrength = typedArray.getFloat(
                R.styleable.MaskTiltImageView_tiltStrength, DEFAULT_TILT_STRENGTH);
        typedArray.recycle();
    }

    private void setUpTiltMonitor(@NonNull Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        tiltMonitor = new AccelerometerTiltMonitor(sensorManager);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        viewWidth = width;
        viewHeight = height;
        mask.initializeWithSize(width * 2, height);
        image.initializeWithSize(width, height);
        result.bitmap = Bitmap.createBitmap(
                mask.bitmap.getWidth(), mask.bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        resultCanvas = new Canvas(result.bitmap);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tiltMonitor.register(new TiltMonitor.TiltListener() {
            @Override
            public void onTiltChanged(float x, float y, float z) {
                tiltX = x;
                postInvalidate();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        tiltMonitor.unregister();
        super.onDetachedFromWindow();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        resultCanvas.save();
        canvas.save();
        float tiltedCenter = calculateConstrainedTiltCenter();
        resultCanvas.drawBitmap(image.bitmap, 0, 0, null);
        resultCanvas.drawBitmap(mask.bitmap, tiltedCenter, 0, mask.paint);
        canvas.drawBitmap(result.bitmap, 0, 0, result.paint);
        canvas.restore();
        resultCanvas.restore();
    }

    /*      Bitmap original = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.original_image);
        Bitmap mask = Bitmap.createBitmap(getContext().getResources(),R.drawable.mask_image);

        //You can change original image here and draw anything you want to be masked on it.

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        tempCanvas.drawBitmap(original, 0, 0, null);
        tempCanvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(null);

        //Draw result after performing masking
        canvas.drawBitmap(result, 0, 0, new Paint());*/

    private float calculateConstrainedTiltCenter() {
        int halfWidth = viewWidth / 2;
        float positionTilt = (viewWidth * -tiltX * tiltStrength) / 2;
        positionTilt = constrainPosition(halfWidth, positionTilt);
        return positionTilt - halfWidth;
    }

    private float constrainPosition(int halfWidth, float positionTilt) {
        if (positionTilt > halfWidth) {
            positionTilt = halfWidth;
        } else if (positionTilt < -halfWidth) {
            positionTilt = -halfWidth;
        }
        return positionTilt;
    }

    private static class TiltEntity {
        private final Paint paint = new Paint();
        private Drawable drawable;
        private Bitmap bitmap;

        void initXfermode(PorterDuff.Mode mode) {
            paint.setXfermode(new PorterDuffXfermode(mode));
        }

        void initializeWithSize(int width, int height) {
            bitmap = drawableToBitmap(drawable, width, height);
        }

        private Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }
            Bitmap bitmap;
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}