package uren.com.catchu.GetFriendOrGroupPackage.GroupPackage;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import uren.com.catchu.FirebaseAdapterPackage.FirebaseAddGroupAdapter;
import uren.com.catchu.FirebaseAdapterPackage.FirebaseGetAccountHolder;
import uren.com.catchu.General_Utils.BitmapConversion;
import uren.com.catchu.General_Utils.UriAdapter;
import uren.com.catchu.GetFriendOrGroupPackage.SelectFriendOrGroupActivity;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedFriendList;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments.SelectedPersonFragment;
import uren.com.catchu.GetFriendOrGroupPackage.SpecialSelectTabAdapter;
import uren.com.catchu.ModelsPackage.Group;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.PermissionConstant.PERMISSION_REQUEST_ACTION_GET_CONTENT;
import static uren.com.catchu.Constants.PermissionConstant.PERMISSION_REQUEST_CAMERA;
import static uren.com.catchu.Constants.PermissionConstant.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE;
import static uren.com.catchu.Constants.StringConstants.gridShown;


public class AddGroupDetailActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolBar;
    ViewPager viewPager;
    TabLayout tabLayout;
    FloatingActionButton saveGroupInfoFab;
    EditText groupNameEditText;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ImageView groupPictureImgv;

    private static SelectedFriendList selectedFriendListInstance;

    public String photoChoosenType = "";

    private Bitmap photo = null;
    private Uri tempUri = null;
    private String imageRealPath;
    private InputStream profileImageStream;

    FirebaseAddGroupAdapter firebaseGroupAdapter;
    private StorageReference riversRef;
    private StorageReference mStorageRef;

    ProgressDialog mProgressDialog;
    Group group;

    RelativeLayout addGroupDtlRelLayout;

    private static final int IMAGE_CAMERA_SELECTED = 0;
    private static final int IMAGE_GALLERY_SELECTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_detail);

        mToolBar = (Toolbar) findViewById(R.id.toolbarLayout);
        mToolBar.setTitle(getResources().getString(R.string.addNewGroup));
        mToolBar.setSubtitle(getResources().getString(R.string.addGroupName));
        mToolBar.setNavigationIcon(R.drawable.back_arrow);
        mToolBar.setBackgroundColor(getResources().getColor(R.color.background, null));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.background_white, null));
        mToolBar.setSubtitleTextColor(getResources().getColor(R.color.background_white, null));
        setSupportActionBar(mToolBar);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        group = new Group();

        selectedFriendListInstance = SelectedFriendList.getInstance();

        TextView participantSize = (TextView) findViewById(R.id.participantSize);
        participantSize.setText(Integer.toString(selectedFriendListInstance.getSize()));

        groupPictureImgv = (ImageView) findViewById(R.id.groupPictureImgv);
        groupPictureImgv.setOnClickListener(this);

        saveGroupInfoFab = (FloatingActionButton) findViewById(R.id.saveGroupInfoFab);
        saveGroupInfoFab.setOnClickListener(this);

        groupNameEditText = (EditText) findViewById(R.id.groupNameEditText);

        addGroupDtlRelLayout = (RelativeLayout) findViewById(R.id.addGroupDtlRelLayout);
        addGroupDtlRelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
            }
        });

        openPersonSelectionPage();
    }

    public String getFbUserID() {
        return FirebaseGetAccountHolder.getUserID();
    }

    public void hideKeyBoard(){

        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void openPersonSelectionPage() {

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        SpecialSelectTabAdapter adapter = new SpecialSelectTabAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new SelectedPersonFragment(gridShown), getResources().getString(R.string.friends));
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view == groupPictureImgv) {

            startChooseImageProc();

        } else if (view == saveGroupInfoFab) {

            Log.i("Info", "groupNameEditText value:" + groupNameEditText.getText().toString());

            if (groupNameEditText.getText().toString().equals("") || groupNameEditText.getText() == null) {
                Toast.makeText(this, getResources().getString(R.string.pleaseWriteGroupName), Toast.LENGTH_SHORT).show();
                return;
            }

            saveGroupDetailToFB();
        }

    }

    private void startChooseImageProc() {

        Log.i("Info", "startChooseImageProc");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        adapter.add("  " + getResources().getString(R.string.openCamera));
        adapter.add("  " + getResources().getString(R.string.openGallery));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.chooseProfilePhoto));

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                Log.i("Info", "  >>Item selected:" + item);

                if (item == IMAGE_CAMERA_SELECTED) {

                    photoChoosenType = getResources().getString(R.string.camera);
                    startCameraProcess();

                } else if (item == IMAGE_GALLERY_SELECTED) {

                    photoChoosenType = getResources().getString(R.string.gallery);
                    startGalleryProcess();

                } else {
                    Toast.makeText(AddGroupDetailActivity.this, "Item Selected Error!!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startCameraProcess() {

        Log.i("Info", "startCameraProcess");

        if (!checkCameraHardware(this)) {
            Toast.makeText(this, getResources().getString(R.string.deviceHasNoCamera), Toast.LENGTH_SHORT).show();
            return;
        }

        checkMediaStoragePermission();
    }

    private void checkMediaStoragePermission() {

        Log.i("Info", "checkMediaStoragePermission");

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {

            checkCameraPermission();
        }
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(intent, PERMISSION_REQUEST_CAMERA);
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void startGalleryProcess() {

        Log.i("Info", "startGalleryProcess");

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selectPicture)),
                PERMISSION_REQUEST_ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("Info", "onActivityResult starts");

        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {

                case PERMISSION_REQUEST_CAMERA:
                    manageProfilePicChoosen(data);
                    break;

                case PERMISSION_REQUEST_ACTION_GET_CONTENT:
                    manageProfilePicChoosen(data);
                    break;

                default:
                    Toast.makeText(this, "requestCode error:" + requestCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manageProfilePicChoosen(Intent data) {

        try {

            if (photoChoosenType == getResources().getString(R.string.camera)) {

                photo = (Bitmap) data.getExtras().get("data");
                tempUri = getImageUri(getApplicationContext(), photo);
                imageRealPath = getRealPathFromCameraURI(tempUri);
                photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
                photo = BitmapConversion.getBitmapOriginRotate(photo, imageRealPath);

                Log.i("Info", "tempuri:" + tempUri);

            } else if (photoChoosenType == getResources().getString(R.string.gallery)) {

                tempUri = data.getData();
                imageRealPath = UriAdapter.getPathFromGalleryUri(getApplicationContext(), tempUri);
                profileImageStream = getContentResolver().openInputStream(tempUri);
                photo = BitmapFactory.decodeStream(profileImageStream);
                photo = BitmapConversion.getRoundedShape(photo, 600, 600, imageRealPath);
            }

            groupPictureImgv.setImageBitmap(photo);

        } catch (Exception e) {
            Log.i("Info", "  >>manageProfilePicChoosen exception:" + e.toString());
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

            return Uri.parse(path);
        } catch (Exception e) {

            Log.i("Info", "getImageUri exception:" + e.toString());
            return null;
        }
    }

    public String getRealPathFromCameraURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("Info", "onRequestPermissionsResult starts");

        switch (requestCode) {

            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    checkCameraPermission();
                }
                break;
            case PERMISSION_REQUEST_CAMERA:
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivityForResult(intent, PERMISSION_REQUEST_CAMERA);
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.errorOccured), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void saveGroupDetailToFB() {

        group.setAdminID(getFbUserID());
        group.setGroupName(groupNameEditText.getText().toString());
        group.setFriendList(selectedFriendListInstance.getSelectedFriendList());

        firebaseGroupAdapter = new FirebaseAddGroupAdapter(group);

        saveGroupPicToFirebase();
    }

    public void saveGroupPicToFirebase() {

        mProgressDialog.setMessage(getResources().getString(R.string.groupIsCreating));
        mProgressDialog.show();

        riversRef = mStorageRef.child("Groups/groupPics").child(firebaseGroupAdapter.getGroupID() + ".jpg");

        if(tempUri != null)
            savePicture();
        else
            returnPreviousActivity();
    }

    private void returnPreviousActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), SelectFriendOrGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }, 1000);
    }

    private void savePicture() {

        riversRef.putFile(tempUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        group.setPictureUrl(downloadUrl.toString());
                        firebaseGroupAdapter.savePictureUrl(group.getPictureUrl());
                        returnPreviousActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(AddGroupDetailActivity.this, getResources().getString(R.string.errorOccured) +
                                        exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
