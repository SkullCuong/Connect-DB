package com.example.connect_data_base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.PersonViewHolder> {
    private List<Person> people;
    private LayoutInflater inflater;
    private Context context;

    public ListAdapter(List<Person> people, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.people = people;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.activity_list, null);
        return new ListAdapter.PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        holder.bindData(people.get(position));
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {
        ImageView personImage;
        TextView name, email, address, dob;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            personImage = itemView.findViewById(R.id.profileImage);
            name = itemView.findViewById(R.id.personName);
            email = itemView.findViewById(R.id.personEmail);
            address = itemView.findViewById(R.id.personAddress);
            dob = itemView.findViewById(R.id.personDob);

        }

        void bindData(final Person person) {
            personImage.setImageBitmap(getUserImage(person.getImage()));
            name.setText(person.getName());
            dob.setText(person.getDob());
            email.setText(person.getEmail());
            address.setText(person.getAddress());
        }
    }

    private Bitmap getUserImage(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }
}