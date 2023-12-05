package pl.pollub.android.myapplication.ui.medications;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

public class MultiChoiceSpinner extends MaterialSpinner {

    private List<String> items;
    private List<Boolean> selected;

    public MultiChoiceSpinner(Context context) {
        super(context);
        init();
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiChoiceSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.items = new ArrayList<>();
        this.selected = new ArrayList<>();

        // Ustaw listener dla zdarzeń wyboru elementów
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                selected.set(position, !selected.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Niepotrzebna implementacja
            }
        });
    }

    public void setItems(List<String> items) {
        this.items = items;
        this.selected.clear();
        for (int i = 0; i < items.size(); i++) {
            selected.add(false);
        }

        // Ustaw adapter do wyboru
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        setAdapter(adapter);
    }

    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            if (selected.get(i)) {
                selectedItems.add(items.get(i));
            }
        }
        return selectedItems;
    }
}
