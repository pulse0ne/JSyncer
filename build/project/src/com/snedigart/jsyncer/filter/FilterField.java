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

import com.snedigart.jsync.filter.SyncFilter;
import com.snedigart.jsyncer.JSyncerConstants;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

/**
 * FilterField class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public abstract class FilterField {

    protected final ChoiceBox<String> inclusiveChoiceBox;

    protected final Button closeButton;

    protected FilterField() {
        inclusiveChoiceBox = new ChoiceBox<>();
        inclusiveChoiceBox.getItems().addAll("Include", "Exclude");
        inclusiveChoiceBox.getSelectionModel().selectFirst();

        closeButton = new Button();
        Label iconLabel = new Label("\uf057");
        iconLabel.getStyleClass().add("fontawesome");
        closeButton.setGraphic(iconLabel);

        inclusiveChoiceBox.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
        closeButton.getStyleClass().add("fontawesome-button");
    }

    /**
     * Returns the filter associated with this field
     * 
     * @return SyncFilter
     */
    public abstract SyncFilter getFilter();

    /**
     * Returns the stretchable center input node
     * 
     * @return Node
     */
    public abstract Node getInputNode();

    /**
     * Returns the close button
     * 
     * @return Button
     */
    public Button getCloseButton() {
        return closeButton;
    }

    /**
     * Returns the choicebox
     * 
     * @return ChoiceBox
     */
    public ChoiceBox<String> getInclusiveChoiceBox() {
        return inclusiveChoiceBox;
    }

    /**
     * Returns whethor or not this is an inclusive filter
     * 
     * @return boolean
     */
    public boolean isInclusive() {
        return inclusiveChoiceBox.getSelectionModel().getSelectedItem().equals("Include");
    }

}
