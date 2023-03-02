package com.example.macseh.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.macseh.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    PieChartView pieChartView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*-*-*-*-*-*- Elementos del fragment -*-*-*-*-*-*/
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });

        pieChartView = root.findViewById(R.id.chart);

        List pieData = new ArrayList<>();
        pieData.add(new SliceValue(15, Color.parseColor("#29A19C")).setLabel("D1: $10"));
        pieData.add(new SliceValue(25, Color.parseColor("#258E8A")).setLabel("D2: $4"));
        pieData.add(new SliceValue(10, Color.parseColor("#1F7A77")).setLabel("D3: $18"));
        pieData.add(new SliceValue(60, Color.parseColor("#1A6563")).setLabel("D4: $28"));

        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true).setCenterText1("Consumo Electrico").setCenterText1FontSize(21).setCenterText1Color(Color.parseColor("#f5f5f5"));
        pieChartView.setPieChartData(pieChartData);

        return root;
    }
}
