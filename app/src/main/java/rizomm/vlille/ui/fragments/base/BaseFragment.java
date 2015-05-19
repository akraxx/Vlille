package rizomm.vlille.ui.fragments.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import rizomm.vlille.VlilleApplication;
import rizomm.vlille.ui.InjectableResource;

/**
 * Created by Maximilien on 22/02/2015.
 */
public abstract class BaseFragment extends Fragment implements InjectableResource {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Perform injection so that when this call returns all dependencies will be available for use.
        ((VlilleApplication) getActivity().getApplication()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(getContentView(), container, false);
        ButterKnife.inject(this, view);

        return onViewInflated(view, inflater, container, savedInstanceState);
    }

    public abstract View onViewInflated(View view,
                                        LayoutInflater inflater,
                                        @Nullable ViewGroup container,
                                        @Nullable Bundle savedInstanceState);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
