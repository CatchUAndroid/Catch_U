package uren.com.catchu.MainPackage;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import uren.com.catchu.FragmentControllers.FragNavController;
import uren.com.catchu.FragmentControllers.FragmentHistory;
import uren.com.catchu.General_Utils.CommonUtils;
import uren.com.catchu.LoginPackage.LoginActivity;
import uren.com.catchu.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String FBuserId;
    private Context context;

    private int onPauseCount = 0;
    private boolean onPausedInd = false;

    @BindView(R.id.content_frame)
    FrameLayout contentFrame;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private int[] mTabIconsSelected = {
            R.drawable.tab_home,
            R.drawable.tab_search,
            R.drawable.tab_pin,
            R.drawable.tab_news,
            R.drawable.tab_profile};

    @BindArray(R.array.tab_name)
    String[] TABS;

    @BindView(R.id.bottom_tab_layout)
    TabLayout bottomTabLayout;

    private FragNavController mNavController;

    private FragmentHistory fragmentHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null)
            startActivity(new Intent(this, LoginActivity.class));

        initValues();

    }

    private void initValues() {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        FBuserId = currentUser.getUid();

        onPausedInd = true;
        context = this;
        ButterKnife.bind(this);

        initToolbar();
        initTab();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initTab() {
        if (bottomTabLayout != null) {
            for (int i = 0; i < TABS.length; i++) {
                bottomTabLayout.addTab(bottomTabLayout.newTab());
                TabLayout.Tab tab = bottomTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(getTabView(i));
            }
        }
    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab_item_bottom, null);
        ImageView icon = (ImageView) view.findViewById(R.id.tab_icon);
        icon.setImageDrawable(CommonUtils.setDrawableSelector(MainActivity.this, mTabIconsSelected[position], mTabIconsSelected[position]));
        return view;
    }
}
