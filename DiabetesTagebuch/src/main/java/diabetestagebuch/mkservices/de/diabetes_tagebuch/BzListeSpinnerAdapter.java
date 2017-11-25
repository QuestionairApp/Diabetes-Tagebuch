package diabetestagebuch.mkservices.de.diabetes_tagebuch;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by manfr on 19.09.2017.
 */

public class BzListeSpinnerAdapter extends ArrayAdapter<bzData> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<bzData> items;
    private final int mResource;
    public BzListeSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);

        mContext=context;
        mInflater=LayoutInflater.from(context);
        mResource=resource;
        items=objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent){
        final View view=mInflater.inflate(mResource, parent, false);
        TextView datumTv=(TextView)view.findViewById(R.id.tv_item_datum);
        TextView zeitTv=(TextView)view.findViewById(R.id.tv_item_zeit);

        bzData bz=items.get(position);
        datumTv.setText(bz.getDatum());
        zeitTv.setText(bz.getZeit());

        return view;
    }
}
