package cl.felipebarriga.android.smartbandlogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


import cl.felipebarriga.android.smartbandlogger.ml.MyModel;
import cl.felipebarriga.android.utils.PreferencesUtils;

/**
 * Copyright (c) 2015 Felipe Barriga Richards. See Copyright Notice in LICENSE file.
 */
public class MainActivity extends Activity implements OnEventListener {

    private LoggerSingleton mLoggerSingleton = null;

    private static final String LOG_TAG = "SmartBandLogger";
    private final String CLASS = getClass().getSimpleName();

    private static final int HISTORY_SIZE = 300;

    private PreferencesUtils mPrefs = null;

    private TextView mStatus        = null;
    private TextView mRecords       = null;
    private TextView mDataRate      = null;
    private TextView mFilename      = null;
    private TextView mLogSize       = null;
    private TextView mElapsedTime   = null;

    private XYPlot mHistoryPlot            = null;
    private SimpleXYSeries mXHistorySeries = null;
    private SimpleXYSeries mYHistorySeries = null;
    private SimpleXYSeries mZHistorySeries = null;

    private Redrawer redrawer;
    private boolean mDebugMessages = false;
    private LoggerSingleton.Status mCurrentStatus;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );

        mLoggerSingleton = LoggerSingleton.getInstance();

        mStatus         = ( TextView ) findViewById( R.id.main_status_lbl );
        mRecords        = ( TextView ) findViewById( R.id.main_records_lbl );
        mDataRate       = ( TextView ) findViewById( R.id.main_data_rate_lbl );
        mFilename       = ( TextView ) findViewById( R.id.main_filename_lbl );
        mLogSize        = ( TextView ) findViewById( R.id.main_log_size_lbl );
        mElapsedTime    = ( TextView ) findViewById( R.id.main_elapsed_time_lbl );
        mHistoryPlot    = ( XYPlot   ) findViewById( R.id.main_history_plot );

        mPrefs = new PreferencesUtils( this );

        initPlot();
        updateUI();
        redrawPlot();
        mLoggerSingleton.setOnEventListener( this );
        updateElapsedTime();


        int TIME_STAMP = 50;
        List<Float> ax = new ArrayList<>();
        List<Float> ay = new ArrayList<>();
        List<Float> az = new ArrayList<>();
        for (int i = 0; i <50; i++ ){
            ax.add((float)1.0);
            ay.add((float)2.0);
            az.add((float)3.0);
        }


        try {
            //float [][][] data = {{{6.5F,37.0F,35.0F },{ 9.0F,37.0F,37.0F },{11.5F,38.0F,39.5F},{12.0F,39.0F,35.0F },{13.0F,  37.5F, 36.5F},{15.0F,  38.5F, 36.0F },{13.0F,  39.0F,  37.0F },{12.5F, 41.0F,  38.0F},{11.0F,  41.0F,  39.0F },{10.0F,  40.5F, 40.0F },{11.0F,  41.5F, 39.5F},{ 6.0F, 40.0F,  38.0F },{ 5.0F, 39.5F, 39.5F},{ 8.0F,  38.0F,  35.0F },{ 6.0F, 37.0F,  35.5F},{ 8.5F, 35.5F, 33.5F},{11.0F,  36.0F,  33.0F },{14.5F, 36.0F,  32.5F},{13.0F,  37.0F,  32.0F },{16.0F, 35.0F,  32.5F},{15.0F,  36.0F,  33.5F},{14.0F,  37.0F,  33.0F },{12.0F,  36.5F,34.5F},{ 8.0F,  37.0F,  34.0F },{12.0F,37.0F,  36.0F },{12.5F, 40.0F, 38.5F},{14.0F,  37.0F,  37.0F },{13.0F,38.0F,36.5F},{15.0F,38.0F,36.0F},{11.5F,38.0F,38.5F},{10.5F,40.0F,38.5F}, {10.0F,40.0F,40.0F}, { 9.5F,41.5F,41.0F},{ 7.0F,40.0F,41.0F},{5.0F,40.0F,0.5F},{ 6.0F,36.5F,35.0F },{ 6.0F,36.3F,5.0F},{ 9.0F,5.0F,34.0F},{15.0F,36.0F,33.0F},{13.0F, 35.5F, 31.0F},{15.3F,6.5F,32.5F},{17.0F,  35.3F, 2.0F},{15.0F,36.0F,33.0F},{15.0F,36.0F,33.0F},{14.5F,37.0F,35.5F},{9.5F,36.0F,34,0F},{13.0F,37.0F,37.0F},{11.5F,38.5F,37.5F},{11.0F,39.0F,37.0F},{13.0F,38.0F,38.0F}}};
            List<Float> data = new ArrayList<>();
            data.addAll(ax.subList(0, TIME_STAMP));
            data.addAll(ay.subList(0, TIME_STAMP));
            data.addAll(az.subList(0, TIME_STAMP));
            System.out.print(data);
            MyModel model = MyModel.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 50, 3}, DataType.FLOAT32);
            inputFeature0.loadArray(toFloatArray(data));

            // Runs model inference and gets result.
            MyModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    private float[] toFloatArray(List<Float> data){
        int i = 0;

        float[] array = new float[data.size()];
        for (Float f: data){
            array[i++] = (f !=null ? f: Float.NaN);
        }
        //Log.d(TAG, "toFloatArray: " + array);
        return array;

    }

    @Override
    public void onResume() {
        super.onResume();
        mDebugMessages = mPrefs.getBooleanPreference( "debugMessages", R.string.settings_default_debug );
        redrawer.start();
    }

    @Override
    public void onPause() {
        redrawer.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLoggerSingleton.setOnEventListener( null );
        redrawer.finish();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main, menu );
        return true;
    }

    public void showAboutDialog() {
        View messageView = getLayoutInflater().inflate( R.layout.about, null, false );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle( R.string.app_name);
        builder.setView( messageView);
        builder.create();
        builder.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                showAboutDialog();
                return true;

            case R.id.preferences:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.open_log_directory:
                startActivity(new Intent(this, ShowLogsActivity.class));
                return true;

            default:
                Log.w( LOG_TAG, CLASS + ": onOptionsItemSelected: unknown item" );
                return super.onOptionsItemSelected(item);
        }
    }

    private Paint getLinePaint ( float width, int color ) {
        Paint linePaint = new Paint();
        linePaint.setAntiAlias( true );
        linePaint.setStrokeWidth( PixelUtils.dpToPix( width ) );
        linePaint.setColor( color );
        linePaint.setStyle( Paint.Style.STROKE );

        return linePaint;
    }

    private LineAndPointFormatter getLineAndPointFormatter( float width, int color ) {
        Paint linePaint = getLinePaint( width, color );
        LineAndPointFormatter lpf = new LineAndPointFormatter( null, null, null, null );
        lpf.setLinePaint( linePaint );
        return lpf;
    }

    private void initPlot() {
        mXHistorySeries = new SimpleXYSeries( "X" );
        mXHistorySeries.useImplicitXVals();

        mYHistorySeries = new SimpleXYSeries( "Y" );
        mYHistorySeries.useImplicitXVals();

        mZHistorySeries = new SimpleXYSeries( "Z" );
        mZHistorySeries.useImplicitXVals();

        mHistoryPlot.setRangeBoundaries( -20, 20, BoundaryMode.AUTO );
        mHistoryPlot.setDomainBoundaries( 0, HISTORY_SIZE, BoundaryMode.FIXED );

//        int x_plotColor = getResources().getColor( R.color.plot_x, null );
//        int y_plotColor = getResources().getColor( R.color.plot_y, null );
//        int z_plotColor = getResources().getColor( R.color.plot_z, null );
//        mHistoryPlot.addSeries( mXHistorySeries, new LineAndPointFormatter( x_plotColor, null, null, null ) );
//        mHistoryPlot.addSeries( mYHistorySeries, new LineAndPointFormatter( y_plotColor, null, null, null ) );
//        mHistoryPlot.addSeries( mZHistorySeries, new LineAndPointFormatter( z_plotColor, null, null, null ) );

        float lineWidth = mPrefs.getFloatPreference( "plotLineWidth", R.string.settings_default_plot_line_width );

        mHistoryPlot.addSeries( mXHistorySeries, getLineAndPointFormatter( lineWidth, Color.RED   ) );
        mHistoryPlot.addSeries( mYHistorySeries, getLineAndPointFormatter( lineWidth, Color.GREEN ) );
        mHistoryPlot.addSeries( mZHistorySeries, getLineAndPointFormatter( lineWidth, Color.BLUE  ) );

        //mHistoryPlot.setRangeValueFormat( new DecimalFormat( "#" ) );
        mHistoryPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("#"));


        redrawer = new Redrawer( mHistoryPlot, 40, false );
    }
    @Override
    public void onEvent( OnEventObject e ) {
        OnEventObject.Actions action = e.getAction();

        if( mDebugMessages ) {
            Log.d( LOG_TAG, CLASS + ": onEvent action=" + action.name() + " e=" + e.toString() );
        }

        updateElapsedTime();
        switch( action ) {
            case RECORDS:
                updatePlot();
                updateRecords();

                List<ChartRecord> records = mLoggerSingleton.getChartRecords();
                int size = records.size();
                if( size == 0 ) {
                    return;
                }
                ChartRecord record = records.get( size - 1 );
                Log.w( LOG_TAG, CLASS + ":x " + record.x +":y " + record.y +":z " + record.z );

                break;

            case LOG_FILENAME:
                updateFilename();
                break;

            case STATUS:
                updateStatus();
                break;

            case SENSOR_RATE:
                updateSensorRate();
                break;

            case LOG_SIZE:
                updateLogSize();
                break;

            case ALL:
                updateUI();
                break;

            default:
                Log.w( LOG_TAG, CLASS + ": onEvent: unknown action: " + action );
                updateUI();
                break;
        }
    }

    public void updateUI() {
        updateStatus();
        updateRecords();
        updateSensorRate();
        updateFilename();
        updateLogSize();
    }

    private void clearPlot() {
        while( mXHistorySeries.size() > 0 ) {
            mXHistorySeries.removeFirst();
        }

        while( mYHistorySeries.size() > 0 ) {
            mYHistorySeries.removeFirst();
        }

        while( mZHistorySeries.size() > 0 ) {
            mZHistorySeries.removeFirst();
        }
        mLoggerSingleton.clearChartRecords();
    }

    public void updateStatus() {
        LoggerSingleton.Status status = mLoggerSingleton.getStatus();
        if( mCurrentStatus == LoggerSingleton.Status.STOPPED ) {
            if( status == LoggerSingleton.Status.RUNNING ) {
                clearPlot();
            }

        }
        mCurrentStatus = status;
        mStatus.setText( mLoggerSingleton.getStatus().name() );
    }

    public void updateRecords() {
        mRecords.setText( String.valueOf( mLoggerSingleton.getRecords() ) );
    }

    public void updateSensorRate() {
        mDataRate.setText( mLoggerSingleton.getSensorRateStr() );
    }

    public void updateFilename() {
        mFilename.setText( mLoggerSingleton.getLogFilename() );
    }

    public void updateLogSize() {
        mLogSize.setText( String.format( getString( R.string.main_size_label), mLoggerSingleton.getLogSize() ) );
    }

    public void updateElapsedTime() {
        long seconds = mLoggerSingleton.getElapsedTime() / 1000;
        mElapsedTime.setText( String.format( getString( R.string.main_elapsed_label ), seconds ) );
    }

    public void redrawPlot() {
        List<ChartRecord> records = mLoggerSingleton.getChartRecords();
        int size = records.size();
        if( size == 0 ) {
            return;
        }

        int first = Math.max( 0, size - 1 - HISTORY_SIZE );
        for( int i = first; i < size; ++i ) {
            ChartRecord record = records.get( i );
            mXHistorySeries.addLast( record.timestamp, record.x );
            mYHistorySeries.addLast( record.timestamp, record.y );
            mZHistorySeries.addLast( record.timestamp, record.z );
        }

    }

    public void updatePlot() {

        // get rid the oldest sample in history:
        if( mXHistorySeries.size() > HISTORY_SIZE ) {
            mXHistorySeries.removeFirst();
            mYHistorySeries.removeFirst();
            mZHistorySeries.removeFirst();
        }

        List<ChartRecord> records = mLoggerSingleton.getChartRecords();
        int size = records.size();
        if( size == 0 ) {
            return;
        }

        ChartRecord record = records.get( size - 1 );
        if( mDebugMessages ) {
            Log.d( LOG_TAG, CLASS + ": updatePlot: size=" + size + " record=" + record.toString() );
        }

        // add the latest history sample:
        mXHistorySeries.addLast( record.timestamp, record.x );
        mYHistorySeries.addLast( record.timestamp, record.y );
        mZHistorySeries.addLast( record.timestamp, record.z );
    }


}
