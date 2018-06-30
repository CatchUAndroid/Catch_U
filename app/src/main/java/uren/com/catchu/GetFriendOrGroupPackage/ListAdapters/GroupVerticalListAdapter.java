package uren.com.catchu.GetFriendOrGroupPackage.ListAdapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseDeleteGroupAdapter;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetGroups;
import uren.com.catchu.General_Utils.DialogBox;
import uren.com.catchu.General_Utils.LazyList.ImageLoader;
import uren.com.catchu.GetFriendOrGroupPackage.GroupPackage.DisplayGroupDetail;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedGroupList;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.deleteGroup;
import static uren.com.catchu.Constants.StringConstants.displayRounded;
import static uren.com.catchu.Constants.StringConstants.exitGroup;
import static uren.com.catchu.Constants.StringConstants.groupConstant;
import static uren.com.catchu.Constants.StringConstants.groupsCacheDirectory;

public class GroupVerticalListAdapter extends RecyclerView.Adapter<GroupVerticalListAdapter.MyViewHolder> implements Filterable {

    private ArrayList<Group> groupList = new ArrayList<>();
    private ArrayList<Group> groupListCopy = new ArrayList<>();
    public ImageLoader imageLoader;
    View view;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;
    SelectedGroupList selectedGroupList;
    Context context;

    private String searchText;

    public static final int groupDetailItem = 0;
    public static final int groupDeleteItem = 1;

    Activity activity;

