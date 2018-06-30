package uren.com.catchu.GetFriendOrGroupPackage;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseAddFriendToGroupAdapter;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetGroups;
import uren.com.catchu.GetFriendOrGroupPackage.GroupPackage.AddGroupDetailActivity;
import uren.com.catchu.GetFriendOrGroupPackage.GroupPackage.DisplayGroupDetail;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedFriendList;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments.PersonFragment;
import uren.com.catchu.ModelsPackage.Friend;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.comeFromPage;
import static uren.com.catchu.Constants.StringConstants.groupConstant;
import static uren.com.catchu.Constants.StringConstants.verticalShown;


public class AddFriendToGroupActivity extends AppCompatActivity {

    Toolbar mToolBar;

    ViewPager viewPager;
    String comingPageName = null;

    Group group;

    private static SelectedFriendList selectedFriendListInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_to_group);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        //mToolBar.setTitle(getResources().getString(R.string.addNewGroup));
        mToolBar.setSubtitle(getResources().getString(R.string.addFriendToGroup));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        getIntentValues(savedInstanceState);

        Log.i("info", "ddd:" + FirebaseGetGroups.getInstance(getFbUserID()));

        SelectedFriendList.setInstance(null);

        FloatingActionButton nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        nextFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedPerson();
            }
        });

        openPersonSelectionPage();
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }


    private void getIntentValues(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                comingPageName = extras.getString(comeFromPage);
            }
        } else {
            comingPageName = (String) savedInstanceState.getSerializable(comeFromPage);
        }

        Intent i = getIntent();
        group = (Group) i.getSerializableExtra(groupConstant);
    }

    private void openPersonSelectionPage() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new PersonFragment(getFbUserID(), verticalShown, comingPageName, group, null,
                AddFriendToGroupActivity.this)," ");
        viewPager.setAdapter(adapter);
    }

    public void checkSelectedPerson(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        if (selectedFriendListInstance.getSelectedFriendList().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
            return;
        }

        if(comingPageName != null){
            if(comingPageName.equals(DisplayGroupDetail.class.getSimpleName())) {
                addFriendToGroup();
                finish();
            }
        }else
            startActivity(new Intent(this, AddGroupDetailActivity.class));
    }

    private void addFriendToGroup() {

        for(Friend friend : selectedFriendListInstance.getSelectedFriendList()) {
            DisplayGroupDetail.addFriendToGroup(friend);
        }

        FirebaseAddFriendToGroupAdapter addFriendToGroupAdapter =
                new FirebaseAddFriendToGroupAdapter(selectedFriendListInstance.getSelectedFriendList(), group.getGroupID());
        addFriendToGroupAdapter.addFriendsToGroup();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}