package pl.arturtamborski.spookysquare;

import java.util.Random;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)
                    && action != null && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        setContentView(new MainSurfaceView(this, null));
    }

    public static int getRandomColor() {
        Random random = new Random();

        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        int a = 0xff;

        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int bleach(int color, float amount) {
        int a = (color & 0xff000000) >> 24;
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = (color & 0x000000ff);

        int light = (3*r + b + 4*g) >> 3;

        if (light > Integer.MAX_VALUE / 2) {
            amount = 0.5f + Math.abs(0.5f - amount);
        } else {
            amount = 0.5f - Math.abs(0.5f - amount);
        }

        r = (int)((r * (1 - amount) / 255 + amount) * 255);
        g = (int)((g * (1 - amount) / 255 + amount) * 255);
        b = (int)((b * (1 - amount) / 255 + amount) * 255);

        return a << 24 | r << 16 | g << 8 | b;
    }
}
