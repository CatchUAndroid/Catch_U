package uren.com.catchu.GetFriendOrGroupPackage.GroupPackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseDeleteGroupAdapter;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetGroups;
import uren.com.catchu.General_Utils.LazyList.ImageLoader;
import uren.com.catchu.GetFriendOrGroupPackage.AddFriendToGroupActivity;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialSelectTabAdapter;
import uren.com.catchu.ModelsPackage.Friend;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.*;

public class DisplayGroupDetail extends AppCompatActivity implements View.OnClickListener{

    public static Group group;
    ImageView groupPictureImgV;
    public ImageLoader imageLoader;
    TextView personCntTv;

    SpecialSelectTabAdapter adapter;

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_group_detail);

        imageLoader = new ImageLoader(this, groupsCacheDirectory);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main_activity_page);

        Intent i = getIntent();
        group = (Group) i.getSerializableExtra(groupConstant);

        groupPictureImgV = (ImageView) findViewById(R.id.groupPictureImgv);

        personCntTv = (TextView) findViewById(R.id.personCntTv);
        personCntTv.setText(Integer.toString(group.getFriendList().size()));

        CardView addFriendCardView = (CardView) findViewById(R.id.addFriendCardView);
        addFriendCardView.setOnClickListener(this);

        CardView deleteGroupCardView = (CardView)findViewById(R.id.deleteGroupCardView);
        deleteGroupCardView.setOnClickListener(this);

        TextView deleteGroupTextView = (TextView) findViewById(R.id.deleteGroupTextView);

        if(getFbUserID().equals(group.getAdminID())) {
            addFriendCardView.setVisibility(View.VISIBLE);
            deleteGroupTextView.setText(getResources().getString(R.string.deleteGroup));
        }else {
            deleteGroupTextView.setText(getResources().getString(R.string.exitFromGroup));
        }

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(group.getGroupName());

        imageLoader.DisplayImage(group.getPictureUrl(), groupPictureImgV, displayRectangle);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();
    }


    private void setupViewPager() {
        adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new GroupDetailFragment(group, verticalShown)," ");
        viewPager.setAdapter(adapter);
    }

    public Group getGroup(){
        return this.group;
    }

    public void setGroup(Group group){
        this.group = group;
    }

    public void setGroupFriendList(ArrayList<Friend> friendList){
        this.group.setFriendList(friendList);
    }

    public static void addFriendToGroup(Friend friend){
        group.getFriendList().add(friend);
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    public void reloadAdapter(){
        adapter.addFragment(new GroupDetailFragment(group, verticalShown)," ");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        Intent intent;

        switch (i) {
            case R.id.addFriendCardView:
                intent = new Intent(getApplicationContext(), AddFriendToGroupActivity.class);
                intent.putExtra(comeFromPage, this.getClass().getSimpleName());
                intent.putExtra(groupConstant, group);
                startActivity(intent);
                break;

            case R.id.deleteGroupCardView:
                if(getFbUserID().equals(group.getAdminID())) {
                    showYesNoDialog(null, getResources().getString(R.string.areYouSureToDeleteGroup), deleteGroup);
                }else {
                    showYesNoDialog(null, getResources().getString(R.string.areYouSureExitFromGroup), exitGroup);
                }
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void showYesNoDialog(String title, String message, final String type) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGroupDetail.this, android.R.style.Theme_Material_Dialog_Alert);

        builder.setIcon(R.drawable.warning_icon40);
        builder.setMessage(message);

        if (title != null)
            builder.setTitle(title);

        builder.setPositiveButton(getResources().getString(R.string.constantYes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(type.equals(deleteGroup)){
                    imageLoader.removeImageViewFromMap(group.getPictureUrl());

                    FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter =
                            new FirebaseDeleteGroupAdapter(group, deleteGroup);
                    FirebaseGetGroups.getInstance(group.getAdminID()).removeGroupFromList(group.getGroupID());

                    finish();
                }else if(type.equals(exitGroup)){

                    FirebaseDeleteGroupAdapter firebaseDeleteGroupAdapter =
                            new FirebaseDeleteGroupAdapter(group, exitGroup);
                    FirebaseGetGroups.getInstance(getFbUserID()).removeGroupFromList(group.getGroupID());

                    finish();
                }else {
                    dialog.dismiss();
                }
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.constantNo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}