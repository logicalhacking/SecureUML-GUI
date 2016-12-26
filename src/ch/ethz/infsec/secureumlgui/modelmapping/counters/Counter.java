package ch.ethz.infsec.secureumlgui.modelmapping.counters;

public class Counter {

    protected String sinPlur(int number, String singular, String plural) {
        if (number == 1) {
            return singular;
        } else {
            return plural;
        }
    }

}
