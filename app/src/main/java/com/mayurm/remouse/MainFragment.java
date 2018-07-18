package com.mayurm.remouse;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class MainFragment extends Fragment {

    private MainFragmentListeners listener;
    private EditText ip;
    private Button connect;

    public interface MainFragmentListeners {
        public void onConnectClick(String ip);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            if(ip.getText().toString().matches(Patterns.IP_ADDRESS.toString())){
                connect.setEnabled(true);
                connect.setBackgroundColor(Color.rgb(88,130,221));
            }else{
                connect.setEnabled(false);
                connect.setBackgroundColor(Color.argb(44,0,0,0));
            }
        }
    };

    public static MainFragment newInstance() {
        MainFragment mainFragment = new MainFragment();
        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragmentListeners) {
            listener = (MainFragmentListeners) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MainFragment.MainFragmentListeners");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        connect = view.findViewById(R.id.connectButton);
        ip = view.findViewById(R.id.ipEnter);

        connect.setEnabled(false);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConnectClick(ip.getText().toString());
            }
        });

        ip.addTextChangedListener(mTextWatcher);

        InputFilter[] filters = new InputFilter[1];

        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt
                            .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        Log.d("a","ipfull");
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };

        ip.setFilters(filters);



    }
}
