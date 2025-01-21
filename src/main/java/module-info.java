module org.outfoxedfinal {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.outfoxedfinal to javafx.fxml;
    exports org.outfoxedfinal;
    opens org.outfoxedfinal.logic to javafx.fxml;
    exports org.outfoxedfinal.logic to javafx.fxml;
}