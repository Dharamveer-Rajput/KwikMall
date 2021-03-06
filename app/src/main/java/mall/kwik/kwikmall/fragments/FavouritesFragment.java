package mall.kwik.kwikmall.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import mall.kwik.kwikmall.AppConstants;
import mall.kwik.kwikmall.baseFragActivity.BaseFragment;
import mall.kwik.kwikmall.adapters.FavouritesAdapter;
import mall.kwik.kwikmall.apiresponse.GetFavouriteResponse.GetFavouritePayload;
import mall.kwik.kwikmall.apiresponse.GetFavouriteResponse.GetFavouriteSuccess;
import mall.kwik.kwikmall.R;


/**
 * Created by dharamveer on 10/1/18.
 */

public class FavouritesFragment extends BaseFragment {

    private ImageView imageViewBackFavourties;
    private RecyclerView recyclerViewFavourites;

    private FavouritesAdapter adapterFavourites;
    public ArrayList<GetFavouritePayload> getFavouritePayloads = new ArrayList<>();
    private FrameLayout recyclerFavouritesFrame;
    private TextView tvnoitems;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favourites, container, false);


        //Image views
        imageViewBackFavourties = view.findViewById(R.id.imageViewBackFavourties);

        //Frame Layout
        recyclerFavouritesFrame = view.findViewById(R.id.recyclerFavouritesFrame);

        //Text View
        tvnoitems = view.findViewById(R.id.tvnoitems);

        //Recyclerview
        recyclerViewFavourites = view.findViewById(R.id.recyclerViewFavourites);

        imageViewBackFavourties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( getFragmentManager().getBackStackEntryCount() > 0)
                {
                    getFragmentManager().popBackStack();
                    return;
                }
                getActivity().onBackPressed();
            }
        });


        GetFavouritesListApi();


        return view;
    }

    private void GetFavouritesListApi() {

        String idUser = String.valueOf(sharedPrefsHelper.get(AppConstants.USER_ID,0));

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("userId",idUser);


        compositeDisposable.add(apiService.getfavouriteProducts(stringStringHashMap)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GetFavouriteSuccess>() {
                    @Override
                    public void accept(GetFavouriteSuccess getFavouriteSuccess) throws Exception {

                        if(getFavouriteSuccess.getSuccess()){

                            getFavouritePayloads = new ArrayList<>(getFavouriteSuccess.getPayload());



                            adapterFavourites = new FavouritesAdapter(getActivity(),getFavouritePayloads);



                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerViewFavourites.setLayoutManager(mLayoutManager);
                            recyclerViewFavourites.setItemAnimator(new DefaultItemAnimator());

                            recyclerViewFavourites.setAdapter(adapterFavourites);


                            adapterFavourites.getToCartActivity(new FavouritesAdapter.FavouriteClickListenerToCart() {
                                @Override
                                public void GoToCartActivity(View v) {

                                    Fragment fragment = null;
                                    fragment = new ViewCartFragment();

                                    if(fragment!=null){

                                        FragmentManager fragmentManager = getFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment, "viewCartFragment")
                                                .addToBackStack(null).commit();
                                        getActivity().overridePendingTransition(R.anim.slide_in, R.anim.nothing);

                                    }
                                }
                            });


                            adapterFavourites.setOnItemClickListener(new FavouritesAdapter.FavouriteClickListener() {
                                @Override
                                public void onDelete(View view, int position) {
                                    getFavouritePayloads.remove(position);
                                    adapterFavourites.notifyItemRemoved(position);

                                }
                            });


                        }
                        else {
                            recyclerFavouritesFrame.setVisibility(View.GONE);
                            tvnoitems.setVisibility(View.VISIBLE);

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        showAlertDialog("Retry",throwable.getMessage());
                    }
                }));

/*
        restClient.getfavouriteProducts(stringStringHashMap).enqueue(new Callback<GetFavouriteSuccess>() {
            @Override
            public void onResponse(Call<GetFavouriteSuccess> call, Response<GetFavouriteSuccess> response) {


                if(response.body().getSuccess()){

                    getFavouritePayloads = new ArrayList<>(response.body().getPayload());



                    adapterFavourites = new FavouritesAdapter(getActivity(),getFavouritePayloads);



                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerViewFavourites.setLayoutManager(mLayoutManager);
                    recyclerViewFavourites.setItemAnimator(new DefaultItemAnimator());

                    recyclerViewFavourites.setAdapter(adapterFavourites);


                    adapterFavourites.getToCartActivity(new FavouritesAdapter.FavouriteClickListenerToCart() {
                        @Override
                        public void GoToCartActivity(View v) {

                            Fragment fragment = null;
                            fragment = new ViewCartFragment();

                            if(fragment!=null){

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.mainFrame, fragment, "viewCartFragment")
                                        .addToBackStack(null).commit();
                                getActivity().overridePendingTransition(R.anim.slide_in, R.anim.nothing);

                            }
                        }
                    });


                    adapterFavourites.setOnItemClickListener(new FavouritesAdapter.FavouriteClickListener() {
                        @Override
                        public void onDelete(View view, int position) {
                            getFavouritePayloads.remove(position);
                            adapterFavourites.notifyItemRemoved(position);

                        }
                    });

                }
                else {

                    recyclerFavouritesFrame.setVisibility(View.GONE);
                    tvnoitems.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<GetFavouriteSuccess> call, Throwable t) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder1.setMessage(t.getMessage());
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });



                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });
*/
    }


}
