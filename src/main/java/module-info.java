module com {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires javafx.base;

    requires lucene.core;
    requires lucene.queryparser;
    requires lucene.queries;
    requires lucene.highlighter;
    requires lucene.suggest;

    opens com.view;
    opens com.controller;
    opens com.model;

    exports com.view;
    exports com.controller;
    exports com.model;
}