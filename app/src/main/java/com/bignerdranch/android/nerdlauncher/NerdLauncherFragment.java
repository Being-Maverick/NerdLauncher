package com.bignerdranch.android.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private static final String TAG = "NerdLauncherFragment";

    public static NerdLauncherFragment newInstance(){
        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setUpAdapter();

        return v;
    }

    private void setUpAdapter(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activites = packageManager.queryIntentActivities(intent,0);
        Collections.sort(activites, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(pm).toString(),o2.loadLabel(pm).toString());
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(activites));
        Log.i(TAG, "Found " + activites.size() + " activities");
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ResolveInfo mResolveInfo;
        private TextView mTextView;

        public ActivityHolder(View view){
            super(view);
            mTextView = (TextView) view;
            mTextView.setOnClickListener(this);
        }

        public void bindInfo(ResolveInfo resolveInfo){
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String label = mResolveInfo.loadLabel(pm).toString();
            mTextView.setText(label);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent i = new Intent(Intent.ACTION_MAIN).setClassName(activityInfo.applicationInfo.packageName,activityInfo.name).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder>{
        private List<ResolveInfo> mResolveInfoList;

        public ActivityAdapter(List<ResolveInfo> list){
            mResolveInfoList = list;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1,viewGroup,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ActivityHolder activityHolder, int i) {
            ResolveInfo resolveInfo = mResolveInfoList.get(i);
            activityHolder.bindInfo(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return mResolveInfoList.size();
        }
    }
}
