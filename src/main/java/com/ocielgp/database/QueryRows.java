package com.ocielgp.database;

import javafx.collections.ObservableList;

public class QueryRows {
    private final ObservableList data;
    private final int rows;

    public QueryRows(ObservableList data, int rows) {
        this.data = data;
        this.rows = rows;
    }

    public ObservableList getData() {
        return data;
    }

    public int getRows() {
        return rows;
    }
}
