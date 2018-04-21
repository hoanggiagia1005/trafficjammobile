package trafficjam.k60n.com.trafficjam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import trafficjam.k60n.com.trafficjam.R;

public class FirstScreenAct extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.edt_start)
    void click(View view) {
        switch (view.getId()) {
            case R.id.edt_start: {
                Intent i = new Intent(this, MapsActivity.class);
                startActivityForResult(i, 1);
                break;

            }
            default:
                break;
        }
    }
}
