package uni.fmi.miroslav.carcompanion.customelements;

import android.view.View;
import android.widget.TextView;

public class ToggleView<T extends TextView> {

    T view;
    String modeOn;
    String modeOff;
    boolean mode = true;

    public ToggleView (T view, String on, String off){
        modeOn = on;
        modeOff = off;
        this.view = view;
    }

    public ToggleView (T view){
        this(view, null, null);
    }

    public void toggle(){
        mode = !mode;
        if (modeOn != null && modeOff != null)
            update();
    }

    private void update(){ view.setText(mode ? modeOn : modeOff); }

    public boolean isOn() { return mode; }

    public void switchTo(boolean bool){
        if (mode != bool){
            toggle();
        }
    }

    public void updateInfo(String on, String off) {
        updateInfo( on, off, true);
    }

    public void updateInfo(String on, String off, boolean mod){
        mode = mode && mod;
        modeOn = on;
        modeOff = off;
        update();
    }

    public void setOnClickListener(View.OnClickListener listener){
        view.setOnClickListener(listener);
    }
}
