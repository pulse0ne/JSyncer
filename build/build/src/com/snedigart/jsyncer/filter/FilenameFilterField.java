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

import com.snedigart.jsync.filter.FileNameSyncFilter;
import com.snedigart.jsync.filter.SyncFilter;
import com.snedigart.jsyncer.JSyncerConstants;

import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 * FilenameFilterField class
 * 
 * @author Tyler Snedigar
 * @version 1.0
 */
public class FilenameFilterField extends FilterField {

    protected final TextField field;

    /**
     * @param inclusive
     */
    public FilenameFilterField() {
        super();
        field = new TextField();
        field.setPromptText("example: **/*photo0*");
        field.setStyle(JSyncerConstants.FONT_SIZE_STYLE);
    }

    /**
     * @see com.snedigart.jsyncer.filter.FilterField#getFilter()
     */
    @Override
    public SyncFilter getFilter() {
        return new FileNameSyncFilter(field.getText());
    }

    /**
     * @see com.snedigart.jsyncer.filter.FilterField#getInputNode()
     */
    @Override
    public Node getInputNode() {
        return field;
    }

}
