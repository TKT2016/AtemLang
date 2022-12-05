module AtemIDE {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires com.google.common;
    requires org.fxmisc.flowless;
    requires java.logging;
    requires java.desktop;
    requires jsr305;
    requires reactfx;

    requires com.kodedu.terminalfx;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens AtemIDE to javafx.fxml;
    exports AtemIDE;

    opens AtemIDE.controller to javafx.fxml;
    exports AtemIDE.controller;
    exports AtemIDE.terminalFX;

    exports  AtemIDE.configs;
}