package stop_delaying.ui.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.procrastination.R;
import com.google.android.material.button.MaterialButton;

public class HomeFragment extends Fragment {

    private OnHomeFragmentInteractionListener mListener;

    public interface OnHomeFragmentInteractionListener {
        void onWhatIsProcrastinationClicked();
        void onLinksAndVideosClicked();
        void onTipsAndTricksClicked();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnWhatIsProcrastination = view.findViewById(R.id.btnWhatIsProcrastination);
        MaterialButton btnLinksAndVideos = view.findViewById(R.id.btnLinksAndVideos);
        MaterialButton btnTipsAndTricks = view.findViewById(R.id.btnTipsAndTricks);

        btnWhatIsProcrastination.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onWhatIsProcrastinationClicked();
            }
        });

        btnLinksAndVideos.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onLinksAndVideosClicked();
            }
        });

        btnTipsAndTricks.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onTipsAndTricksClicked();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
