package com.example.filrouge;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.filrouge.Interfaces.Menuable;

public class MenuFragment extends Fragment {

    private Menuable listener;

    private final int[] iconIds = {
            R.id.menu_icon_0, R.id.menu_icon_1, R.id.menu_icon_2,
            R.id.menu_icon_3, R.id.menu_icon_4, R.id.menu_icon_5,
            R.id.menu_icon_6
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Menuable) {
            listener = (Menuable) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menufragment, container, false);

        for (int i = 0; i < iconIds.length; i++) {
            final int index = i;
            ImageView icon = view.findViewById(iconIds[i]);
            if (icon != null) {
                icon.setSelected(index == 0);
                icon.setOnClickListener(v -> {
                    updateSelection(index);
                    if (listener != null) {
                        listener.onMenuClick(index);
                    }
                });
            }
        }

        return view;
    }

    public void updateSelection(int position) {
        View root = getView();
        if (root == null) return;
        for (int i = 0; i < iconIds.length; i++) {
            ImageView icon = root.findViewById(iconIds[i]);
            if (icon != null) {
                icon.setSelected(i == position);
            }
        }
    }
}