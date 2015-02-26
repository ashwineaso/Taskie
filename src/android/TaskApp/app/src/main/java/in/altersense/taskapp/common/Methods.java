package in.altersense.taskapp.common;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.altersense.taskapp.DashboardActivity;
import in.altersense.taskapp.components.AltEngine;
import in.altersense.taskapp.models.Collaborator;

/**
 * Created by mahesmohan on 2/22/15.
 */
public class Methods {

    public static final String CLASS_TAG = "Methods ";

    /**
     * Returns a duplicate free list.
     * @param collaboratorList A list with duplicates
     * @return A list without duplicates.
     */
    public static List<Collaborator> removeDuplicates(List<Collaborator> collaboratorList) {
        Set<Collaborator> collaboratorSet = new HashSet<>();
        for (Collaborator collaborator:collaboratorList) {
            if(!collaboratorSet.add(collaborator)) {
               collaboratorSet.remove(collaborator);
            }
        }
        List<Collaborator> distinctList = new ArrayList<>();
        distinctList.addAll(collaboratorSet);
        return distinctList;
    }

}
