package uren.com.catchu.GetFriendOrGroupPackage.SpecialFragments;


import android.annotation.SuppressLint;
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

import butterknife.ButterKnife;
import uren.com.catchu.GetFriendOrGroupPackage.ListAdapters.FriendGridListAdapter;
import uren.com.catchu.GetFriendOrGroupPackage.ListAdapters.FriendVerticalListAdapter;
import uren.com.catchu.GetFriendOrGroupPackage.SelectedItemPackage.SelectedFriendList;
import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.gridShown;
import static uren.com.catchu.Constants.StringConstants.horizontalShown;
import static uren.com.catchu.Constants.StringConstants.verticalShown;

@SuppressLint("ValidFragment")
public class SelectedPersonFragment extends Fragment {

    RecyclerView personRecyclerView;

    private View mView;
    String viewType;

    LinearLayoutManager linearLayoutManager;
    GridLayoutManager gridLayoutManager;

    private static SelectedFriendList selectedFriendListInstance;

    @SuppressLint("ValidFragment")
    public SelectedPersonFragment(String viewType) {
        this.viewType = viewType;
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

    public void getData(){

        selectedFriendListInstance = SelectedFriendList.getInstance();

        switch (viewType){

            case verticalShown:
                FriendVerticalListAdapter friendVerticalListAdapter = new FriendVerticalListAdapter(getActivity(),
                        selectedFriendListInstance.getSelectedFriendList(), null);
                personRecyclerView.setAdapter(friendVerticalListAdapter);
                linearLayoutManager  = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case horizontalShown:
                linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                personRecyclerView.setLayoutManager(linearLayoutManager);
                break;

            case gridShown:
                FriendGridListAdapter friendGridListAdapter = new FriendGridListAdapter(getActivity(),
                        selectedFriendListInstance.getSelectedFriendList());
                personRecyclerView.setAdapter(friendGridListAdapter);
                gridLayoutManager =new GridLayoutManager(getActivity(), 4);
                personRecyclerView.setLayoutManager(gridLayoutManager);
                break;

            default:
                Toast.makeText(getActivity(), "SelectedPersonFragment Fragment getData teknik hata!!", Toast.LENGTH_SHORT).show();
        }
    }
}
