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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.InputEvent;
import javafx.util.StringConverter;

/**
 * TimeSpinner class
 * 
 * @author Tyler Snedigar (inspired by James_D:
 *         http://stackoverflow.com/a/32617768/4580653)
 * @version 1.0
 */
public class TimeSpinner extends Spinner<LocalTime> {

    private final ObjectProperty<TimeComponent> component = new SimpleObjectProperty<>(TimeComponent.HOURS);

    public TimeSpinner() {
        this(LocalTime.now());
    }

    public TimeSpinner(LocalTime time) {
        setEditable(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        StringConverter<LocalTime> converter = new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                return formatter.format(time);
            }

            @Override
            public LocalTime fromString(String string) {
                String[] tokens = string.split(":");
                int h = getIntField(tokens, 0);
                int m = getIntField(tokens, 1);
                int s = getIntField(tokens, 2);
                int totalSeconds = (h * 60 + m) * 60 + s;
                return LocalTime.of((totalSeconds / 3600) % 24, (totalSeconds / 60) % 60, s % 60);
            }

            private int getIntField(String[] tokens, int ix) {
                if (tokens.length <= ix || tokens[ix].isEmpty()) {
                    return 0;
                }
                return Integer.parseInt(tokens[ix]);
            }
        };

        TextFormatter<LocalTime> textFormatter = new TextFormatter<LocalTime>(converter, LocalTime.now(), c -> {
            String newText = c.getControlNewText();
            if (newText.matches("[0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}")) {
                return c;
            }
            return null;
        });

        SpinnerValueFactory<LocalTime> valueFactory = new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(converter);
                setValue(time);
            }

            @Override
            public void decrement(int steps) {
                setValue(component.get().decrement(getValue(), steps));
                component.get().select(TimeSpinner.this);
            }

            @Override
            public void increment(int steps) {
                setValue(component.get().increment(getValue(), steps));
                component.get().select(TimeSpinner.this);
            }
        };

        this.setValueFactory(valueFactory);
        this.getEditor().setTextFormatter(textFormatter);
        this.getEditor().addEventHandler(InputEvent.ANY, e -> {
            int caretPos = this.getEditor().getCaretPosition();
            int hrIndex = this.getEditor().getText().indexOf(':');
            int minIndex = this.getEditor().getText().indexOf(':', hrIndex + 1);
            if (caretPos <= hrIndex) {
                component.set(TimeComponent.HOURS);
            } else if (caretPos <= minIndex) {
                component.set(TimeComponent.MINUTES);
            } else {
                component.set(TimeComponent.SECONDS);
            }
        });

        component.addListener((obs, ov, nv) -> nv.select(this));
    }

    /**
     * Private convenience class for the 3 time components (hour, minute,
     * second)
     * 
     * @author Tyler Snedigar (inspired by James_D:
     *         http://stackoverflow.com/a/32617768/4580653)
     * @version 1.0
     */
    private static abstract class TimeComponent {

        abstract LocalTime increment(LocalTime time, int steps);

        abstract void select(TimeSpinner spinner);

        LocalTime decrement(LocalTime time, int steps) {
            return increment(time, -steps);
        }

        static final TimeComponent HOURS = new TimeComponent() {
            @Override
            public LocalTime increment(LocalTime time, int steps) {
                return time.plusHours(steps);
            }

            @Override
            public void select(TimeSpinner spinner) {
                spinner.getEditor().selectRange(0, spinner.getEditor().getText().indexOf(':'));
            }
        };

        static final TimeComponent MINUTES = new TimeComponent() {
            @Override
            public LocalTime increment(LocalTime time, int steps) {
                return time.plusMinutes(steps);
            }

            @Override
            public void select(TimeSpinner spinner) {
                int hrIndex = spinner.getEditor().getText().indexOf(':');
                int minIndex = spinner.getEditor().getText().indexOf(':', hrIndex + 1);
                spinner.getEditor().selectRange(hrIndex + 1, minIndex);
            }

        };

        static final TimeComponent SECONDS = new TimeComponent() {
            @Override
            public LocalTime increment(LocalTime time, int steps) {
                return time.plusSeconds(steps);
            }

            @Override
            public void select(TimeSpinner spinner) {
                int index = spinner.getEditor().getText().lastIndexOf(':');
                spinner.getEditor().selectRange(index + 1, spinner.getEditor().getText().length());
            }
        };
    }
}
