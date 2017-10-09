package sattar.androidnewsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentOne extends Fragment {

    private static final String TAG = "FragmentOne";
    List<News> data;
    RecyclerViewAdapter mAdapter;

    public FragmentOne() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new ArrayList<>();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder().
                readTimeout(60, TimeUnit.SECONDS).
                connectTimeout(60, TimeUnit.SECONDS);
        okHttpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://jsonblob.com/api/jsonBlob/51f6c057-ac31-11e7-a12e-f379e8dd88b5/")
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        Call<List<News>> postCall = service.getPosts();

        postCall.enqueue(new Callback<List<News>>() {

            @Override
            public void onResponse(Call<List<News>> call, retrofit2.Response<List<News>> response) {
                if (response.isSuccessful()) {
                    setAdapter(response.body());
                }

            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        //Log.d(TAG, "onCreateView: " + data.get(0).getTitle());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RecyclerViewAdapter(data);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    public void setAdapter(List<News> newsList) {
        data.clear();
        data.addAll(newsList);
        mAdapter.notifyDataSetChanged();
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mTextView;
        private TextView mDateView;
        private CircleImageView mImgView;
        public View view;

        public RecyclerViewHolder(View itemView) { super(itemView); }

        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.card_view, container, false));

            mCardView = itemView.findViewById(R.id.card_container);
            mTextView = itemView.findViewById(R.id.text_holder);
            mImgView = itemView.findViewById(R.id.img_holder);
            mDateView = itemView.findViewById(R.id.date_holder);
        }


    }

    private class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
        List<News> mList;


        public RecyclerViewAdapter(List<News> list) {
            this.mList = list;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            return new RecyclerViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
            int resId = getResources().getIdentifier(mList.get(position).getImagePath(), "drawable", getActivity().getPackageName());
            holder.mImgView.setImageResource(resId);
            holder.mTextView.setText(mList.get(position).getTitle());
            holder.mDateView.setText(mList.get(position).getDate());
            holder.mCardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(getContext(), SecondActivity.class);
                    intent.putExtra("News_title", mList.get(position).getTitle());
                    intent.putExtra("News_content", mList.get(position).getContent());
                    intent.putExtra("News_imagePath", mList.get(position).getImagePath());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mList != null) {
                return mList.size();
            }
            return 0;
        }
    }
}