package leo.test.com.compasstest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private ImageView compassImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compassImg=(ImageView)findViewById(R.id.compass_img);

        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Sensor magneticSensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener,magneticSensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(listener,accelerometerSensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener=new SensorEventListener() {

        float[] accelerometerValues=new float[3];
        float[] magneticValues=new float[3];

        private float lastRotateDegree;
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                accelerometerValues=event.values.clone();
            }else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD){
                magneticValues=event.values.clone();
            }

            float[] R=new float[9];
            float[] value=new float[3];
            SensorManager.getRotationMatrix(R,null,accelerometerValues,magneticValues);
            SensorManager.getOrientation(R,value);
            //将计算出的角度取反，用于旋转指南针背景图

            float rotateDegree=-(float)Math.toDegrees(value[0]);
            if (Math.abs(rotateDegree-lastRotateDegree)>1){
                RotateAnimation animation=new RotateAnimation(lastRotateDegree,rotateDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                animation.setFillAfter(true);
                compassImg.startAnimation(animation);
                lastRotateDegree=rotateDegree;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