    public GroupVerticalListAdapter(Context context, ArrayList<Group> groupListMap, String searchText) {
        layoutInflater = LayoutInflater.from(context);
        groupList = groupListMap;
        groupListCopy.addAll(groupList);
        Collections.sort(groupList, new CustomComparator());
        this.context = context;
        this.searchText = searchText;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), groupsCacheDirectory);
        selectedGroupList = SelectedGroupList.getInstance();

        if(searchText != null) {
            if (!searchText.isEmpty())
                getFilter().filter(searchText);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    groupListCopy = groupList;
                } else {
                    ArrayList<Group> tempFilteredList = new ArrayList<>();

                    for (Group group : groupList) {

                        if (group.getGroupName().toLowerCase().contains(searchString.toLowerCase())) {
                            tempFilteredList.add(group);
                        }
                    }
                    groupListCopy = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = groupListCopy;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                groupListCopy = (ArrayList<Group>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CustomComparator implements Comparator<Group> {
        @Override
        public int compare(Group o1, Group o2) {
            return o1.getGroupName().compareToIgnoreCase(o2.getGroupName());
        }
    }

    @Override
    public GroupVerticalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.special_vertical_list_item, parent, false);
        GroupVerticalListAdapter.MyViewHolder holder = new GroupVerticalListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView specialNameTextView;
        TextView itemIdTextView;
        TextView adminIDTextView;
        CheckBox selectCheckBox;
        Group selectedGroup;
        ImageView specialProfileImgView;
        int position = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            specialNameTextView = (TextView) view.findViewById(R.id.specialNameTextView);
            selectCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);
            itemIdTextView = (TextView) view.findViewById(R.id.itemIdTextView);
            adminIDTextView = (TextView) view.findViewById(R.id.adminIDTextView);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(itemView);
                    if (selectCheckBox.isChecked()) {
                        selectCheckBox.setChecked(false);
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    } else {
                        selectCheckBox.setChecked(true);
                        selectedGroupList.addGroup(selectedGroup);
                    }
                }
            });

            selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyBoard(itemView);
                    if (selectCheckBox.isChecked()) {
                        selectedGroupList.addGroup(selectedGroup);
                    } else {
                        selectedGroupList.removeGroup(selectedGroup.getGroupID());
                    }
                }
            });

            specialListLinearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.i("Info", "Long click");
                    Log.i("Info", "view:" + v.toString());

                    hideKeyBoard(itemView);
                    ViewGroup viewGroup = (ViewGroup) v;

                    boolean groupIDFoundInd = false;
                    boolean groupNameFoundInd = false;
                    boolean groupAdminFoundInd = false;

                    String foundedGroupId = null;
                    String foundedGroupName = null;
                    String foundedAdminID = null;

                    Log.i("Info", "viewgroup:" + viewGroup.toString());

                    for (int i = 0; i < viewGroup.getChildCount(); i++) {
                        View view = viewGroup.getChildAt(i);

                        if (itemIdTextView == view.findViewById(R.id.itemIdTextView)) {
                            TextView groupIdTv = view.findViewById(R.id.itemIdTextView);
                            foundedGroupId = groupIdTv.getText().toString();
                            groupIDFoundInd = true;
                        }

                        if (adminIDTextView == view.findViewById(R.id.adminIDTextView)) {
                            TextView groupAdminID = view.findViewById(R.id.adminIDTextView);
                            foundedAdminID = groupAdminID.getText().toString();
                            groupNameFoundInd = true;
                        }

                        if (specialNameTextView == view.findViewById(R.id.specialNameTextView)) {
                            TextView groupNameTv = view.findViewById(R.id.specialNameTextView);
                            foundedGroupName = groupNameTv.getText().toString();
                            groupAdminFoundInd = true;
                        }
                    }

                    if (!groupIDFoundInd || !groupNameFoundInd || !groupAdminFoundInd) {
                        DialogBox.getInstance().showDialogBox(context, context.getResources().getString(R.string.groupNotSelected));

                        return false;
                    }

                    showGroupDetail(position, selectedGroup, specialProfileImgView);

                    return true;
                }
            });
        }

        public void setData(Group selectedGroup, int position) {

            this.selectedGroup = selectedGroup;
            this.position = position;
            this.specialNameTextView.setText(selectedGroup.getGroupName());
            this.itemIdTextView.setText(selectedGroup.getGroupID());
            this.adminIDTextView.setText(selectedGroup.getAdminID());
            imageLoader.DisplayImage(selectedGroup.getPictureUrl(), specialProfileImgView, displayRounded);
            selectCheckBox.setChecked(false);
        }
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    private void showGroupDetail(final int position,
                                 final Group selectedGroup,
                                 final ImageView specialProfileImgView) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        adapter.add("  " + context.getResources().getString(R.string.groupInformation));

        if (selectedGroup.getAdminID().equals(getFbUserID()))
            adapter.add("  " + context.getResources().getString(R.string.deleteGroup));
        else
            adapter.add("  " + context.getResources().getString(R.string.exitFromGroup));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(selectedGroup.getGroupName());

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == groupDetailItem) {

                    Intent intent = new Intent(context, DisplayGroupDetail.class);
                    intent.putExtra(groupConstant, selectedGroup);
                    context.startActivity(intent);

                } else if (item == groupDeleteItem) {

                    if (selectedGroup.getAdminID().equals(getFbUserID())) {
                        imageLoader.removeImageViewFromMap(selectedGroup.getPictureUrl());
                        removeItemFromGroupList(position);
                        FirebaseGetGroups.getInstance(selectedGroup.getAdminID()).removeGroupFromList(selectedGroup.getGroupID());
                        deleteGroupFromFirebase(selectedGroup, deleteGroup);
                        notifyDataSetChanged();
                    }
                    else{
                        //imageLoader.removeImageViewFromMap(selectedGroup.getPictureUrl());
                        removeItemFromGroupList(position);
                        deleteGroupFromFirebase(selectedGroup, exitGroup);
                        FirebaseGetGroups.getInstance(getFbUserID()).removeGroupFromList(selectedGroup.getGroupID());
                        notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(context, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void removeItemFromGroupList(int position) {

        groupListCopy.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteGroupFromFirebase(Group selectedGroup, String type) {
        FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter = new FirebaseDeleteGroupAdapter(selectedGroup, type);
    }

    @Override
    public void onBindViewHolder(GroupVerticalListAdapter.MyViewHolder holder, int position) {
        Log.i("Info", "groupListCopy len           :" + groupListCopy.size());
        Log.i("Info", "   onBindViewHolder position:" + position);


        try {
            Group selectedGroup = groupListCopy.get(position);
            holder.setData(selectedGroup, position);
        }catch (IndexOutOfBoundsException exception){
            Log.i("Info", "error:" + exception.toString());
            exception.printStackTrace();
        }
    }

    public void hideKeyBoard(View view){

        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return groupListCopy.size();
    }
}