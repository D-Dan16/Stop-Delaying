package procrastination.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.procrastination.R;

public class LinksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_links, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        CardView linkCard1 = view.findViewById(R.id.link_card_1);
        linkCard1.setOnClickListener(v -> openLink("https://www.youtube.com/watch?v=arj7oStGLkU"));

        CardView linkCard2 = view.findViewById(R.id.link_card_2);
        linkCard2.setOnClickListener(v -> openLink("https://www.mindtools.com/pages/article/newHTE_96.htm"));

        CardView linkCard3 = view.findViewById(R.id.link_card_3);
        linkCard3.setOnClickListener(v -> openLink("https://www.psychologicalscience.org/observer/the-science-of-procrastination"));

        CardView linkCard4 = view.findViewById(R.id.link_card_4);
        linkCard4.setOnClickListener(v -> openLink("https://www.forbes.com/sites/bryanrobinson/2021/01/04/the-2-minute-rule-can-help-you-stop-procrastinating-and-stick-to-your-goals/"));

        CardView linkCard5 = view.findViewById(R.id.link_card_5);
        linkCard5.setOnClickListener(v -> openLink("https://todoist.com/productivity-methods/pomodoro-technique"));
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
