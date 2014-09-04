package com.nth.ikiam;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.nth.ikiam.adapters.NotasListAdapter;
import com.nth.ikiam.db.*;
import com.nth.ikiam.utils.Utils;

import java.util.List;

/**
 * Created by luz on 03/09/14.
 */
public class NotepadFragment extends ListFragment {

    MapActivity activity;
    List<Nota> notasList;
    NotasListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MapActivity) getActivity();

        Utils.hideSoftKeyboard(activity);

        notasList = Nota.list(activity);

        adapter = new NotasListAdapter(getActivity(), notasList);
        setListAdapter(adapter);

        if (notasList.size() == 0) {
            Fragment fragment = new NotaCreateFrgment();
            Bundle args = new Bundle();
            args.putLong("nota", -1);
            Utils.openFragment(activity, fragment, getString(R.string.nota_create_title), args);
        }

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) {
                final int positionToRemove = position;

//        System.out.println("SELECTED:::::: " + selected);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.nota_create_dlg_delete_contenido)
                        .setTitle(R.string.nota_create_dlg_delete_title);

                // Add the buttons
                builder.setPositiveButton(R.string.global_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        Nota selected = notasList.get(positionToRemove);
                        Nota.delete(activity, selected);
                        notasList.remove(positionToRemove);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), getString(R.string.nota_create_delete), Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(R.string.global_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // Set other dialog properties


                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Nota selected = notasList.get(position);

        Bundle args = new Bundle();
        args.putLong("nota", selected.id);
        Fragment fragment = new NotaCreateFrgment();
        Utils.openFragment(activity, fragment, getString(R.string.nota_edit_title), args);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setTitle(R.string.notepad_title);
    }
}