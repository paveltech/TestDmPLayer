/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.api.ApiClient;
import com.dmplayer.api.ApiInterface;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.SongDetail;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.phonemidea.PhoneMediaControl;
import com.dmplayer.phonemidea.PhoneMediaControl.PhoneMediaControlINterface;
import com.dmplayer.pojo.SongItem;
import com.dmplayer.pojo.SongResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAllSongs extends Fragment {

    private ListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();
    public ApiInterface apiInterface;
    public String SONG_PATH = "https://vod.rockerzs.com/music/numb/master.m3u8";
    public String IMAGE_PATH = "http://storage.googleapis.com/automotive-media/";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_allsongs, null);
        apiInterface = ApiClient.getRetrofitInstance().create(ApiInterface.class);
        setupInitialViews(v);
        loadAllSongs();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View inflatreView) {
        recycler_songslist = (ListView) inflatreView.findViewById(R.id.recycler_allSongs);
        mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
        recycler_songslist.setAdapter(mAllSongsListAdapter);
    }

    private void loadAllSongs() {
        apiInterface.getSongs().enqueue(new Callback<SongResponse>() {
            @Override
            public void onResponse(Call<SongResponse> call, Response<SongResponse> response) {
                if (response.isSuccessful()) {
                    for (SongItem songItem : response.body().getMusic()) {
                        songList.add(new SongDetail(songItem.getDuration(),
                                2, songItem.getArtist(),
                                songItem.getTitle(),
                                SONG_PATH, songItem.getTitle(),
                                "" + songItem.getDuration(), IMAGE_PATH+songItem.getImage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<SongResponse> call, Throwable t) {

            }
        });


        /*
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhonemediacontrolinterface(new PhoneMediaControlINterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicList(getActivity(), -1, PhoneMediaControl.SonLoadFor.All, "");

         */
    }

    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
                mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
                mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(mDetail.getImagePath(), mViewHolder.imageSongThm, options);


            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    SongDetail mDetail = songList.get(position);
                    ((DMPlayerBaseActivity) getActivity()).loadSongsDetails(mDetail);

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SonLoadFor.All.ordinal(), -1);
                        }
                    }

                }
            });

            mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
            if (Build.VERSION.SDK_INT > 15) {
                mViewHolder.imagemore.setImageAlpha(255);
            } else {
                mViewHolder.imagemore.setAlpha(255);
            }

            mViewHolder.imagemore.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.playnext:
                                        break;
                                    case R.id.addtoque:
                                        break;
                                    case R.id.addtoplaylist:
                                        break;
                                    case R.id.gotoartis:
                                        break;
                                    case R.id.gotoalbum:
                                        break;
                                    case R.id.delete:
                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView textViewSongName;
            ImageView imageSongThm, imagemore;
            TextView textViewSongArtisNameAndDuration;
            LinearLayout song_row;
        }
    }

}
