/*
 * Copyright 2016 Ali Salah Alddin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package edu.uofk.eeese.eeese.projects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uofk.eeese.eeese.R;
import edu.uofk.eeese.eeese.R2;
import edu.uofk.eeese.eeese.data.Project;

class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    @NonNull
    private List<Project> mProjects;
    @NonNull
    private Context mContext;

    ProjectsAdapter(Context context) {
        this(context, Collections.<Project>emptyList());
    }

    ProjectsAdapter(@NonNull Context context, @NonNull List<Project> projects) {
        mContext = context;
        mProjects = projects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Project project = mProjects.get(position);

        holder.title
                .setText(project.getName());
        holder.desc
                .setText(project.getDesc());
        Picasso.with(mContext)
                .load(project.getImageUri())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public void changeProjects(@Nullable List<Project> projects) {
        if (projects != null) {
            mProjects = projects;
        } else {
            mProjects = Collections.emptyList();
        }

        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.project_name)
        public TextView title;
        @BindView(R2.id.project_desc)
        public TextView desc;
        @BindView(R2.id.project_image)
        public ImageView image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
