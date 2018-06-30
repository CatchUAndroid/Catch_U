package uren.com.catchu.GetFriendOrGroupPackage.ListAdapters;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseDeleteFriendAdapter;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.General_Utils.LazyList.ImageLoader;
import uren.com.catchu.GetFriendOrGroupPackage.GroupPackage.DisplayGroupDetail;
import uren.com.catchu.ModelsPackage.Friend;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.displayRounded;
import static uren.com.catchu.Constants.StringConstants.friendsCacheDirectory;

public class GroupDTLVerticalListAdapter extends RecyclerView.Adapter<GroupDTLVerticalListAdapter.MyViewHolder> {

    private ArrayList<Friend> data;
    public ImageLoader imageLoader;
    View view;
    private ImageView specialProfileImgView;
    LinearLayout specialListLinearLayout;
    LayoutInflater layoutInflater;

    Group group;

    Context context;
    Activity activity;

    public static final int removeFromGroup = 0;
    public static final int displayProfile = 1;

    TextView textview;

    public GroupDTLVerticalListAdapter(Context context, Group group) {
        layoutInflater = LayoutInflater.from(context);
        data = group.getFriendList();
        Collections.sort(data, new CustomComparator());
        this.context = context;
        this.group = group;
        activity = (Activity) context;
        imageLoader = new ImageLoader(context.getApplicationContext(), friendsCacheDirectory);
    }

    public class CustomComparator implements Comparator<Friend> {
        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getNameSurname().compareToIgnoreCase(o2.getNameSurname());
        }
    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public GroupDTLVerticalListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.group_detail_list, parent, false);
        GroupDTLVerticalListAdapter.MyViewHolder holder = new GroupDTLVerticalListAdapter.MyViewHolder(view);

        textview = (TextView) activity.findViewById(R.id.personCntTv);
        textview.setText(Integer.toString(data.size()));

        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userNameSurname;
        Friend selectedFriend;
        int position = 0;
        Button adminDisplayBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            specialProfileImgView = (ImageView) view.findViewById(R.id.specialPictureImgView);
            userNameSurname = (TextView) view.findViewById(R.id.specialNameTextView);
            specialListLinearLayout = (LinearLayout) view.findViewById(R.id.specialListLinearLayout);
            adminDisplayBtn = (Button) view.findViewById(R.id.adminDisplayBtn);

            specialListLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);

                    if (!getFbUserID().equals(selectedFriend.getUserID())) {
                        adapter.add(context.getResources().getString(R.string.removeFromGroup));
                        adapter.add(context.getResources().getString(R.string.viewTheProfile));
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (item == removeFromGroup) {
                                removeUserFromGroupList(position);
                                FirebaseDeleteFriendAdapter firebaseDeleteFriendAdapter =
                                        new FirebaseDeleteFriendAdapter(group.getGroupID(), selectedFriend.getUserID());
                                firebaseDeleteFriendAdapter.deleteUserFromGroup();
                                notifyDataSetChanged();

                                textview.setText(Integer.toString(data.size()));

                                if (context instanceof DisplayGroupDetail) {
                                    ((DisplayGroupDetail) context).setGroupFriendList(data);
                                }

                            } else if (item == displayProfile) {


                            } else {
                                Toast.makeText(context, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }

        public void setData(Friend selectedFriend, int position) {

            //kullanici adi soyadi bilgisi yazilacak
            if (getFbUserID().equals(selectedFriend.getUserID()))
                this.userNameSurname.setText(context.getResources().getString(R.string.constantYou));
            else
                this.userNameSurname.setText(selectedFriend.getNameSurname());

            //Admin grup box degeri eklenecek
            if (group.getAdminID().equals(selectedFriend.getUserID()))
                adminDisplayBtn.setVisibility(View.VISIBLE);
            else
                adminDisplayBtn.setVisibility(View.GONE);

            this.position = position;
            this.selectedFriend = selectedFriend;
            imageLoader.DisplayImage(selectedFriend.getProfilePicSrc(), specialProfileImgView, displayRounded);
        }
    }

    public void removeUserFromGroupList(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    @Override
    public void onBindViewHolder(GroupDTLVerticalListAdapter.MyViewHolder holder, int position) {
        Friend selectedFriend = data.get(position);
        holder.setData(selectedFriend, position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}