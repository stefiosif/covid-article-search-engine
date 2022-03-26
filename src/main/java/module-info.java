module com {
    requires javafx.baseEmpty;
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires org.controlsfx.controls;

    requires lucene.core;
    requires lucene.queryparser;
    requires lucene.queries;
    requires lucene.highlighter;
    requires lucene.suggest;

    opens com.view to javafx.fxml;
    exports com.view;
    exports com.model;
    exports com.controller;
}