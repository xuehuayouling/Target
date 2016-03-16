package com.ruili.target.adapters;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.ruili.target.R;
import com.ruili.target.utils.Constant;
import com.ruili.target.utils.ImageTool;
import com.ruili.target.utils.TextUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> mPicPaths;
	private LayoutInflater mInflater;
	private RequestQueue mQueue;
	private BitmapCache mBitmapCache;

	public ImageAdapter(Context context, List<String> picPaths) {
		super();
		mContext = context;
		if (null == picPaths) {
			mPicPaths = new ArrayList<>();
		} else {
			mPicPaths = picPaths;
		}
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mQueue = Volley.newRequestQueue(context);
		mBitmapCache = new BitmapCache();
	}

	public void setPicPaths(List<String> picPaths) {
		if (null == picPaths) {
			mPicPaths = new ArrayList<>();
		} else {
			mPicPaths = picPaths;
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mPicPaths.size();
	}

	@Override
	public Object getItem(int position) {
		return mPicPaths.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		final String picPath = mPicPaths.get(position);
		if (null == convertView) {
			view = mInflater.inflate(R.layout.widget_pic_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.imgvPic = (ImageView) view.findViewById(R.id.imgv_photo_preview);
			view.setTag(holder);
		} else {
			view = convertView;
		}
		ViewHolder holder = new ViewHolder();
		holder = (ViewHolder) view.getTag();
		if (picPath.startsWith("http")) {
			downLoadPic(picPath, holder.imgvPic);
		} else {
			holder.imgvPic.setImageBitmap(getBitmap(picPath));
		}
		return view;
	}

	private void downLoadPic(String picUrl, ImageView imageView) {
		ImageLoader imageLoader = new ImageLoader(mQueue, mBitmapCache);  
		ImageListener listener = ImageLoader.getImageListener(imageView,  
		        R.drawable.ic_loading, R.drawable.ic_load_failed);
		imageLoader.get(Constant.getPicUrl(picUrl), listener);  
	}
	
	private Bitmap getBitmap(String picPath) {
		if (TextUtil.isEmpty(picPath)) {
			return null;
		}
		return ImageTool.compressBitmap(picPath, 1024, 768);
	}

	class ViewHolder {
		ImageView imgvPic;
	}

	public class BitmapCache implements ImageCache {  
		  
	    private LruCache<String, Bitmap> mCache;  
	  
	    public BitmapCache() {  
	        int maxSize = 5 * 1024 * 1024;  
	        mCache = new LruCache<String, Bitmap>(maxSize) {  
	            @Override  
	            protected int sizeOf(String key, Bitmap bitmap) {  
	                return bitmap.getRowBytes() * bitmap.getHeight();  
	            }  
	        };  
	    }  
	  
	    @Override  
	    public Bitmap getBitmap(String url) {  
	        return mCache.get(url);  
	    }  
	  
	    @Override  
	    public void putBitmap(String url, Bitmap bitmap) {  
	        mCache.put(url, bitmap);  
	    }  
	  
	}  
}
