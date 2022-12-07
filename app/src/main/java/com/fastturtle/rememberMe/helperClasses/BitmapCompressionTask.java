package com.fastturtle.rememberMe.helperClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class BitmapCompressionTask extends AsyncTask<Uri, Void, Bitmap> {
    private final WeakReference<Context> contextRef;
    BitmapCompressedListener listener;

    public BitmapCompressionTask(Context context, BitmapCompressedListener listener) {
        this.contextRef = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Uri... uris) {
        Bitmap compressedBitmap = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contextRef.get().getContentResolver(), uris[0]);
            compressedBitmap = Utils.getCompressedBitmap(bitmap, 300);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return compressedBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        listener.onBitmapCompressed(bitmap);
    }

    public interface BitmapCompressedListener {
        void onBitmapCompressed(Bitmap bitmap);
    }
}
