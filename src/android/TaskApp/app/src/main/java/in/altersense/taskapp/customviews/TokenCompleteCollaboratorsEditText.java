package in.altersense.taskapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import in.altersense.taskapp.R;
import in.altersense.taskapp.database.UserDbHelper;
import in.altersense.taskapp.models.User;

/**
 * Created by mahesmohan on 2/14/15.
 */
public class TokenCompleteCollaboratorsEditText extends TokenCompleteTextView {

    public TokenCompleteCollaboratorsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Object o) {
        User user = (User) o;
        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout) l.inflate(
                R.layout.collaborator_token,
                (ViewGroup) TokenCompleteCollaboratorsEditText.this.getParent(),
                false
                );
        ((TextView)view.findViewById(R.id.name)).setText(user.getName());
        return view;
    }

    @Override
    protected Object defaultObject(String s) {
        User newUser = new User(s,s);
        return newUser;
    }
}
