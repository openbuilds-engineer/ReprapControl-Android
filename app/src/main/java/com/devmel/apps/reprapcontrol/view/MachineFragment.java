package com.devmel.apps.reprapcontrol.view;

import com.devmel.apps.reprapcontrol.MainActivity;
import com.devmel.apps.reprapcontrol.PortSelect;
import com.devmel.apps.reprapcontrol.R;
import com.devmel.apps.reprapcontrol.datas.SharedData;
import com.devmel.storage.android.UserPrefs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MachineFragment extends Fragment {
	MainActivity mActivity = null;	//Controller
	SharedData sharedData = new SharedData();

	private Button connectBt;
	private Button selectPortBt;
	private EditText baudRateValue;
	private TextView infosText;
	private Button swresetBt;
	private LinearLayout lbLayout;
	private CompoundButton vtgSwitch;
	private CompoundButton resetSwitch;

	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
		if(activity != null && activity instanceof MainActivity) {
			sharedData = ((MainActivity) activity).sharedData;
			mActivity = (MainActivity) activity;
		}
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_machine, container, false);
		connectBt = (Button) rootView.findViewById(R.id.connectBt);
		selectPortBt = (Button) rootView.findViewById(R.id.selectPortBt);
		baudRateValue = (EditText) rootView.findViewById(R.id.baudRateValue);
		infosText = (TextView) rootView.findViewById(R.id.infosText);
		swresetBt = (Button) rootView.findViewById(R.id.swresetBt);

		baudRateValue.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				sharedData.setBaudRate(getBaudRateView());
			}
		});

		selectPortBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(mActivity != null)
					mActivity.portSelect();
			}
		});
		connectBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(mActivity != null)
					mActivity.connectClick();
			}
		});
		swresetBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(mActivity != null){
					mActivity.gcodeControl.resetBuffer();
					if(!mActivity.gcodeControl.command("M999")){
						mActivity.machineBusyMsg();
					}
				}
			}
		});

		return rootView;
	}
	
	@Override
	public void onResume(){
	    super.onResume();
		if (selectPortBt != null)
			selectPortBt.setText((sharedData.getPortName() != null) ? sharedData.getPortName() : getString(R.string.port_select));
		setParametersView();
		refresh();
	}

	public void refresh(){
		setInfos(sharedData.firmware);
		testConnectivity();
	}


	private void setInfos(final String infos){
		if(infos != null)
			infosText.setText(infos);
		else
			infosText.setText("");
	}
	private void setConnected(boolean connect){
		if(connect==true){
			baudRateValue.setEnabled(false);
		}else{
			baudRateValue.setEnabled(true);
		}
		if(swresetBt != null)
			swresetBt.setEnabled(connect);
	}
	private void setConnectButton(int state){
		if(state == 2){
			connectBt.setEnabled(false);
			connectBt.setText(getString(R.string.connecting));
		}else if(state == 1){
			connectBt.setEnabled(true);
			connectBt.setText(getString(R.string.disconnect));
		}else{
			connectBt.setEnabled(true);
			connectBt.setText(getString(R.string.connect));
		}
	}

	private int getBaudRateView(){
		int baudRate = sharedData.getBaudrate();
		try{baudRate = Integer.valueOf(baudRateValue.getText().toString());}catch(Exception e){}
		return baudRate;
	}

	private void setParametersView(){
		this.setBaudRateView(sharedData.getBaudrate());
	}

	private void setBaudRateView(int baudrate){
		if(baudrate<0){
			baudrate = 9600;
		}
		if(baudRateValue != null)
			baudRateValue.setText(""+baudrate);
	}

	private void testConnectivity(){
		if (mActivity != null) {
			setConnected(mActivity.sharedData.connect);
			int btState = 0;
			if(mActivity.sharedData.connect)
				btState = 2;
			if(mActivity.gcodeControl.isConnected())
				btState = 1;
			setConnectButton(btState);
		} else {
			setConnected(false);
		}
	}

}
