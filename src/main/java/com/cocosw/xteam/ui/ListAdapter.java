package com.cocosw.xteam.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.cocosw.xteam.R;
import com.cocosw.xteam.data.Emotion;

import java.util.ArrayList;
import java.util.List;

final class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

  public interface RepositoryClickListener {
    void onRepositoryClick(Emotion repository);
  }

  private final RepositoryClickListener repositoryClickListener;

  private List<Emotion> repositories = new ArrayList<>();

  public ListAdapter(RepositoryClickListener repositoryClickListener) {
    this.repositoryClickListener = repositoryClickListener;
    setHasStableIds(true);
  }


  public void replace(List<Emotion> repositories) {
    this.repositories = repositories;
    notifyDataSetChanged();
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
    EmotionItemView view = (EmotionItemView) LayoutInflater.from(viewGroup.getContext())
        .inflate(R.layout.trending_view_repository, viewGroup, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(ViewHolder viewHolder, int i) {
    viewHolder.bindTo(repositories.get(i));
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public int getItemCount() {
    return repositories.size();
  }

  public final class ViewHolder extends RecyclerView.ViewHolder {
    public final EmotionItemView itemView;

    public ViewHolder(EmotionItemView itemView) {
      super(itemView);
      this.itemView = itemView;
      this.itemView.setOnClickListener(v -> {
        Emotion repository = repositories.get(getAdapterPosition());
        repositoryClickListener.onRepositoryClick(repository);
      });
    }

    public void bindTo(Emotion repository) {
      itemView.bindTo(repository);
    }
  }
}
