module org.msv.fm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.netty.codec;
    requires org.msv.sm;

    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;


    opens org.msv.fm to javafx.fxml;
    opens org.msv.fm.fs to javafx.fxml;

    exports org.msv.fm;
    exports org.msv.fm.fs;
}