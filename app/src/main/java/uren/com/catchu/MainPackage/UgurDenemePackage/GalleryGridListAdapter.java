package uren.com.catchu.MainPackage.UgurDenemePackage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import uren.com.catchu.R;

import static uren.com.catchu.Constants.StringConstants.friendsCacheDirectory;

public class GalleryGridListAdapter extends RecyclerView.Adapter<GalleryGridListAdapter.MyViewHolder> {

    private ArrayList<File> fileList;
    View view;

    LayoutInflater layoutInflater;

    Context context;

    final private int galleryImgId = 0;
    final private int cameraImgId = 1;

    int selectedPosition = -1;

    public GalleryGridListAdapter(Context context, ArrayList<File> fileList) {
        layoutInflater = LayoutInflater.from(context);
        this.fileList = fileList;
        this.context = context;
    }


    public Object getItem(int position) {
        return position;
    }

    @Override
    public GalleryGridListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = layoutInflater.inflate(R.layout.media_item_view, parent, false);
        GalleryGridListAdapter.MyViewHolder holder = new GalleryGridListAdapter.MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        File selectedFile;
        int position = 0;
        ImageView specialProfileImgView;
        //ImageView tickImageView;
        int stubId = 0;

        public MyViewHolder(final View itemView) {
            super(itemView);
            specialProfileImgView = view.findViewById(R.id.mMediaThumb);
            //tickImageView = view.findViewById(R.id.tickImageView);

            specialProfileImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }


        public void setData(File selectedFile, int position) {
            //this.position = position;
            //this.selectedFile = selectedFile;

            Picasso.with(context)
                    .load(Uri.fromFile(selectedFile))
                    .resize(500, 500)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_media)
                    .error(R.drawable.placeholder_error_media)
                    .noFade()
                    .into(specialProfileImgView);
        }

        public void setImage(int stubId, int position) {
            this.stubId = stubId;
            this.position = position;

            Picasso.with(context)
                    .load(this.stubId)
                    .resize(500, 500)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_media)
                    .error(R.drawable.placeholder_error_media)
                    .noFade()
                    .into(specialProfileImgView);
        }

        //Secilen item a gore camera acilir, yada gallery acilir yada secilen resim eklenir...
        public void checkHolderSelection(int position) {
            this.position = position;

            if (position == galleryImgId || position == cameraImgId)
                specialProfileImgView.setAlpha(1.0f);
            else
                specialProfileImgView.setAlpha(0.3f);

            /*switch (position) {
                case galleryImgId:

                    break;

                case cameraImgId:

                    break;

                default:
                    specialProfileImgView.setAlpha(0.3f);
                    break;
            }*/
        }

        public void notCheckSelection(int position) {
            this.position = position;

            specialProfileImgView.setAlpha(1.0f);
        }
    }


    @Override
    public void onBindViewHolder(GalleryGridListAdapter.MyViewHolder holder, int position) {

        Log.i("Info", "selectedPosition:" + selectedPosition + "-position:" + position);

        int stub_id;


        switch (position){
            case galleryImgId:
                stub_id = R.drawable.gallery;
                holder.setImage(stub_id, position);
                break;

            case cameraImgId:
                stub_id = R.drawable.camera;
                holder.setImage(stub_id, position);
                break;

            default:
                File selectedImage = fileList.get(position - 2);
                holder.setData(selectedImage, position);
                break;
        }

        if (position == selectedPosition)
            holder.checkHolderSelection(position);
        else
            holder.notCheckSelection(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return  fileList.size() + 2;
    }


}