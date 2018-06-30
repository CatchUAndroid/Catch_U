package uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetFriends;
import uren.com.catchu.GetFriendOrGroupPackage.ListAdapters.FriendGridListAdapter;
import uren.com.catchu.GetFriendOrGroupPackage.ListAdapters.FriendVerticalListAdapter;
import uren.com.catchu.ModelsPackage.Friend;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.gridShown;
import static uren.com.catchu.Constants.StringConstants.horizontalShown;
import static uren.com.catchu.Constants.StringConstants.verticalShown;

@SuppressLint("ValidFragment")
public class PersonFragment extends Fragment {

    RecyclerView personRecyclerView;

    private View mView;
    String FBuserID;
    String viewType;
    String comingPage;
    Group group;
    String searchText;
    Context context;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    @SuppressLint("ValidFragment")
    public PersonFragment(String FBuserID, String viewType, String comingPage,
                          Group group, String searchText, Context context) {
        this.FBuserID = FBuserID;
        this.viewType = viewType;
        this.comingPage = comingPage;
        this.group = group;
        this.searchText = searchText;
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_special_select, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        personRecyclerView = (RecyclerView) mView.findViewById(R.id.specialRecyclerView);
        getData();
    }

    public void getData() {

        FirebaseGetFriends instance = FirebaseGetFriends.getInstance(FBuserID);

        switch (viewType) {
            case verticalShown:
                FriendVerticalListAdapter friendVerticalListAdapter = null;

                if (comingPage != null) {
                    //if (comingPage.equals(DisplayGroupDetail.class.getSimpleName()))
                    //    friendVerticalListAdapter = new FriendVerticalListAdapter(context, getFriendsForGroup(instance), null);
                    //else
                        friendVerticalListAdapter = new FriendVerticalListAdapter(context, instance.getFriendList(), null);
                } else {
                    friendVerticalListAdapter = new FriendVerticalListAdapter(context, instance.getFriendList(), searchText);
                }

                personRecyclerView.setAdapter(friendVerticalListAdapter);
                linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                instance = FirebaseGetFriends.getInstance(FBuserID);
                FriendGridListAdapter friendGridListAdapter = new FriendGridListAdapter(context, instance.getFriendList());
                personRecyclerView.setAdapter(friendGridListAdapter);
                gridLayoutManager = new GridLayoutManager(context, 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(context, "Person Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }

    //Gruba eklenecek arkadaslar listelenecek
    public ArrayList<Friend> getFriendsForGroup(FirebaseGetFriends instance) {

        ArrayList<Friend> friendArrayList = new ArrayList<Friend>();

        for (Friend friend : instance.getFriendList()) {

            boolean friendFind = false;

            for (int i = 0; i < group.getFriendList().size(); i++) {
                Friend tempFriend = group.getFriendList().get(i);

                if (friend.getUserID().equals(tempFriend.getUserID())) {
                    friendFind = true;
                    break;
                }
            }

            if (!friendFind)
                friendArrayList.add(friend);
        }
        return friendArrayList;
    }
}