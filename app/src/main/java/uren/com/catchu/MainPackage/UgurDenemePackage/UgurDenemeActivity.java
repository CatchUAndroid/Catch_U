package uren.com.catchu.MainPackage.UgurDenemePackage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pl.aprilapps.easyphotopicker.EasyImage;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments.SelectedPersonFragment;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialSelectTabAdapter;
import uren.com.catchu.R;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static java.security.AccessController.getContext;
import static uren.com.catchu.Constants.StringConstants.gridShown;

public class UgurDenemeActivity extends AppCompatActivity{

    Button fotoListele;
    Button fotoListele2;
    Button fotoListele3;

    ImageView selectedImageV;

    View photoLayout;
    RelativeLayout mainRelativeLayout;
    LinearLayout mainLinearLayout;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ugur_deneme);

        fotoListele = findViewById(R.id.listPhotosButton);
        fotoListele2 = findViewById(R.id.fotoListele2);
        fotoListele3 = findViewById(R.id.fotoListele3);
        mainRelativeLayout = findViewById(R.id.mainRelativeLayout);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);
        selectedImageV = findViewById(R.id.selectedImageV);

        fotoListele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(UgurDenemeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    EasyImage.openDocuments(UgurDenemeActivity.this, 0);
                } else {
                    Nammu.askForPermission(UgurDenemeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                        @Override
                        public void permissionGranted() {
                            EasyImage.openDocuments(UgurDenemeActivity.this, 0);
                        }

                        @Override
                        public void permissionRefused() {

                        }
                    });
                }
            }
        });




        fotoListele2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fotoListele3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = getLayoutInflater();
                photoLayout = inflater.inflate(R.layout.default_demo_layout, mainRelativeLayout, false);

                final Button cancelButton = (Button) photoLayout.findViewById(R.id.cancelButton);
                final Button approveButton = (Button) photoLayout.findViewById(R.id.approveButton);

                mainRelativeLayout.addView(photoLayout);
                fotoListele3.setEnabled(false);

                openImageSelectFragment();

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainRelativeLayout.removeView(photoLayout);
                        fotoListele3.setEnabled(true);
                    }
                });

                approveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });

    }

    private void openImageSelectFragment() {

        PermissionModule permissionModule = new PermissionModule(this);
        permissionModule.checkPermissions();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new GalleryPickerFrag(), null);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }




}

