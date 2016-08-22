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

import java.time.LocalDate;

import com.snedigart.jsync.filter.LastModifiedSyncFilter;
import com.snedigart.jsync.filter.SyncFilter;
import com.snedigart.jsyncer.JSyncerConstants;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * LastModifiedFilterField class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public class LastModifiedFilterField extends FilterField {

    private static final long SECONDS = 1000L;

    private static final long MINUTES = SECONDS * 60L;

    private static final long HOURS = MINUTES * 60L;

    private final DatePicker fromPicker;

    private final DatePicker toPicker;

    private final TimeSpinner fromSpinner;

    private final TimeSpinner toSpinner;

    private final HBox inputField;

    public LastModifiedFilterField() {
        super();

        fromPicker = new DatePicker();
        toPicker = new DatePicker();

        fromSpinner = new TimeSpinner();
        toSpinner = new TimeSpinner();

        fromPicker.setValue(LocalDate.now().minusDays(1));
        toPicker.setValue(LocalDate.now());

        configurePickers();

        inputField = new HBox();

        fromPicker.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        toPicker.setStyle(JSyncerConstants.FONT_SIZE_STYLE);

        final Label fromLabel = new Label("From");
        fromLabel.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        inputField.getChildren().addAll(fromLabel, fromPicker, fromSpinner);

        inputField.getChildren().add(new Separator(Orientation.VERTICAL));

        final Label toLabel = new Label("To");
        toLabel.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        inputField.getChildren().addAll(toLabel, toPicker, toSpinner);

        inputField.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    public SyncFilter getFilter() {
        long fromTime = getTime(fromPicker, fromSpinner);
        long toTime = getTime(toPicker, toSpinner);
        return new LastModifiedSyncFilter(Math.min(fromTime, toTime), Math.max(fromTime, toTime));
    }

    /**
     * @see com.snedigart.jsyncer.filter.FilterField#getInputNode()
     */
    @Override
    public Node getInputNode() {
        return inputField;
    }

    private long getTime(DatePicker picker, TimeSpinner spinner) {
        long time = 0L;
        time = picker.getValue().toEpochDay();
        time += (spinner.getSecond() * SECONDS) + (spinner.getMinute() * MINUTES) + (spinner.getHour() * HOURS);
        return time;
    }

    private void configurePickers() {
        fromPicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isAfter(toPicker.getValue())) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        });

        toPicker.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isBefore(fromPicker.getValue())) {
                            this.setDisable(true);
                        }
                    }
                };
            }
        });

        fromPicker.setMaxWidth(100.0);
        toPicker.setMaxWidth(100.0);
    }
}
