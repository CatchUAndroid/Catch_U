package uren.com.catchu.GetFriendOrGroupPackage;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetFriends;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetGroups;
import uren.com.catchu.General_Utils.DialogBox;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedFriendList;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedGroupList;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments.GroupFragment;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments.PersonFragment;
import uren.com.catchu.LoginPackage.LoginActivity;
import uren.com.catchu.R;
import uren.com.catchu.SingletonClassPackages.ClearSingletonClasses;

import static uren.com.catchu.Constants.NumericConstants.groups;
import static uren.com.catchu.Constants.NumericConstants.persons;
import static uren.com.catchu.Constants.StringConstants.propGroups;
import static uren.com.catchu.Constants.StringConstants.propPersons;
import static uren.com.catchu.Constants.StringConstants.verticalShown;

public class SelectFriendOrGroupActivity extends AppCompatActivity implements
        View.OnClickListener{

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public static String selectedProperty;
    public static boolean specialSelectedInd = false;

    private static SelectedGroupList selectedGroupListInstance;
    private static SelectedFriendList selectedFriendListInstance;

    Toolbar mToolBar;
    SpecialSelectTabAdapter adapter;
    TextView noItemFoundTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend_or_group);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setTitle(getResources().getString(R.string.selectPersonOrGroup));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        hideSoftKeyboard();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        viewPager.setOnClickListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        noItemFoundTv = (TextView) findViewById(R.id.noItemFoundTv);

        selectedProperty = propPersons;

        FloatingActionButton addSpecialFab = (FloatingActionButton) findViewById(R.id.addSpecialFab);

        addSpecialFab.setOnClickListener(this);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()){
                    case persons:
                        if(selectedProperty.equals(propGroups))
                            hideSoftKeyboard();

                        selectedProperty = propPersons;

                        if( FirebaseGetFriends.getInstance(getFbUserID()).getListSize() == 0) {
                            noItemFoundTv.setVisibility(View.VISIBLE);
                            noItemFoundTv.setText(getResources().getString(R.string.noFriendFound));
                        }else {
                            noItemFoundTv.setVisibility(View.GONE);
                        }

                        break;
                    case groups:
                        if(selectedProperty.equals(propPersons))
                            hideSoftKeyboard();

                        selectedProperty = propGroups;

                        if(FirebaseGetGroups.getInstance(getFbUserID()).getListSize() == 0) {
                            noItemFoundTv.setVisibility(View.VISIBLE);
                            noItemFoundTv.setText(getResources().getString(R.string.noGroupFound));
                        }else {
                            noItemFoundTv.setVisibility(View.GONE);
                        }

                        Log.i("Info", "Tablayout groups");
                        break;
                    default:
                        Log.i("Info", "Tablayout unknown");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_special_select, menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(selectedProperty.equals(propPersons)) {
                    PersonFragment personFragment = new PersonFragment(getFbUserID(), verticalShown, null, null, newText,
                            SelectFriendOrGroupActivity.this);
                    adapter.updateFragment(persons, personFragment);
                    reloadAdapter();
                }else if(selectedProperty.equals(propGroups)){
                    GroupFragment groupFragment = new GroupFragment(getFbUserID(), SelectFriendOrGroupActivity.this, newText);
                    adapter.updateFragment(groups, groupFragment);
                    reloadAdapter();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem register = menu.findItem(R.id.addNewGroup);
        if(selectedProperty.equals(propPersons))
            register.setVisible(false);
        else
            register.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.logOut:
                ClearSingletonClasses.clearAllClasses();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.addNewGroup:

                if(FirebaseGetFriends.getInstance(getFbUserID()).getListSize() == 0){

                    DialogBox.getInstance().showDialogBox(this, getResources().getString(R.string.addFriendFirst));

                }else {
                    startActivity(new Intent(this, AddFriendToGroupActivity.class));
                }

                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(final ViewPager viewPager) {

        adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new PersonFragment(getFbUserID(), verticalShown, null,
                null, null, SelectFriendOrGroupActivity.this),getResources().getString(R.string.friends));
        adapter.addFragment(new GroupFragment(getFbUserID(), SelectFriendOrGroupActivity.this, null), getResources().getString(R.string.groups));
        viewPager.setAdapter(adapter);
    }

    public void reloadAdapter(){
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStart(){
        super.onStart();
        reloadAdapter();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        Intent intent;

        switch (i) {
            case R.id.addSpecialFab:
                selectedFriendListInstance = SelectedFriendList.getInstance();
                selectedGroupListInstance = SelectedGroupList.getInstance();

                specialSelectedInd = true;

                if(selectedProperty == propPersons){
                    if(selectedFriendListInstance.getSelectedFriendList().size() == 0){
                        Toast.makeText(this, getResources().getString(R.string.selectLeastOneFriend), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedGroupListInstance.clearGrouplist();
                }else {
                    if(selectedGroupListInstance.getGroupList().size() == 0){
                        Toast.makeText(this, getResources().getString(R.string.selectLeastOneGroup), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    selectedFriendListInstance.clearFriendList();
                }

                finish();
                break;

            case R.id.viewpager:
                hideSoftKeyboard();
                break;

            default:
                Toast.makeText(this, getResources().getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
