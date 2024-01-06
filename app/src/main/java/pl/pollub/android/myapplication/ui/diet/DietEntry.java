package pl.pollub.android.myapplication.ui.diet;

import java.io.Serializable;
import java.util.Map;

public class DietEntry implements Serializable {
    private String name;
    private int amount;

    // Pusty konstruktor
    public DietEntry() {
        // Pusty konstruktor potrzebny do korzystania z Firestore
    }
    public DietEntry(String name) {
        this.name = name;
    }

    // Konstruktor do ustawiania wszystkich pól
    public DietEntry(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    // Getter i setter dla name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter i setter dla amount
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
