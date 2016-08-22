/*
 * Copyright 2016 Tyler Snedigar.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 3 for more details.
 */
package com.snedigart.jsyncer.filter;

import com.snedigart.jsyncer.JSyncerConstants;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;

/**
 * TimeSpinner class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public class TimeSpinner extends HBox {

    private final Spinner<Integer> hourSpinner;

    private final Spinner<Integer> minuteSpinner;

    private final Spinner<Integer> secondSpinner;

    public TimeSpinner() {
        hourSpinner = new Spinner<>();
        minuteSpinner = new Spinner<>();
        secondSpinner = new Spinner<>();

        configureSpinners();

        this.getChildren().addAll(hourSpinner, createLabel(), minuteSpinner, createLabel(), secondSpinner);
        this.setAlignment(Pos.CENTER_LEFT);
    }

    private void configureSpinners() {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24));
        hourSpinner.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        hourSpinner.setMaxWidth(JSyncerConstants.SPINNER_WIDTH);

        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60));
        minuteSpinner.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        minuteSpinner.setMaxWidth(JSyncerConstants.SPINNER_WIDTH);

        secondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60));
        secondSpinner.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        secondSpinner.setMaxWidth(JSyncerConstants.SPINNER_WIDTH);
    }

    public int getHour() {
        return hourSpinner.getValue();
    }

    public int getMinute() {
        return minuteSpinner.getValue();
    }

    public int getSecond() {
        return secondSpinner.getValue();
    }

    private Label createLabel() {
        Label label = new Label(" : ");
        label.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        return label;
    }

}
