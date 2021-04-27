package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import mmm.i7bachelor_smartsale.app.Adapter.ConversationAdapter;
import mmm.i7bachelor_smartsale.app.Adapter.MarketAdapter;
import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.ViewModels.MarketsViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.MarketsViewModelFactory;

public class MarketsActivity extends MainActivity implements MarketAdapter.IItemClickedListener {

    MarketsActivity context;
    MarketsViewModel viewModel;
    private RecyclerView itemList;
    private SearchView searchView;
    private MarketAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_markets);
        itemList = findViewById(R.id.rcvItems);
        adapter = new MarketAdapter(context);
        itemList.setLayoutManager(new LinearLayoutManager(context));
        itemList.setAdapter(adapter);
        searchView = findViewById(R.id.marketSearch);
        viewModel = new ViewModelProvider(context, new MarketsViewModelFactory(this.getApplicationContext())).get(MarketsViewModel.class);
        viewModel.getItems().observe(this, updateObserver);
    }

    //Tilføjet for at kunne starte på firebase - Hvis noget tilføjes til listen bliver det opdaget her
    //Inspired from https://medium.com/@atifmukhtar/recycler-view-with-mvvm-livedata-a1fd062d2280
    Observer<List<SalesItem>> updateObserver = new Observer<List<SalesItem>>() {
        @Override
        public void onChanged(List<SalesItem> UpdatedItems) {
            adapter.updateSalesItemList(UpdatedItems);
        }
    };

    @Override
    public void OnItemClicked(int index) {
        viewModel.SetSelected(index);
        startActivity(new Intent(this, DetailsActivity.class));
    }

    // https://stackoverflow.com/questions/48314254/how-to-get-text-from-searchview/48314286
    public void Search() {
    //search for items in database.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(),newText,Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
