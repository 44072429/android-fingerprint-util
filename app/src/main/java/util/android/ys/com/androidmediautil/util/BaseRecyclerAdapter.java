package util.android.ys.com.androidmediautil.util;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author illidantao
 * @date 2016/5/20 22:05
 */
public abstract class BaseRecyclerAdapter<T, HD extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<HD> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_FOORER = 2;

    protected ArrayList<T> mDatas = new ArrayList<T>();

    private View mHeaderView;
    private View mFooterView;

    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListenr;
    private boolean headerViewAllLine = true;
    private boolean footerViewAllLine = false;
    int headIs = 0;
    int footIs = 0;
    public LayoutInflater layoutInflater;
    public Context context;

    public BaseRecyclerAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener li) {
        mLongListenr = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        if (headerView != null) {
            headIs = 1;
            notifyItemInserted(0);
        } else {
            headIs = 0;
            notifyItemRemoved(0);
        }
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        if (footerView == null) {
            footIs = 0;
            notifyDataSetChanged();
        } else {
            footIs = 1;
            notifyDataSetChanged();
        }
    }

    public View getFooterView() {
        return mFooterView;
    }

    /**
     * @param headerViewAllLine true headView占据整行 false：可能不是整行
     */
    public void setHeaderViewAllLine(boolean headerViewAllLine) {
        this.headerViewAllLine = headerViewAllLine;
    }

    public void setFooterViewAllLine(boolean footerViewAllLine) {
        this.footerViewAllLine = footerViewAllLine;
    }

    public BaseRecyclerAdapter addDatas(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
        return this;
    }

    public BaseRecyclerAdapter setDatas(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
        return this;
    }

    public BaseRecyclerAdapter addData(T data) {
        mDatas.add(data);
        return this;
    }

    public ArrayList<T> getAllData() {

        return mDatas;
    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER;
        }
        if (mFooterView != null && position == getItemCount() - 1) {
            return TYPE_FOORER;
        }
        return TYPE_NORMAL;
    }


    @Override
    public HD onCreateViewHolder(ViewGroup parent, final int viewType) {
        if (viewType == TYPE_HEADER) {
            return onCreateHead(mHeaderView);
        }
        if (viewType == TYPE_FOORER) {
            return onCreateFoot(mFooterView);
        }
        return onCreateHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(HD viewHolder, final int position) {
        //头尾不需要数据绑定
        if (Config.DEBUG) {
            if (mListener != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(position, null);
                    }
                });
            }
        }
        if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOORER)
            return;

        final int pos = getRealPosition(viewHolder);
        if (mDatas.size() <= pos) {
            onBind(viewHolder, pos, null);
            return;
        }
        final T data = mDatas.get(pos);
        onBind(viewHolder, pos, data);

        if (mListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(pos, data);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mLongListenr!=null){
                        mLongListenr.onItemLongClick(pos, data);
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        if (!headerViewAllLine && !footerViewAllLine) return;

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();

        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (headerViewAllLine && getItemViewType(position) == TYPE_HEADER) || (footerViewAllLine && getItemViewType(position) == TYPE_FOORER)
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (null != mLoadMoreListener && !mIsLoadingMore && dy > 0 && hasNextPage) {
                    int lastVisiblePosition = getLastVisiblePosition(recyclerView);
                    //没头没尾，刚好是，每页多少个的整数倍，视为还有加载内容
                    if (lastVisiblePosition + 1 == getItemCount()) {
                        setLoadingMore(true);
                        mLoadMoreListener.onLoadMore(getItemCount());
                    }
                }
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(HD holder) {
        super.onViewAttachedToWindow(holder);
        if (!headerViewAllLine && !footerViewAllLine) return;
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && holder.getLayoutPosition() == 0) {
            if ((headerViewAllLine && getItemViewType(holder.getLayoutPosition()) == TYPE_HEADER) ||
                    footerViewAllLine && getItemViewType(holder.getLayoutPosition()) == TYPE_FOORER) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + headIs + footIs;
    }

    public int getDataSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**********************
     * 可能需要和必须实现的方法
     *************************/

    public abstract HD onCreateHolder(ViewGroup parent, final int viewType);

    public abstract void onBind(HD viewHolder, int realPosition, T data);

    public HD onCreateHead(View headView) {
        return null;
    }

    public HD onCreateFoot(View footView) {
        return null;
    }

    /**
     * positionk就是List的index
     * head和foot不处理点击事件
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {
        void onItemClick(int position, T data);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(int position, T data);
    }

    public void setHasNextPage(boolean nextPage) {
        hasNextPage = nextPage;
    }

    private boolean hasNextPage = true;
    private boolean mIsLoadingMore;
    private OnLoadMore mLoadMoreListener;


    public void setLoadingMore(boolean isLoadingMore) {
        mIsLoadingMore = isLoadingMore;
    }

    public void setOnLoadMoreListener(OnLoadMore onLoadMore) {
        this.mLoadMoreListener = onLoadMore;
    }

    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    public void removeLoadMoreListener(OnLoadMore onLoadMore) {
        this.mLoadMoreListener = null;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition(RecyclerView recyclerView) {
        int position;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = recyclerView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int size = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }


    public interface OnLoadMore {

        void onLoadMore(int start);

    }
}
