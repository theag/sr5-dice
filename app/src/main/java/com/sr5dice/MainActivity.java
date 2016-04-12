package com.sr5dice;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements EdgeDialog.OnSaveListener, CustomButtonDialog.OnSaveListener {

    private static final String DIALOG_SET_EDGE = "set edge dialog";
    private static final String DIALOG_SET_CUSTOM_BUTTON = "set custom button dialog";
    private DiceRoll diceRoll = null;
    private DiceView diceView;
    private int edge = 0;
    private int custom = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(diceRoll == null) {
            diceRoll = new DiceRoll();
        } else {
            if(diceRoll.isLimitPushed()) {
                findViewById(R.id.btn_push).setEnabled(false);
            }
            if(diceRoll.isSecondChanced()) {
                findViewById(R.id.btn_second).setEnabled(false);
            }
        }
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        if(edge <= 0) {
            edge = sharedPref.getInt(EdgeDialog.ARG_EDGE, 5);
        }
        if(custom < 0) {
            custom = sharedPref.getInt(CustomButtonDialog.ARG_VALUE, 20);
        }
        diceView = (DiceView)findViewById(R.id.diceView);
        diceView.setDiceRoll(diceRoll);
        setTotalText();
        setCustomButtonText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_edge) {
            //TODO: warn this will clear roll if limit has been pushed
            EdgeDialog frag = new EdgeDialog();
            Bundle args = new Bundle();
            args.putInt(EdgeDialog.ARG_EDGE, edge);
            frag.setArguments(args);
            frag.show(getSupportFragmentManager(), DIALOG_SET_EDGE);
            return true;
        } else if(item.getItemId() == R.id.action_custom_roll) {
            CustomButtonDialog frag = new CustomButtonDialog();
            Bundle args = new Bundle();
            args.putInt(CustomButtonDialog.ARG_VALUE, custom);
            frag.setArguments(args);
            frag.show(getSupportFragmentManager(), DIALOG_SET_CUSTOM_BUTTON);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDialogClick(String tag, Bundle data) {
        switch(tag) {
            case DIALOG_SET_EDGE:
                edge = data.getInt(EdgeDialog.ARG_EDGE);
                if(diceRoll.isLimitPushed()) {
                    diceRoll = new DiceRoll();
                    diceView.setDiceRoll(diceRoll);
                    findViewById(R.id.btn_push).setEnabled(true);
                    findViewById(R.id.btn_second).setEnabled(true);
                    findViewById(R.id.btn_one).setEnabled(true);
                    findViewById(R.id.btn_five).setEnabled(true);
                    findViewById(R.id.btn_ten).setEnabled(true);
                    setTotalText();
                }
                break;
            case DIALOG_SET_CUSTOM_BUTTON:
                custom = data.getInt(CustomButtonDialog.ARG_VALUE);
                setCustomButtonText();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(EdgeDialog.ARG_EDGE, edge);
        editor.putInt(CustomButtonDialog.ARG_VALUE, custom);
        editor.commit();
    }

    public void doClick(View view) {
        switch(view.getId()) {
            case R.id.btn_one:
                diceRoll.add(1);
                diceView.update();
                break;
            case R.id.btn_five:
                diceRoll.add(5);
                diceView.update();
                break;
            case R.id.btn_ten:
                diceRoll.add(10);
                diceView.update();
                break;
            case R.id.btn_custom:
                diceRoll.add(custom);
                diceView.update();
                break;
            case R.id.btn_clear:
                diceRoll = new DiceRoll();
                diceView.setDiceRoll(diceRoll);
                findViewById(R.id.btn_push).setEnabled(true);
                findViewById(R.id.btn_second).setEnabled(true);
                findViewById(R.id.btn_one).setEnabled(true);
                findViewById(R.id.btn_five).setEnabled(true);
                findViewById(R.id.btn_ten).setEnabled(true);
                break;
            case R.id.btn_push:
                boolean pushFirst = diceRoll.doLimitPush(edge);
                diceView.update();
                findViewById(R.id.btn_push).setEnabled(false);
                findViewById(R.id.btn_second).setEnabled(false);
                findViewById(R.id.btn_one).setEnabled(pushFirst);
                findViewById(R.id.btn_five).setEnabled(pushFirst);
                findViewById(R.id.btn_ten).setEnabled(pushFirst);
                break;
            case R.id.btn_second:
                diceRoll.doSecondChance();
                diceView.update();
                findViewById(R.id.btn_push).setEnabled(false);
                findViewById(R.id.btn_second).setEnabled(false);
                findViewById(R.id.btn_one).setEnabled(false);
                findViewById(R.id.btn_five).setEnabled(false);
                findViewById(R.id.btn_ten).setEnabled(false);
                break;
        }
        setTotalText();
    }

    private void setTotalText() {
        TextView tv = (TextView)findViewById(R.id.text_total);
        String str = getString(R.string.total_hits) + " " + diceRoll.getTotalHits() +"/";
        if(diceRoll.isLimitPushed()) {
            str += (diceRoll.getTotalRolled()-edge) +"(+" +edge +" Edge)";
        } else {
            str += ""+diceRoll.getTotalRolled();
        }
        tv.setText(str);
    }

    private void setCustomButtonText() {
        Button btn = (Button)findViewById(R.id.btn_custom);
        btn.setText(custom +"*");
    }
}
