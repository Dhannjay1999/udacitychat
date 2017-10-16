package com.example.lenovo.xyz;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import static android.R.id.message;
import static com.example.lenovo.xyz.R.id.myimage;
import static com.example.lenovo.xyz.R.id.mytext;


/**
 * Created by Lenovo on 8/14/2017.
 */

public class artistlist extends ArrayAdapter<Artist> {
    private Activity context;
    private List<Artist> newlist;
    public artistlist(Activity context,List newlist){
super(context,R.layout.list_view,newlist);
        this.context=context;
        this.newlist=newlist;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

LayoutInflater inflater=context.getLayoutInflater();
        View listviewitem=inflater.inflate(R.layout.list_view,null,true);
        TextView pastext=(TextView)listviewitem.findViewById(R.id.pasttext);
        TextView frnd=(TextView)listviewitem.findViewById(R.id.username);
        ImageView as=(ImageView)listviewitem.findViewById(R.id.myimage);
Artist artist=newlist.get(position);


        boolean isPhoto = artist.getImageuri() != null;
        if (isPhoto) {

            as.setVisibility(View.VISIBLE);
            Glide.with(as.getContext())
                    .load(artist.getImageuri())
                    .into(as);
        } else {
            pastext.setVisibility(View.VISIBLE);
            as.setVisibility(View.GONE);
            pastext.setText(artist.getName());
        }

pastext.setText(artist.getName());

        frnd.setText(artist.getGenere());


        return listviewitem;
    }
}
