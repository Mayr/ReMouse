package com.mayurm.remouse;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class MouseFragment extends Fragment {

    private OnClickListener onClickListener;
    private OnTouchListener onTouchListener;

    public interface OnClickListener{
        public void onDismissClick();
    }

    public interface OnTouchListener{
        public boolean onViewTouch(MotionEvent event, float maxWidth, float maxHeight);
    }

    public static MouseFragment newInstance() {
        MouseFragment mainFragment = new MouseFragment();
        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        return mainFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnClickListener ) {
            onClickListener = (OnClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MouseFragment.MainFragmentListeners");
        }

        if (context instanceof OnTouchListener ) {
            onTouchListener = (OnTouchListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MouseFragment.OnTouchListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mouse, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here

        ImageView dismiss = view.findViewById(R.id.closeConnection);
        final View touchArea = view.findViewById(R.id.touchArea);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDismissClick();
            }
        });
        touchArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchListener.onViewTouch(event, touchArea.getWidth(), touchArea.getHeight());
        }
        });

    }
}
