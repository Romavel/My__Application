package pl.pollub.android.myapplication.ui.pomiary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import pl.pollub.android.myapplication.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private InrMeasurementAdapter mAdapter;
    private Drawable icon;
    private final ColorDrawable background;

    public SwipeToDeleteCallback(Context context, InrMeasurementAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete); // Zastąp ikonę odpowiednią do Twojego projektu
        background = new ColorDrawable(Color.RED); // Zastąp kolorem odpowiednim do Twojego projektu
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // Nie implementujemy przeciągania
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position);
    }



    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; // odstęp od krawędzi do tła

        if (dX > 0) { // Swipe w prawo
            background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
            icon.setBounds(itemView.getLeft() + backgroundCornerOffset, itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2,
                    itemView.getLeft() + backgroundCornerOffset + icon.getIntrinsicWidth(), itemView.getBottom() - (itemView.getHeight() - icon.getIntrinsicHeight()) / 2);
        } else if (dX < 0) { // Swipe w lewo
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            icon.setBounds(itemView.getRight() - backgroundCornerOffset - icon.getIntrinsicWidth(), itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2,
                    itemView.getRight() - backgroundCornerOffset, itemView.getBottom() - (itemView.getHeight() - icon.getIntrinsicHeight()) / 2);
        } else {
            background.setBounds(0, 0, 0, 0);
            icon.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
